package com.kytheralabs.magnesium_script.expressions.expressions;

import com.kytheralabs.magnesium_script.expressions.Parser;
import com.kytheralabs.magnesium_script.expressions.Program;
import org.openqa.selenium.WebDriver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class If extends Expression {
    enum PredicateType {
        EQUALS, CONTAINS
    }

    Map<String, Object> ifBlock = new HashMap<String, Object>();
    Program thenBlock = null;

    If(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public void execute() {

    }

    public If parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        // Process predicate
        if(!tokens.containsKey("if")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain predicate!");
        }

        if(!(tokens.get("if") instanceof Map)) {
            throw new InvalidExpressionSyntax("if", "Invalid type passed to `if`: " + tokens.get("if").getClass());
        }

        // Process if block fields
        Map<String, Object> ifBlockTokens = (Map<String, Object>) tokens.get("if");
        if(!ifBlockTokens.containsKey("locator")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain `locator` field!");
        } else if(!ifBlockTokens.containsKey("element")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain `element` field!");
        } else if(!ifBlockTokens.containsKey("condition+") && !ifBlockTokens.containsKey("contains")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain a predicate of either `equals` or `contains`!");
        }

        ifBlock.put("locator", ifBlockTokens.get("locator"));
        ifBlock.put("element", ifBlockTokens.get("element"));
        ifBlock.put("predicate", PredicateType.valueOf(ifBlockTokens.get("eq").toString()));

        // Process then block
        if(!tokens.containsKey("then")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain a `then` block!");
        }
        Parser parser = new Parser(null);
        List<Map<String, Object>> thenBlockTokens = (List<Map<String, Object>>) tokens.get("then");
        thenBlock = parser.parse(driver, thenBlockTokens);

        return this;
    }
}
