package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Break extends Expression {
    public class StopIterationException extends Exception {
        public StopIterationException() {
            super("FATAL: A break operation was called outside of a loop!");
        }
    }

    public Break(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() throws StopIterationException {
        throw new StopIterationException();
    }

    public Break parse(Map<String, Object> tokens) {
        return this;
    }
}
