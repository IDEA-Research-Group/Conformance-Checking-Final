package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.util.Map;

public class EqualAfterMappingConstraint extends Constraint {

    public EqualAfterMappingConstraint(IntVar var1, IntVar var2, Map<Integer, Integer> mapping1, Map<Integer, Integer> mapping2) {
        super("EqualAfterMapping", new EqualAfterMappingPropagator(var1,var2, mapping1, mapping2));
    }

}
