package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class EqualSumsConstraint extends Constraint {

    public EqualSumsConstraint(IntVar[] vars1, IntVar[] vars2){
        super("EqualSums", new EqualSumsPropagator(vars1,vars2));
    }



}
