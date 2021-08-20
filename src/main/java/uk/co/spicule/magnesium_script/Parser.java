package uk.co.spicule.magnesium_script;

import uk.co.spicule.magnesium_script.expressions.Alert;
import uk.co.spicule.magnesium_script.expressions.Get;
import uk.co.spicule.magnesium_script.expressions.Expression;
import org.openqa.selenium.WebDriver;
import java.util.List;
import java.util.Map;

public class Parser {
  Map<String, Object> tokens;

  public static class InvalidExpressionType extends Exception {
    InvalidExpressionType(String expression) {
      super("Invalid expression type specified: `" + expression + "`!");
    }

    InvalidExpressionType(String expression, String message) {
      super("Invalid expression type specified: `" + expression + "`: " + message + "!");
    }
  }

  public Parser(Map<String, Object> tokens) {
    this.tokens = tokens;
  }

  public Program parse(WebDriver driver) throws InvalidExpressionType, Expression.InvalidExpressionSyntax {
    if (!tokens.containsKey("run")) {
      throw new InvalidExpressionType("run", "MagnesiumScript requires the script to be placed under the `run` block");
    }

    return parse(driver, (List<Map<String, Object>>) tokens.get("run"));
  }

  public Program parse(WebDriver driver, List<Map<String, Object>> runBlock)
      throws InvalidExpressionType, Expression.InvalidExpressionSyntax {
    Program program = new Program();

    for (Map<String, Object> instruction : runBlock) {
      if (instruction.containsKey("alert")) {
        program.addInstruction(new Alert(driver, null).parse(instruction));
      } else if (instruction.containsKey("get")) {
        program.addInstruction(new Get(driver, null).parse(instruction));
      } else {
        throw new InvalidExpressionType(instruction.toString());
      }
    }
    return program;
  }
}
