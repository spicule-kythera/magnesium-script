package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import java.util.Map;

public class Snapshot extends Expression {

    public Snapshot(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public String execute() {
        LOG.debug("Taking a snapshot of page: " + driver.getCurrentUrl());

        return driver.getPageSource();
    }

    public Snapshot parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        return this;
    }
}