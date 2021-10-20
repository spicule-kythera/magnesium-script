package uk.co.spicule.magnesium_script;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.expressions.*;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class Program {
    // Static things
    protected static Logger LOG = LoggerFactory.getLogger(Program.class);

    // Instance things
    List<String> snapshots = new ArrayList<>();
    List<Expression> program = new ArrayList<>();
    Expression parent = null;
    Map<String, Object> context = new HashMap<>();

    public Program() {

    }

    public Program(@Nullable Expression parent) {
        this.parent = parent;
    }

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
                String className = Expression.classPathToSlugName(operation).toUpperCase();
                List<String> subSnapshots = ((Subroutine) operation).getFlatStack();

                LOG.debug("Adding " + subSnapshots.size() + " snapshots from " + className + "-BLOCK to stack with " + snapshots.size() + " items!");
                snapshots.addAll(subSnapshots);
            } else if(operation instanceof Snapshot) {
                LOG.debug("Adding 1 snapshot to stack with " + snapshots.size() + " items!");
                snapshots.add((String) response);
            }
        }

        return this;
    }

    protected void addInstruction(Expression instruction) {
        instruction.setParent(parent);
        program.add(instruction);
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