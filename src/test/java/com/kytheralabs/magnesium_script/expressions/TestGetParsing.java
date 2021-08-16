package com.kytheralabs.magnesium_script.expressions;

import com.kytheralabs.magnesium_script.expressions.expressions.Expression;
import com.kytheralabs.magnesium_script.expressions.expressions.Get;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class TestGetParsing {
    Get operation;
    HashMap<String, Object> tokens;

    @BeforeEach
    void setUp() {
        operation = new Get(null, null);
        tokens = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        operation = null;
        tokens = null;
    }

    @Test
    void noGet(){
        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidURL() {
        tokens.put("get", "not a valid url!");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void validURL() {
        tokens.put("get", "https://duckduckgo.com");

        Assertions.assertDoesNotThrow(() -> {
            operation.parse(tokens);
        });
    }
}