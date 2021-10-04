package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;
import java.util.*;

@SuppressWarnings("unchecked")
public class Try extends Expression implements Subroutine {
    Program tryBlock = null;
    Map<String, Program> catchBlocks = new HashMap<>();
    Program finallyBlock = null;

    public Try(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() throws Break.StopIterationException {
        LOG.debug("Trying to run block " + ((tryBlock != null) ? tryBlock.toString() : null ) + ", will catch these errors: " + catchBlocks.keySet() + ", will finally run block " + ((finallyBlock != null) ? finallyBlock.toString() : null) + ")");

        try {
            tryBlock.run();
        } catch (Exception e) {
            String slugName = Expression.classPathToSlugName(e); // Convert the whole exception name to just the slug

            // Run the exception handler iff it's found in the catch block.
            if(catchBlocks.containsKey(slugName)) {
                catchBlocks.get(slugName).run();
            } else { // Re-Throw the exception
                throw e;
            }
        } finally {
            // If finally block was specified, run it
            if(finallyBlock != null) {
                finallyBlock.run();
            }
        }
        return null;
    }

    public Try parse(Map<String, Object> tokens) throws Parser.InvalidExpressionType, InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("try", ArrayList.class, tokens);
        assertRequiredField("catch", LinkedHashMap.class, tokens);

        // Populate finally block if it was specified
        List<Map<String, Object>> finallyBlockTokens = (List<Map<String, Object>>) tokens.get("finally");
        if(assertOptionalField("finally", ArrayList.class, tokens)) {
            finallyBlock = new Parser(null).parse(driver, finallyBlockTokens, this);
        }

        // Populate the try-block
        List<Map<String, Object>> tryBlockTokens = (List<Map<String, Object>>) tokens.get("try");
        tryBlock = new Parser(null).parse(driver, tryBlockTokens, this);

        // Populate the catch-block
        Map<String, List<Map<String, Object>>> catchBlockTokens = (Map<String, List<Map<String, Object>>>) tokens.get("catch");
        for (Map.Entry<String, List<Map<String, Object>>> exceptionHandler : catchBlockTokens.entrySet()) {
            String exceptionName = exceptionHandler.getKey().toLowerCase();
            List<Map<String, Object>> instructions = exceptionHandler.getValue();

            Program handler = new Parser(null).parse(driver, instructions, this);
            catchBlocks.put(exceptionName, handler);
        }

        return this;
    }

    public List<String> getFlatStack() {
        // Create stack with the try block
        ArrayList<String> snapshots = new ArrayList<>(tryBlock.getSnapshots());

        // Add all the catch blocks
        for(Program block : catchBlocks.values()) {
            snapshots.addAll(block.getSnapshots());
        }

        // Add finally block if it exists
        if(finallyBlock != null) {
            snapshots.addAll(finallyBlock.getSnapshots());
        }
        return snapshots;
    }
}