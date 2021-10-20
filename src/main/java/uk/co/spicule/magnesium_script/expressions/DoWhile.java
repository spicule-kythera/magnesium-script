package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DoWhile extends ConditionalExpression implements Subroutine {
    Program doBlock = null;

    public DoWhile(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("DO " + doBlock.toString() + " WHILE " + condition.toString());

        try {
            do {
                doBlock.run();
            } while(conditionRunsWithoutException());
        } catch (Break.StopIterationException e) {
            // Do nothing
        }

        return null;
    }

    public DoWhile parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        // Assert the required fields
        assertRequiredField("do", ArrayList.class, tokens);
        assertRequiredField("while", LinkedHashMap.class, tokens);

        // Populate the condition as a wait-block
        condition = new Wait(driver, this).parse((Map<String, Object>) tokens.get("while"));

        // Block sub-parser
        Parser subParser = new Parser(null);

        // Populate the do block
        doBlock = subParser.parse(driver, (ArrayList<Map<String, Object>>) tokens.get("do"), this);

        return this;
    }

    public List<String> getFlatStack() {
        return new ArrayList<>(doBlock.getSnapshots());
    }
}
