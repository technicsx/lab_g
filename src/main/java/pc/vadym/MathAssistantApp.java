package pc.vadym;

import pc.vadym.database.DatabaseConnection;
import pc.vadym.database.DatabaseHandler;
import pc.vadym.helpers.MathExpressionHandler;
import java.util.Arrays;
import java.util.Scanner;

public class MathAssistantApp {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Greetings from the MathAssistant!");

            while (true) {
                System.out.println("\nChoose your option:\n" +
                        "Enter 1 - to input expression;\n" +
                        "Enter 2 - to input search of root;\n" +
                        "Enter 3 - to stop the app.\n" +
                        "Your pick:");

                int optionPick = scanner.nextInt();

                if (optionPick == 1) {
                    System.out.println("Waiting for your expression to be entered:");
                    scanner.nextLine();
                    String expressionInput = MathExpressionHandler.removeAllSpaces(scanner.nextLine());
                    System.out.println("Your expression: " + expressionInput);

                    MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow(expressionInput);
                    MathExpressionHandler.checkValidityOfExpressionTermsOrThrow(expressionInput);
                    MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow(expressionInput);
                    System.out.println("Given expression is valid.");
                    int expressionId = DatabaseHandler.storeFullExpression(expressionInput);


                    System.out.println("Waiting for your x variable to be entered:");
                    double root = scanner.nextDouble();
                    boolean result = MathExpressionHandler.isRootOfFullExpression(expressionId, expressionInput, root);

                    if (result) {
                        System.out.println("x = " + root + " IS a root of expression: " + expressionInput);
                    } else {
                        System.out.println("x = " + root + " IS NOT a root of expression: " + expressionInput);
                    }
                } else if (optionPick == 2) {
                    while (true) {
                        System.out.println("\nChoose your search option:\n" +
                                "Enter 1 - by root values;\n" +
                                "Enter 2 - only one root;\n" +
                                "Enter 3 - return.\n" +
                                "Your pick:");

                        optionPick = scanner.nextInt();

                        if (optionPick == 1) {
                            System.out.print("Input root value (divide them by ','):");
                            scanner.nextLine();
                            String values = MathExpressionHandler.removeAllSpaces(scanner.nextLine());

                            double[] roots = Arrays.stream(values.split(",")).mapToDouble(obj -> Double.parseDouble(obj.toString()))
                                    .toArray();

                            DatabaseHandler.findAllExpressionThatHaveAnyOfTheRoot(roots);
                        } else if (optionPick == 2) {
                            System.out.println("Expressions with only one root:");
                            DatabaseHandler.findAllExpressionThatHaveOnlyOneRoot();

                        } else if (optionPick == 3) {
                            System.out.println("Returning to above menu.");
                            break;
                        } else {
                            System.out.println("Sorry. You have to pick one from the suggested options above for search.");
                        }
                    }
                } else if (optionPick == 3) {
                    DatabaseConnection.getInstance().closeExistingConnection();
                    scanner.close();
                    break;
                } else {
                    System.out.println("Sorry. You have to pick one from the suggested options above.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}