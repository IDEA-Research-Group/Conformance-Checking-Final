package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Ascending1Except0Constraint extends Constraint {

    public Ascending1Except0Constraint(IntVar[] vars) {
        super("Ascending1Except0", new Ascending1Except0Propagator(vars));
    }



}
