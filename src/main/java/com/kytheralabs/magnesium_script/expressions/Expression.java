package com.kytheralabs.magnesium_script.expressions;

import java.util.Map;
import org.openqa.selenium.WebDriver;

abstract public class Expression {
    public static class InvalidExpressionSyntax extends Exception {
        InvalidExpressionSyntax(String key) {
            super("Invalid syntax for expression `" + key + "`!");
        }

        InvalidExpressionSyntax(String key, String message) {
            super("Invalid syntax for expression `" + key + "`: " + message + "!");
        }
    }

    WebDriver driver;

    Expression(WebDriver driver) {
        this.driver = driver;
    }

    abstract public void eval();
    abstract public Expression parse(Map<String, Object> tokens) throws InvalidExpressionSyntax;
}