package uk.co.spicule.magnesium_script;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.expressions.Expression;
import uk.co.spicule.magnesium_script.expressions.Snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    // Static things
    protected static Logger LOG;

    // Instance things
    private List<Expression> program;
    private Map<String, Object> global_context = new HashMap<>();
    private List<String> snapshots = new ArrayList<>();

    public Program() {
        Program.LOG = LoggerFactory.getLogger(Program.class);
        program = new ArrayList<>();
    }

    public Program(Logger LOG) {
        Program.LOG = LOG;
        program = new ArrayList<>();
    }

    public final Program run() {
        for(Expression operation : program) {
            Object res = operation.execute();

            if(operation instanceof Snapshot) {
                snapshots.add((String) res);
            }
        }

        return this;
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

    @Getter
    public final List<String> getSnapshots() {
        return snapshots;
    }

    @Setter
    public void setSnapshots(List<String> snapshots) {
        this.snapshots = snapshots;
    }
}
