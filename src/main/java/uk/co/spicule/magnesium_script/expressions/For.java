package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class For extends Expression implements Subroutine {
    enum Condition {
        EACH,
        ITERATOR;

        protected static Condition stringToEnum(String name) throws InvalidExpressionSyntax {
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
                    return subExecuteForIterator(condition);
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

    private Object subExecuteForIterator(Map<String, Object> tokens) {
        // TODO: fill
        return null;
    }

    public For parse(Map<String, Object> tokens) throws InvalidExpressionSyntax,
                                                        Parser.InvalidExpressionType {
        // Check for required fields
        Map<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("for", Map.class);
        requiredFields.put("do", List.class);
        assertRequiredFields("for", requiredFields, tokens);

        // Process condition block conditionally
        // Populate the condition type and sub-parse accordingly
        conditionType = Condition.stringToEnum(tokens.get("condition").toString());
        switch(conditionType) {
            case EACH:
                subParseForEach(tokens);
                break;
            case ITERATOR:
                subParseForIterator(tokens);
                break;
            default:
                throw new InvalidExpressionSyntax("FATAL: Invalid condition-type: " + conditionType);
        }

        // Process do block
        ArrayList<Map<String, Object>> runBlockTokens = (ArrayList<Map<String, Object>>) tokens.get("do");
        doBlock = new Parser(null).parse(driver, runBlockTokens, this);

        return this;
    }

    private void subParseFor(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Check for required fields
        assertRequiredField("for", "condition", String.class, tokens);

        // Populate the condition type and sub-parse accordingly
        conditionType = Condition.stringToEnum(tokens.get("condition").toString());
        switch(conditionType) {
            case EACH:
                subParseForEach(tokens);
                break;
            case ITERATOR:
                subParseForIterator(tokens);
                break;
            default:
                throw new InvalidExpressionSyntax("FATAL: Invalid condition-type: " + conditionType);
        }
    }

    private void subParseForEach(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Check for required fields
        Map<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("locator", String.class);
        requiredFields.put("locatorType", String.class);
        requiredFields.put("iterator", String.class);
        assertRequiredFields("for-condition: each", requiredFields, tokens);

        // Load the condition map
        condition.put("locatorType", tokens.get("locatorType"));
        condition.put("locator", tokens.get("locator"));
        condition.put("iterator", tokens.get("iterator"));
    }

    private void subParseForIterator(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // TODO: fill
    }

    public List<String> getFlatStack() {
        ArrayList<String> stack = new ArrayList<>();
        stack.addAll(doBlock.getSnapshots());
        return stack;
    }
}
