package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableBitSet;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableSet;

public class Ascending1Except0Propagator extends Propagator<IntVar> {
    IntVar[] vars;

    public Ascending1Except0Propagator(IntVar[] vars) {
        super(vars, PropagatorPriority.LINEAR, false);
        this.vars = vars;
    }

    @Override
    public void propagate(int i) throws ContradictionException {
        int bynow = 1;
        boolean instantiate = true;
        IntIterableSet set = new IntIterableBitSet();

        for(IntVar var : vars){
            if(instantiate){
                if(!var.contains(0)){
                    var.instantiateTo(bynow, this);
                    set.add(bynow);
                    bynow++;
                } else if(!var.contains(bynow)){
                    var.instantiateTo(0,this);
                } else{
                    instantiate = false;
                }
            } else {
                var.removeValues(set, this);
            }

        }
    }

    @Override
    public ESat isEntailed() {
        int bynow = 1;

        for(IntVar var : vars){
            if(!var.isInstantiated()){
                return ESat.UNDEFINED;
            }
            if(!var.isInstantiatedTo(0) && !var.isInstantiatedTo(bynow)){
                return ESat.FALSE;
            }

            if(var.isInstantiatedTo(bynow)){
                bynow++;
            }
        }

        return ESat.TRUE;
    }


}
