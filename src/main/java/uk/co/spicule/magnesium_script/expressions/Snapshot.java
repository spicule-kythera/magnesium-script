package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Snapshot extends Expression {
    boolean takeSnapshot;

    public Snapshot(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public String execute() {
        if(takeSnapshot){
            return driver.getPageSource();
        } else {
            return "<p>no snapshot was taken for page \"" + driver.getCurrentUrl() + "\"</p>";
        }
    }

    public Snapshot parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        takeSnapshot = Boolean.getBoolean(tokens.get("snapshot").toString());
        return this;
    }
}
