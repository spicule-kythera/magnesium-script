package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

public abstract class ConditionalExpression extends Expression {
    protected  Wait condition = null;

    public ConditionalExpression(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    protected boolean conditionRunsWithoutException() {
        try {
            condition.execute();
            return true;
        } catch (Exception e) {
            LOG.warn("Condition failed due to the following:");
            e.printStackTrace();
            return false;
        }
    }
}
