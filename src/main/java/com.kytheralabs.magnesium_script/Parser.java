package com.kytheralabs.magnesium_script;

import com.kytheralabs.magnesium_script.expressions.Alert;
import com.kytheralabs.magnesium_script.expressions.Expression.InvalidExpressionSyntax;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Parser {
    Map<String, String> tokens;

    public static class InvalidExpressionType extends Exception {
        InvalidExpressionType(String expression) {
            super("Invalid expression type specified: `" + expression +  "`");
        }
    }

    public Parser(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    public Program parse(WebDriver driver) throws InvalidExpressionType, InvalidExpressionSyntax {
        Program program = new Program();
        for (Map.Entry<String, String> token : tokens.entrySet()) {
            String key = token.getKey().toLowerCase();
            switch (key) {
                case "alert":
                    program.addInstruction(new Alert(driver).parse(tokens));
                    break;
                case "snapshot":
//                    program.addInstruction(new Expression());
                    break;
                default:
                    throw new InvalidExpressionType(key);
            }
        }
        return program;
    }
}