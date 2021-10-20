package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class For extends Expression implements Subroutine {
    enum Condition {
        EACH,
        ITERATOR;

        private static Condition stringToEnum(String name) throws InvalidExpressionSyntax {
            return Condition.valueOf(Expression.validateTypeClass(Condition.class, name));
        }
    }

    Condition conditionType = null;
    Map<String, Object> condition = new HashMap<>();
    Program doBlock = new Program();

    public For(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Resolving expression: `" + this.getClass() + "`!");

        try {
            switch (conditionType) {
                case EACH:
                    return subExecuteForEach(condition);
                case ITERATOR:
                    return null;
                default:
                    throw new RuntimeException("FATAL: Invalid condition-type: " + conditionType);
            }
        } catch (Break.StopIterationException e) {
            // Do nothing
            LOG.warn("Exiting for expression due to break!");
        }

        return null;
    }

    private Object subExecuteForEach(Map<String, Object> tokens) throws Break.StopIterationException {
        List<WebElement> elements = driver.findElements(Expression.by(tokens.get("locatorType").toString(), tokens.get("locator").toString()));
        for(WebElement element : elements) {
            context.put(tokens.get("iterator").toString(), element);
            doBlock.appendContext(context);
            doBlock.run();
        }
        return null;
    }

    public For parse(Map<String, Object> tokens) throws InvalidExpressionSyntax,
                                                        Parser.InvalidExpressionType {
        // Assert the required fields
        assertRequiredField("for", Map.class, tokens);
        assertRequiredField("do", List.class, tokens);

        // Process condition-block based on specified condition-type
        conditionType = Condition.stringToEnum(tokens.get("condition").toString());
        switch(conditionType) {
            case EACH:
                subParseForEach(tokens);
                break;
            case ITERATOR:
                throw new InvalidExpressionSyntax("Iteration with `for` is not yet supported!");
            default:
                throw new InvalidExpressionSyntax("FATAL: Invalid condition-type: " + conditionType);
        }

        // Process do block
        ArrayList<Map<String, Object>> runBlockTokens = (ArrayList<Map<String, Object>>) tokens.get("do");
        doBlock = new Parser(null).parse(driver, runBlockTokens, this);

        return this;
    }

    private void subParseForEach(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Check for required fields
        assertRequiredField("locator", String.class, tokens);
        assertRequiredField("locatorType", String.class, tokens);
        assertRequiredField("iterator", String.class, tokens);

        // Load the condition map
        condition.put("locatorType", tokens.get("locatorType"));
        condition.put("locator", tokens.get("locator"));
        condition.put("iterator", tokens.get("iterator"));
    }

    public List<String> getFlatStack() {
        return new ArrayList<>(doBlock.getSnapshots());
    }
}
