package com.kytheralabs.magnesium_script.expressions.expressions;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Get extends Expression {
    URL url = null;

    public Get(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public void execute() {
        driver.get(url.toString());
    }

    public Get parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        if(!tokens.containsKey("get")) {
            throw new InvalidExpressionSyntax("get", "Must contain `get` field, specifying a valid URL!");
        }

        String urlToken = tokens.get("get").toString();

        try {
            url = new URL(urlToken);
        } catch (MalformedURLException e) {
            throw new InvalidExpressionSyntax(e);
        }
        return this;
    }
}