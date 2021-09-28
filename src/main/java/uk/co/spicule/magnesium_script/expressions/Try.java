package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.lang.reflect.Type;
import java.util.*;

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
            // If a finally block was specified, run itc
            if(finallyBlock != null) {
                finallyBlock.run();
            }
        }
        return null;
    }

    public Try parse(Map<String, Object> tokens) throws Parser.InvalidExpressionType, InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("try", ArrayList.class);
        requiredFields.put("catch", LinkedHashMap.class);
        assertRequiredFields("try", requiredFields, tokens);
        boolean hasFinally = assertOptionalField("finally", ArrayList.class, tokens);

        // Populate the try-block
        List<Map<String, Object>> tryBlockTokens = (List<Map<String, Object>>) tokens.get("try");
        tryBlock = new Parser(null).parse(driver, tryBlockTokens, this);

        // Populate the catch-block
        Map<String, List<Map<String, Object>>> catchBlockTokens = (Map<String, List<Map<String, Object>>>) tokens.get("catch");
        for (Map.Entry<String, List<Map<String, Object>>> exceptionHandler : catchBlockTokens.entrySet()) {
            String exceptionName = exceptionHandler.getKey().toLowerCase();
            List<Map<String, Object>> instructions = exceptionHandler.getValue();

            Program handler = new Parser(null).parse(driver, tryBlockTokens, this);
            catchBlocks.put(exceptionName, handler);
        }

        // Populate the finally block if any
        List<Map<String, Object>> finallyBlockTokens = (List<Map<String, Object>>) tokens.get("finally");
        if(hasFinally) {
            finallyBlock = new Parser(null).parse(driver, finallyBlockTokens, this);
        }

        return this;
    }

    public List<String> getFlatStack() {
        ArrayList<String> snapshots = new ArrayList();

        // Add the try block
        snapshots.addAll(tryBlock.getSnapshots());

        // Add all of the catch blocks
        for(Program block : catchBlocks.values()) {
            snapshots.addAll(block.getSnapshots());
        }

        // Add the finally block if it exists
        if(finallyBlock != null) {
            snapshots.addAll(finallyBlock.getSnapshots());
        }
        return snapshots;
    }
}