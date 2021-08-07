package com.kytheralabs.magnesium_script;

import java.io.IOException;
import com.kytheralabs.magnesium_script.expressions.Expression;

public class Sandbox {
    public static void main(String[] args) throws Parser.InvalidExpressionType, IOException, Expression.InvalidExpressionSyntax {
        String path = System.getProperty("user.home") + "/cs/work/yaml-scripts/demo.yml";
        MagnesiumScript.compile(new String[]{path});
    }
}
