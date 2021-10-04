package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Break extends Expression {
    public static class StopIterationException extends Exception {
        public StopIterationException() {
            super("FATAL: A break operation was called and uncaught by an iterator!");
        }
    }

    public Break(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() throws StopIterationException {
        throw new StopIterationException();
    }

    public Break parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Guarantee that the expression parent is an iterator expression
        if(parent == null || !(parent instanceof DoWhile || parent instanceof  For)) {
            throw new InvalidExpressionSyntax("Break operation is not allowed to be called outside of iterator expressions!");
        }
        return this;
    }
}
