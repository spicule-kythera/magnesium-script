package uk.co.spicule.magnesium_script.expressions;

import uk.co.spicule.magnesium_script.Parser;
import uk.co.spicule.magnesium_script.Program;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Type;
import java.util.*;

public class If extends Expression {
    Wait condition = null;
    Program thenBlock = null;
    Program elseBlock = null;

    public If(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
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

    private boolean conditionRunsWithoutException() {
        try {
            condition.execute();
            return true;
        } catch (Exception e) {
            System.out.println("If-condition failed due to the following:");
            e.printStackTrace();
            return false;
        }
    }
}
