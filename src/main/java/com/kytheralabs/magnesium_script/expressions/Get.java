package com.kytheralabs.magnesium_script.expressions;

import java.util.Map;
import org.openqa.selenium.WebDriver;

public class Get extends Expression {
    String url = null;

    public Get(WebDriver driver) {
        super(driver);
    }

    public void eval() {
        driver.get(url);
    }

    public Get parse(Map<String, Object> tokens) throws InvalidExpressionSyntax{
        url = tokens.get("get").toString();
        return this;
    }
}