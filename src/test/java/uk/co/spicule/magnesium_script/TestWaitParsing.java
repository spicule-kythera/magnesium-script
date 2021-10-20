package uk.co.spicule.magnesium_script;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.spicule.magnesium_script.expressions.Expression;
import uk.co.spicule.magnesium_script.expressions.Wait;

import java.util.HashMap;

public class TestWaitParsing {
    Wait operation;
    HashMap<String, Object> tokens;

    @BeforeEach
    void setUp() {
        operation = new Wait();
        tokens = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        operation = null;
        tokens = null;
    }

    @Test
    void noWait() {
        Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> operation.parse(tokens));
    }
}
