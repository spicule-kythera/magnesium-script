package uk.co.spicule.magnesium_script;

import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.expressions.*;

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

  public Program parse(WebDriver driver, List<Map<String, Object>> runBlock) throws InvalidExpressionType, Expression.InvalidExpressionSyntax {
    return parse(driver, runBlock, null);
  }

  public Program parse(WebDriver driver, List<Map<String, Object>> runBlock, Expression parent)
      throws InvalidExpressionType, Expression.InvalidExpressionSyntax {
    Program program = new Program();

    for (Map<String, Object> instruction : runBlock) {
      if (instruction.containsKey("alert")) {
        program.addInstruction(new Alert(driver, parent).parse(instruction));
      } else if (instruction.containsKey("dump-stack") || instruction.containsKey("dump_stack")) {

        program.addInstruction(new DumpStack(driver, parent).parse(instruction));
      } else if (instruction.containsKey("for")) {
        program.addInstruction(new For(driver, parent).parse(instruction));
      } else if (instruction.containsKey("get")) {
        program.addInstruction(new Get(driver, parent).parse(instruction));
      } else if (instruction.containsKey("if")) {
        program.addInstruction(new If(driver, parent).parse(instruction));
      } else if (instruction.containsKey("screenshot")) {
        program.addInstruction(new Screenshot(driver, parent).parse(instruction));
      } else if (instruction.containsKey("snapshot")) {
        program.addInstruction(new Snapshot(driver, parent).parse(instruction));
      } else if (instruction.containsKey("try")) {
        program.addInstruction(new Try(driver, parent).parse(instruction));
      } else {
        throw new InvalidExpressionType(instruction.toString());
      }
    }
    return program;
  }
}
