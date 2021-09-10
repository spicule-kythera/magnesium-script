package uk.co.spicule.magnesium_script.expressions;

import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class If extends Expression {
    enum PredicateType {
        EQUALS, CONTAINS
    }

    PredicateType predicateType = null;
    String locator = null;
    String element = null;
    String value = null;
    Program thenBlock = null;
    Program elseBlock = null;

    public If(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        boolean predicate;

        switch (predicateType) {
            case EQUALS:
                predicate = true;
                break;
            case CONTAINS:
                predicate = false;
                break;
            default:
                throw new InvalidArgumentException("Internal error: invalid predicate: " + predicateType.toString());
        }

        if(predicate) {
            thenBlock.run();
        } else if(elseBlock != null) {
            elseBlock.run();
        }
        return null;
    }

    public If parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        // Process predicate
        if(!tokens.containsKey("if")) {
            throw new InvalidExpressionSyntax("if", "`if` operation must contain predicate");
        }

        if(!(tokens.get("if") instanceof Map)) {
            throw new InvalidExpressionSyntax("if", "Expected `if` block to be a Map! Found: " + tokens.get("if").getClass());
        }

        // Process if block fields
        Map<String, Object> ifBlockTokens = (Map<String, Object>) tokens.get("if");
        if(!ifBlockTokens.containsKey("locator")) {
            throw new InvalidExpressionSyntax("if", "`locator` field is required");
        } else if(!ifBlockTokens.containsKey("element")) {
            throw new InvalidExpressionSyntax("if", "`element` field is required");
        } else if(!ifBlockTokens.containsKey("condition")) {
            throw new InvalidExpressionSyntax("if", "`condition` field is required");
        } else if(!ifBlockTokens.containsKey("value")) {
            throw new InvalidExpressionSyntax("if", "`value` field is required");
        }

        // Process condition type
        String conditionToken = ifBlockTokens.get("condition").toString();
        try {
            predicateType = PredicateType.valueOf(conditionToken.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidExpressionSyntax("if", "Invalid predicate type `" + conditionToken + "`! Token must be one of the following: " + Arrays.toString(PredicateType.values()));
        }

        locator = ifBlockTokens.get("locator").toString();
        element = ifBlockTokens.get("element").toString();
        value = ifBlockTokens.get("value").toString();

        // Block sub-parser
        Parser parser = new Parser(null);

        // Process then block
        if(!tokens.containsKey("then")) {
            throw new InvalidExpressionSyntax("if", "`then` field is required");
        } else if(!(tokens.get("then") instanceof List && ((List<?>) tokens.get("then")).get(0) instanceof Map)){
            throw new InvalidExpressionSyntax("if", "Expected `then` block to be a List<Map>, found: " + tokens.get("then").getClass());
        }
        List<Map<String, Object>> thenBlockTokens = (List<Map<String, Object>>) tokens.get("then");
        thenBlock = parser.parse(driver, thenBlockTokens);

        // Process else block
        if(tokens.containsKey("else")) {
            if(!(tokens.get("else") instanceof List && ((List<?>) tokens.get("else")).get(0) instanceof Map)){
                throw new InvalidExpressionSyntax("if", "Expected `else` block to be a List<Map>, found: " + tokens.get("else").getClass());
            }

            List<Map<String, Object>> elseBlockTokens = (List<Map<String, Object>>) tokens.get("else");
            elseBlock = parser.parse(driver, elseBlockTokens);
        }

        return this;
    }
}
