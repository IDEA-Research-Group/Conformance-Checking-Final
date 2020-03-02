package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class BelongsToConstraint extends Constraint {

    public BelongsToConstraint(IntVar var, IntVar[] vars) {
        super("BelongsTo", new BelongsToPropagatorMk2(var,vars));
    }

    static IntVar[] compactVars(IntVar var, IntVar[] elements) {
        IntVar[] allVars = new IntVar[elements.length+1];
        allVars[0] = var;
        for (int i = 1; i <= elements.length; i++) {
            allVars[i] = elements[i-1];
        }
        return allVars;
    }

}
