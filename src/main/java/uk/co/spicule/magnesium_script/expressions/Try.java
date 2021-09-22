package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.lang.reflect.Type;
import java.util.*;

public class Try extends Expression {
    Program tryBlock = new Program();
    Map<String, Program> catchBlocks = new HashMap<>();

    public Try(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    private static String exceptionToSlugName(Exception e) {
        String[] parts = e.getClass().toString().split("\\.");
        return parts[parts.length - 1].toLowerCase();
    }

    public Object execute() {
        LOG.debug("Resolving expression: `" + this.getClass() + "`!");

        try {
            tryBlock.run();
        } catch (Exception e) {
            String slugName = exceptionToSlugName(e); // Convert the whole exception name to just the slug

            // Run the exception handler iff it's found in the catch block.
            if(catchBlocks.containsKey(slugName)) {
                catchBlocks.get(slugName).run();
            } else { // Re-Throw the exception
                throw e;
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

        return this;
    }
}