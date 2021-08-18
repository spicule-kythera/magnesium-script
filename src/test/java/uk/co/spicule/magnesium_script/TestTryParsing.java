package uk.co.spicule.magnesium_script;

import uk.co.spicule.magnesium_script.expressions.Expression;
import uk.co.spicule.magnesium_script.expressions.Try;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class TestTryParsing {
    Try operation;
    HashMap<String, Object> tokens;

    @BeforeEach
    void setUp() {
        operation = new Try(null, null);
        tokens = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        operation = null;
        tokens = null;
    }

    @Test
    void noTryBlock() {
        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
            operation.parse(tokens);
        });
    }
}
