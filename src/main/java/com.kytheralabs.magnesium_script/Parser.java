package com.kytheralabs.magnesium_script;

import java.util.Map;
import java.util.ArrayList;
import org.openqa.selenium.WebDriver;
import com.kytheralabs.magnesium_script.expressions.Get;
import com.kytheralabs.magnesium_script.expressions.Alert;
import com.kytheralabs.magnesium_script.expressions.Expression.InvalidExpressionSyntax;

public class Parser {
    Map<String, Object> tokens;

    public static class InvalidExpressionType extends Exception {
        InvalidExpressionType(String expression) {
            super("Invalid expression type specified: `" + expression +  "`!");
        }

        InvalidExpressionType(String expression, String message) {
            super("Invalid expression type specified: `" + expression +  "`: " + message + "!");
        }
    }

    public Parser(Map<String, Object> tokens) {
        this.tokens = tokens;
    }

    public Program parse(WebDriver driver) throws InvalidExpressionType, InvalidExpressionSyntax {
        Program program = new Program();

        if(!tokens.containsKey("run")){
            throw new InvalidExpressionType("run", "MagnesiumScript requires the script to be placed under the `run` block");
        }

        ArrayList<Map<String, Object>> runBlock = (ArrayList<Map<String, Object>>) tokens.get("run");

        for (Map<String, Object> instruction : runBlock) {
            if(instruction.containsKey("alert")) {
                program.addInstruction(new Alert(driver).parse(instruction));
            } else if(instruction.containsKey("get")) {
                program.addInstruction(new Get(driver).parse(instruction));
            }else {
                throw new InvalidExpressionType(instruction.toString());
            }
        }
        return program;
    }
}