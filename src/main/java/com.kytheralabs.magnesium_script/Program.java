package com.kytheralabs.magnesium_script;

import com.kytheralabs.magnesium_script.expressions.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    private List<Expression> program;
    private Map<String, Object> global_context = new HashMap<>();

    Program() {
        program = new ArrayList<>();
    }

    public void run() {
        program.forEach(Expression::eval);
    }

    protected boolean addInstruction(Expression instruction) {
        try{
            program.add(instruction);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
