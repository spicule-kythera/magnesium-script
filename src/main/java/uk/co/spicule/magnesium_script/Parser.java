package uk.co.spicule.magnesium_script;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.expressions.*;

import java.util.List;
import java.util.Map;

public class Parser {
  // Static things
  public final static Logger LOG = LoggerFactory.getLogger(Parser.class);

  // Instance things
  Map<String, Object> tokens;

  public static class InvalidExpressionType extends Exception {
    public InvalidExpressionType(String expression) {
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
    Program program = new Program(parent);

    for (Map<String, Object> instructionBlock : runBlock) {
      String instructionName = instructionBlock.keySet().toArray()[0].toString().replace("_", "-");

      LOG.debug(instructionName + " -> " + instructionBlock);

      if (instructionName.equals("alert")) {
        program.addInstruction(new Alert(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("break")) {
        program.addInstruction(new Break(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("click")) {
        program.addInstruction(new Click(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("do")) {
        program.addInstruction(new DoWhile(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("dump-stack")) {
        program.addInstruction(new DumpStack(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("for")) {
        program.addInstruction(new For(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("get")) {
        program.addInstruction(new Get(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("if")) {
        program.addInstruction(new If(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("no-op")) {
        program.addInstruction(new NoOp(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("select")) {
        program.addInstruction(new Select(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("send-keys")) {
        program.addInstruction(new SendKeys(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("screenshot")) {
        program.addInstruction(new Screenshot(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("snapshot")) {
        program.addInstruction(new Snapshot(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("try")) {
        program.addInstruction(new Try(driver, parent).parse(instructionBlock));
      } else if (instructionName.equals("wait")) {
        program.addInstruction(new Wait(driver, parent).parse(instructionBlock));
      } else {
        throw new InvalidExpressionType(instructionName);
      }
    }
    return program;
  }
}
