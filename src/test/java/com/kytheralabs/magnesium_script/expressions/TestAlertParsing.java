package com.kytheralabs.magnesium_script.expressions;

import com.kytheralabs.magnesium_script.expressions.expressions.Alert;
import com.kytheralabs.magnesium_script.expressions.expressions.Expression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class TestAlertParsing {
    Alert operation;
    HashMap<String, Object> tokens;

    @BeforeEach
    void setUp() {
        operation = new Alert(null, null);
        tokens = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        operation = null;
        tokens = null;
    }

    @Test
    void noAlert(){
        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidAlertType() {
        tokens.put("alert", "non-existant alert-type");

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void validAcceptAlert() {
        tokens.put("alert", "Accept");

        Assertions.assertDoesNotThrow(() -> {
            operation.parse(tokens);
        });
    }

    @Test
    void invalidTimeout() {
        tokens.put("alert", "accept");
        tokens.put("timeout", 10.1);

        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }

    @Test
    void validTimeout() {
        tokens.put("alert", "accept");
        tokens.put("timeout", 10);

        Assertions.assertDoesNotThrow(() -> {
            operation.parse(tokens);
        });
    }
}