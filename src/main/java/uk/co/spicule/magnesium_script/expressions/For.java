package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@SuppressWarnings("unchecked")
public class For extends Expression implements Subroutine {
    enum Condition {
        EACH,
        ITERATOR;

        private static Condition stringToEnum(String name) throws InvalidExpressionSyntax {
            return Condition.valueOf(Expression.validateTypeClass(Condition.class, name));
        }
    }

    // Iteration type
    Condition type = null;

    // Element-iteration-specific details
    By locator = null;

    // Range-iteration-specific details
    int rangeMin = 0;
    int rangeMax;
    int rangeIncrement = 1;
    String iteratorName = "i";

    Program doBlock = new Program();

    public For(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Running `for` block with " + doBlock.size() + " items in the `do block`!");

        try{
            switch(type) {
                case EACH:
                    LOG.debug("Iterating on list of `" + locator + "` elements!");
                    return subExecuteForEach();
                case ITERATOR:
                    LOG.debug("Iterating on range (" + rangeMin + "..." + rangeMax+ ") by increments of " + rangeIncrement + "!");
                    return subExecuteForIterator();
                default:
                    throw new RuntimeException("FATAL: Invalid condition-type: " + type);
            }
        } catch (Break.StopIterationException e) {
            // Do Nothing
            LOG.warn("Stopped iteration");
            return null;
        }
    }

    private Object subExecuteForEach() throws Break.StopIterationException {
        List<WebElement> elements = driver.findElements(locator);
        LOG.debug("Iterating on " + elements.size() + " elements!");
        for(WebElement element: elements) {
            LOG.debug("Storing `" + iteratorName + "` as: " + element);
            this.context.put(iteratorName, element);
            doBlock.run();
        }

        return null;
    }

    private Object subExecuteForIterator() throws Break.StopIterationException {
        for(int i = rangeMin; i < rangeMax; i+=rangeIncrement) {
            LOG.debug("Storing `" + iteratorName + "` as: " + i);
            context.put(iteratorName, i);
            doBlock.run();
        }
        return null;
    }

    public For parse(Map<String, Object> tokens) throws InvalidExpressionSyntax,
                                                        Parser.InvalidExpressionType {
        // Assert the required fields
        assertRequiredField("for", String.class, tokens);
        assertRequiredField("do", ArrayList.class, tokens);

        // Process do block
        ArrayList<Map<String, Object>> runBlockTokens = (ArrayList<Map<String, Object>>) tokens.get("do");
        doBlock = new Parser(null).parse(driver, runBlockTokens, this);

        // Process condition-block based on specified condition-type
        type = Condition.stringToEnum(tokens.get("for").toString());
        switch(type) {
            case EACH:
                return subParseForEach(tokens);
            case ITERATOR:
                return subParseForIterator(tokens);
            default:
                throw new InvalidExpressionSyntax("FATAL: Invalid condition-type: " + type);
        }
    }

    private For subParseForEach(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("in", LinkedHashMap.class, tokens);

        // Assert required fields inside the in-block
        Map<String, Object> inBlock = (Map<String, Object>) tokens.get("in");
        assertRequiredField("locator-type", String.class, inBlock);
        assertRequiredField("locator", String.class, inBlock);

        // Get the custom iterator name, if any
        if(assertOptionalField("iterator-name", String.class, inBlock)) {
            String iteratorName = inBlock.get("iterator-name").toString();
            Matcher matcher = SendKeys.SPECIAL_CHARACTER_PATTERN.matcher(iteratorName);

            if(!matcher.find()) {
                throw new Expression.InvalidExpressionSyntax("iterator name: must match regex pattern: `" + SendKeys.SPECIAL_CHARACTER_PATTERN + "`");
            }
            this.iteratorName = iteratorName.substring(1 + matcher.start(), matcher.end() - 1);
        }

        // Populate the variable
        String locatorType = inBlock.get("locator-type").toString();
        String locator = inBlock.get("locator").toString();
        this.locator = (By) by(locatorType, locator);

        return this;
    }

    private For subParseForIterator(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("range", LinkedHashMap.class, tokens);

        // Process rangeBlock
        Map<String, Object> rangeBlock = (Map<String, Object>) tokens.get("range");
        assertRequiredField("max", Integer.class, rangeBlock);

        rangeMax = Integer.parseInt(rangeBlock.get("max").toString());

        if(assertOptionalField("min", Integer.class, rangeBlock)) {
            rangeMin = Integer.parseInt(rangeBlock.get("min").toString());
        }

        if(assertOptionalField("increment", Integer.class, rangeBlock)) {
            rangeIncrement = Integer.parseInt(rangeBlock.get("increment").toString());
        }

        if(assertOptionalField("iterator-name", String.class, rangeBlock)) {
            iteratorName = rangeBlock.get("iterator-name").toString().replaceAll("-", "_");
        }

        return this;
    }

    public List<String> getFlatStack() {
        return new ArrayList<>(doBlock.getSnapshots());
    }
}
