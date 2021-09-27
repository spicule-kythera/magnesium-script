package uk.co.spicule.magnesium_script;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.expressions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    // Static things
    protected static Logger LOG = LoggerFactory.getLogger(Program.class);

    // Instance things
    private List<String> snapshots = new ArrayList<>();
    private List<Expression> program = new ArrayList<>();
    private Map<String, Object> context = new HashMap<>();

    public final Program run() throws Break.StopIterationException {
        for(Expression operation : program) {
            // Pre-execution phase
            if(operation instanceof DumpStack) {
                ((DumpStack) operation).setStack(snapshots);
            }
            operation.appendContext(context);

            // Execution phase
            Object response = operation.execute();

            // Post-execution phase
            if(operation instanceof Subroutine) {
                snapshots.addAll(((Subroutine) operation).getFlatStack());
                LOG.debug("Currying subroutine! " + snapshots.size() + " items in stack!");
            } else if(operation instanceof Snapshot) {
                snapshots.add((String) response);
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

    public String toString() {
        return "Program with " + program.size() + " instructions [context: " + context.toString() +  ", " + snapshots.size() + " snapshots]";
    }

    public void appendContext(Map<String, Object> context) {
        for(Map.Entry<String, Object> entry : context.entrySet()) {
            if(!this.context.containsKey(entry.getKey())) {
                this.context.put(entry.getKey(), entry.getValue());
            }
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
