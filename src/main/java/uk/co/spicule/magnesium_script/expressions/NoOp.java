package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class NoOp extends  Expression {
    public NoOp(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Performing no operation!");
        return null;
    }

    public NoOp parse(Map<String, Object> tokens) {
        return this;
    }
}
