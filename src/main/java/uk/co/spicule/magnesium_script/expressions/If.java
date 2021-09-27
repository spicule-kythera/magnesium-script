package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;

import java.lang.reflect.Type;
import java.util.*;

public class If extends ConditionalExpression implements Subroutine {
    Program thenBlock = null;
    Program elseBlock = null;

    public If(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() throws Break.StopIterationException {
        LOG.debug("IF " + condition.toString() + " succeeds, THEN run block " + thenBlock.toString() + ((elseBlock != null) ? (", ELSE run block " + elseBlock) : "") + "!");

        if(conditionRunsWithoutException()) {
            thenBlock.run();
        } else if(elseBlock != null) {
            elseBlock.run();
        }
        return null;
    }

    public If parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("if", LinkedHashMap.class);
        requiredFields.put("then", ArrayList.class);
        assertRequiredFields("if", requiredFields, tokens);

        // Assert optional fields
        boolean hasElse = assertOptionalField("else", ArrayList.class, tokens);

        // Populate the condition as a wait-block
        condition = new Wait(driver, this).parse((Map<String, Object>) tokens.get("if"));

        // Block sub-parser
        Parser subParser = new Parser(null);

        // Populate the then/else blocks
        thenBlock = subParser.parse(driver, (ArrayList) tokens.get("then"));
        if(hasElse){
            elseBlock = subParser.parse(driver, (ArrayList) tokens.get("else"));
        }

        return this;
    }

    public List<String> getFlatStack() {
        ArrayList<String> stack = new ArrayList<>();
        stack.addAll(thenBlock.getSnapshots());
        if(elseBlock != null) {
            stack.addAll(elseBlock.getSnapshots());
        }
        return stack;
    }
}
