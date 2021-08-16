package com.kytheralabs.magnesium_script.expressions.expressions;

import com.kytheralabs.magnesium_script.expressions.Parser;
import com.kytheralabs.magnesium_script.expressions.Program;
import org.openqa.selenium.WebDriver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Try extends Expression {
    Program tryBlock = new Program();
    Map<String, Program> catchBlocks = new HashMap<>();

    Try(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    private static String exceptionToSlugName(Exception e) {
        String[] parts = e.getClass().toString().split("\\.");
        return parts[parts.length - 1].toLowerCase();
    }

    public void execute() {
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
    }

    public Try parse(Map<String, Object> tokens) throws Parser.InvalidExpressionType, InvalidExpressionSyntax {
        // Process Try
        if(!tokens.containsKey("try")) {
            throw new InvalidExpressionSyntax("try", "`try` operation must contain list of operations to run.");
        }
        List<Map<String, Object>> tryBlockTokens = (List<Map<String, Object>>) tokens.get("try");
        Parser parser = new Parser(null);
        tryBlock = parser.parse(driver, tryBlockTokens);

        // Process catch
        if(!tokens.containsKey("catch")){
            throw new InvalidExpressionSyntax("try", "`try` operation must contain `catch` block with a map of exceptions to instructions for processing errors.");
        }
        Map<String, List<Map<String, Object>>> catchBlockTokens = (Map<String, List<Map<String, Object>>>) tokens.get("catch");

        for (Map.Entry<String, List<Map<String, Object>>> exceptionHandler : catchBlockTokens.entrySet()) {
            String exceptionName = exceptionHandler.getKey().toLowerCase();
            List<Map<String, Object>> instructions = exceptionHandler.getValue();

            Program handler = parser.parse(driver, instructions);
            catchBlocks.put(exceptionName, handler);
        }

        return this;
    }
}