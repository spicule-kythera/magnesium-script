package uk.co.spicule.magnesium_script;

import uk.co.spicule.magnesium_script.expressions.Expression;
import uk.co.spicule.magnesium_script.expressions.If;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestIfParsing {
    If operation;
    HashMap<String, Object> tokens;

    @BeforeEach
    void setUp() {
        operation = new If(null, null);
        tokens = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        operation = null;
        tokens = null;
    }

    @Test
    void noIfBlock() {
        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidIfBlockType() {
        tokens.put("if", "this is not a map");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void noIfBlockLocator() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void noIfBlockElement() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void noIfBlockCondition() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");
        ifBlock.put("element", ".someClassName");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void noIfBlockValue() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");
        ifBlock.put("element", ".someClassName");
        ifBlock.put("condition", "equals");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void noThenBlock() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");
        ifBlock.put("element", ".someClassName");
        ifBlock.put("condition", "equals");
        ifBlock.put("value", "Some value");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidThenBlockType() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");
        ifBlock.put("element", ".someClassName");
        ifBlock.put("condition", "equals");
        ifBlock.put("value", "some value");
        tokens.put("then", "this is not a block");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidElseStatement() {
        HashMap<String, Object> ifBlock = new HashMap<>();
        ArrayList<HashMap<String, Object>> thenBlock = new ArrayList<>();
        HashMap<String, Object> op = new HashMap<>();
        op.put("get", "https://duckduckgo.com");
        thenBlock.add(op);

        tokens.put("if", ifBlock);
        ifBlock.put("locator", "class");
        ifBlock.put("element", ".someClassName");
        ifBlock.put("condition", "equals");
        ifBlock.put("value", "some value");
        tokens.put("then", thenBlock);
        tokens.put("else", "this is not an else block");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void validIfStatement() {
        LinkedHashMap<String, Object> ifBlock = new LinkedHashMap<>();
        ArrayList<HashMap<String, Object>> thenBlock = new ArrayList<>();
        LinkedHashMap<String, Object> op = new LinkedHashMap<>();
        op.put("get", "https://duckduckgo.com");
        thenBlock.add(op);

        tokens.put("if", ifBlock);
        ifBlock.put("wait", 10);
        ifBlock.put("until", "element-exists");
        ifBlock.put("locator", "someRandomID");
        ifBlock.put("locator-type", "id");
        tokens.put("then", thenBlock);

        Assertions.assertDoesNotThrow(() -> {
            operation.parse(tokens);
        });
    }
}
