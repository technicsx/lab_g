package pc.vadym.helpers;

import pc.vadym.database.DatabaseHandler;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class MathExpressionHandler {
    private static String TERM_REGEX = "\\d+(\\.\\d+)?|x";
    private static String ALLOWED_OPERATORS = "*+-/";
    private static String NUMBER_DIGITS = "0123456789";
    private static String SPACES = "\\s+";

    public static boolean isRootOfFullExpression(int expressionId, String fullExpression, double root) throws Exception {
        String[] expressionSplits = fullExpression.split("=");
        String rootStr = Double.toString(root);

        String leftExpression = expressionSplits[0].replaceAll("x", rootStr);
        String rightExpression = expressionSplits[1].replaceAll("x", rootStr);

        double leftResultValue = evalExpressionPart(leftExpression);
        double rightResultValue = evalExpressionPart(rightExpression);

        boolean isRoot = Math.abs(leftResultValue - rightResultValue) <= Math.pow(10, -9);

        System.out.println("leftResultValue " + leftResultValue + ", rightResultValue " + rightResultValue);
        if (isRoot) {
            int rootId = DatabaseHandler.storeRootOfExpression(root);
            DatabaseHandler.connectedInsertedExpressionAndRootByIds(expressionId, rootId);
        }

        return isRoot;
    }

    private static double evalExpressionPart(String expressionPart) throws Exception {
        Stack<Character> operatorsWithBraces = new Stack<>();
        Stack<Double> numbers = new Stack<>();

        for (int i = 0; i < expressionPart.length(); ++i) {
            char currentChar = expressionPart.charAt(i);
            if (NUMBER_DIGITS.indexOf(currentChar) != -1) {
                String possibleNumber = "";
                int pos = i;
                while (pos < expressionPart.length() && (NUMBER_DIGITS.indexOf(expressionPart.charAt(pos)) != -1 || expressionPart.charAt(pos) == '.')) {
                    possibleNumber += expressionPart.charAt(pos);
                    pos++;
                }
                numbers.push(Double.parseDouble(possibleNumber));
                i = pos - 1;
            } else if (currentChar == '(') {
                operatorsWithBraces.push(currentChar);
            } else if (currentChar == ')') {
                while (operatorsWithBraces.peek() != '(') {
                    double rightOperand = numbers.pop();
                    double leftOperand = numbers.pop();
                    char operatorInBetween = operatorsWithBraces.pop();
                    numbers.push(performOperation(
                            leftOperand,
                            rightOperand, operatorInBetween));
                }
                operatorsWithBraces.pop();
            } else if (ALLOWED_OPERATORS.indexOf(currentChar) != -1) {
                while (!operatorsWithBraces.isEmpty() && previousOperatorMoreImportantThanCurrent(operatorsWithBraces.peek(), currentChar)) {
                    double rightOperand = numbers.pop();
                    double leftOperand = numbers.pop();
                    char operatorInBetween = operatorsWithBraces.pop();
                    numbers.push(performOperation(leftOperand, rightOperand, operatorInBetween));
                    operatorsWithBraces.push(currentChar);
                }
                operatorsWithBraces.push(currentChar);
            }
        }

        while (!operatorsWithBraces.isEmpty()) {
            double rightOperand = numbers.pop();
            double leftOperand = numbers.pop();
            char operatorInBetween = operatorsWithBraces.pop();

            numbers.push(performOperation(leftOperand,
                    rightOperand, operatorInBetween));
        }

        return numbers.pop();
    }


    private static boolean previousOperatorMoreImportantThanCurrent(char prevOperator, char currentOperator) {
        if (((currentOperator == '*' || currentOperator == '/') && (prevOperator == '+' || prevOperator == '-'))
                || ((prevOperator == '*' && currentOperator == '*') || (prevOperator == '/' && currentOperator == '/'))) {
            return false;
        }

        return prevOperator != '(' && prevOperator != ')';
    }

    private static double performOperation(double firstOperand, double secondOperand, char operation) throws Exception {
        return switch (operation) {
            case '*' -> firstOperand * secondOperand;
            case '/' -> {
                if (secondOperand == 0) {
                    throw new Exception();
                }
                yield firstOperand / secondOperand;
            }
            case '+' -> firstOperand + secondOperand;
            case '-' -> firstOperand - secondOperand;
            default -> throw new Exception();
        };
    }

    public static void checkValidityOfExpressionTermsOrThrow(String expression) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-/*=)(");
        Pattern termPattern = Pattern.compile(TERM_REGEX);

        if (expression.indexOf('x') == -1) {
            throw new Exception("No unknown variable x found");
        }

        if (expression.indexOf('=') == -1) {
            throw new Exception("It is not a proper equation, = needed");
        }

        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            if (!termPattern.matcher(nextToken).matches()) {
                throw new Exception("Incorrect term found: " + nextToken);
            }
        }
    }

    public static void checkCorrectnessOfExpressionBracketsOrThrow(String expression) throws Exception {
        Stack<Character> brackets = new Stack<>();

        for (int i = 0; i < expression.length(); ++i) {

            char currentChar = expression.charAt(i);
            if (currentChar == '(') {
                brackets.push(currentChar);
            }

            if (currentChar == ')') {
                char poppedBracket;
                try {
                    poppedBracket = brackets.pop();
                } catch (EmptyStackException e) {
                    throw new Exception("Incorrect brackets, stack was empty on checking of opening bracket");
                }
                if (poppedBracket != '(') {
                    throw new Exception("Incorrect brackets, popped bracket was not an opening one");
                }
            }
        }

        if (!brackets.isEmpty()) {
            throw new Exception("Incorrect brackets");
        }
    }

    public static void checkCorrectnessOfExpressionOperatorsOrThrow(String expression) throws Exception {
        for (int i = 0; i < expression.length() - 1; ++i) {
            char currentOperator = expression.charAt(i);

            if (ALLOWED_OPERATORS.indexOf((expression.charAt(i))) != -1) {
                if (currentOperator != '-' && i == 0) {
                    throw new Exception("Incorrect operators usage on start of expression, only - is allowed");
                }

                char nextChar = expression.charAt(i + 1);
                if (ALLOWED_OPERATORS.indexOf(nextChar) != -1 && nextChar != '-') {
                    throw new Exception("Incorrect operators usage, more than one operator was found next to each other: " + currentOperator + nextChar);
                }
            }
        }
    }

    public static String removeAllSpaces(String expression) {
        return expression.replaceAll(SPACES, "");
    }
}
