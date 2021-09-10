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

public class For extends Expression {
    enum Condition {
        EACH, ITERATOR
    }

    Condition conditionType = null;
    Map<String, Object> condition = new HashMap<>();
    Program runBlock = new Program();

    public For(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        switch (conditionType) {
            case EACH:
                return subExecuteForEach(condition);
            case ITERATOR:
                return subExecuteForIterator(condition);
            default:
                throw new UnknownError("This error was thrown because the for-block could not match an iterator condition. This error should never be thrown. If you are reading this, logic as we know it has failed to work. God help you.");
        }
    }

    private Object subExecuteForEach(Map<String, Object> tokens) {
        List<WebElement> elements = driver.findElements(by(tokens.get("locatorType").toString(), tokens.get("locator").toString()));
        for(WebElement element : elements) {
            context.put(tokens.get("iterator").toString(), element);
            runBlock.run();
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
        subParseFor((Map<String, Object>) tokens.get("for"));

        // Process do block
        ArrayList<Map<String, Object>> runBlockTokens = (ArrayList<Map<String, Object>>) tokens.get("do");
        runBlock = new Parser(null).parse(driver, runBlockTokens, this);

        return this;
    }

    private void subParseFor(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Check for required fields
        assertRequiredField("for", "condition", String.class, tokens);

        // Populate the condition type and sub-parse accordingly
        conditionType = Condition.valueOf(tokens.get("condition").toString().toUpperCase());
        switch(conditionType) {
            case EACH:
                subParseForEach(tokens);
                break;
            case ITERATOR:
                subParseForIterator(tokens);
                break;
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
}
