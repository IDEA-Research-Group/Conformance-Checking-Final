package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

public class BelongsToPropagator extends Propagator<IntVar> {
    private IntVar variable;
    private IntVar[] list;

    public BelongsToPropagator(IntVar var, IntVar[] elements){
        super(BelongsToConstraint.compactVars(var,elements), PropagatorPriority.LINEAR, true);
        variable = var;
        list = elements;
    }

    @Override
    public void propagate(int i) throws ContradictionException {
        restrictVariableToListDomain();
        restrictListToVariableDomainLazy();
    }

    @Override
    public void propagate(int varIdx, int mask) throws ContradictionException {
        //outdated comment
        //We always have to check the list, either because the variable changed or to check contradictions.
        if(varIdx > 0){
            restrictVariableToListDomain();
        } else {
            restrictListToVariableDomainLazy();
        }
    }

    @Override
    public ESat isEntailed() {
        for(IntVar i : list){
            //If the list element domain overlaps with the variable one
            if(overlapsDomains(i,variable)){
                return ESat.TRUE;
            }
        }
        return ESat.FALSE;
    }

    private void restrictVariableToListDomain() throws ContradictionException{

        /*System.out.println("restricting variable:");
        System.out.println("lower bound: " + lowerBound);
        System.out.println("upper bound: " + upperBound);*/

        Integer upperBound = Integer.MIN_VALUE;
        Integer lowerBound = Integer.MAX_VALUE;

        for(IntVar i : list){
            int ub = i.getUB();
            if(ub > upperBound){
                upperBound = ub;
            }

            int lb = i.getLB();
            if(lb < lowerBound){
                lowerBound = lb;
            }
        }
        variable.updateBounds(lowerBound,upperBound,this);
    }

    //Only modifies domains when it's the only possibility
    private void restrictListToVariableDomainLazy() throws ContradictionException{
        IntVar overlapper = null;

        for(IntVar i : list){
            //If the list element domain overlaps with the variable one
            if(overlapsDomains(i,variable)){
                if(overlapper != null){
                    return;
                } else {
                    overlapper = i;
                }
            }
        }
        if(overlapper == null){
            throw new ContradictionException();
        } else {
            overlapper.updateBounds(variable.getLB(),variable.getUB(),this);
        }
    }

    //Only works for non-enumerated domains
    private boolean overlapsDomains(IntVar a, IntVar b){
        boolean aLesserThanB = a.getUB() > b.getLB() && a.getUB() < b.getUB();
        boolean bLesserThanA = b.getUB() > a.getLB() && b.getUB() < a.getUB();
        return aLesserThanB || bLesserThanA;
    }


}