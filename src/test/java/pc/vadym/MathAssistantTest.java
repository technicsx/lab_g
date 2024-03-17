package pc.vadym;

import org.junit.Test;
import pc.vadym.exceptions.BracketsException;
import pc.vadym.exceptions.OperatorsException;
import pc.vadym.exceptions.TermsException;
import pc.vadym.helpers.MathExpressionHandler;

import static org.junit.Assert.assertThrows;

public class MathAssistantTest {
    @Test
    public void testIncorrectBracketsValidity() {
        assertThrows(BracketsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("((1+2)+1+5=(1+x)");
        });

        assertThrows(BracketsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("(1+(2-1)))*1+5");
        });

        assertThrows(BracketsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("(1+(2-1)))*1+)7");
        });
    }

    @Test
    public void testCorrectBracketsValidity() {
        try {
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("1+(2-1)*1+1");
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("((1-0)*2+1)*1+5");
            MathExpressionHandler.checkCorrectnessOfExpressionBracketsOrThrow("1+(2-(1-2))*(1+5)");
        } catch (BracketsException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIncorrectTermsValidity() {
        assertThrows(TermsException.class, () -> {
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("(1+y)+1+5=x");
        });

        assertThrows(TermsException.class, () -> {
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("(sdf+(2-1))*1+5");
        });

        assertThrows(TermsException.class, () -> {
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("(1+(2-1))*1.1.1+)7=12-x");
        });
    }

    @Test
    public void testCorrectTermsValidity() {
        try {
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("1+(2-1)*x+1=21");
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("((1-0)*2+1)*x+5=1.1");
            MathExpressionHandler.checkValidityOfExpressionTermsOrThrow("x+(2-(1-x))*(1+5)=0");
        } catch (TermsException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIncorrectOperatorsValidity() {
        assertThrows(OperatorsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("*(1+x)+1+5");
        });

        assertThrows(OperatorsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("(1++(2-1))*1+5");
        });

        assertThrows(OperatorsException.class, () -> {
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("(1+(2-1))*1+)7=12-x");
        });
    }

    @Test
    public void testCorrectOperatorsValidity() {
        try {
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("1+(2-1)*-x+1=21");
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("((1-0)*-2+1)*x+5=1.1");
            MathExpressionHandler.checkCorrectnessOfExpressionOperatorsOrThrow("x+(2-(1-x))*-(1+5)=0");
        } catch (OperatorsException e) {
            throw new RuntimeException(e);
        }
    }

}
