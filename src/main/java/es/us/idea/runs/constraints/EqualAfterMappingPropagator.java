package es.us.idea.runs.constraints;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableBitSet;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableSet;
import scala.Tuple2;

import java.util.Map;

public class EqualAfterMappingPropagator extends Propagator<IntVar>{
    private IntVar var1;
    private IntVar var2;
    private Map<Integer, Integer> mapping1;
    private Map<Integer, Integer> mapping2;

    EqualAfterMappingPropagator(IntVar var1, IntVar var2, Map<Integer, Integer> mapping1, Map<Integer, Integer> mapping2) {
        super(new IntVar[] {var1,var2}, PropagatorPriority.LINEAR, false);
        this.var1 = var1;
        this.var2 = var2;
        this.mapping1 = mapping1;
        this.mapping2 = mapping2;
    }

    @Override
    public void propagate(int mask) throws ContradictionException {

        //TODO GITANADA
        if(!var1.contains(0) && !var2.contains(0)){
            //var1
            IntIterableSet valuestoRemove = new IntIterableBitSet();
            DisposableValueIterator dv = var1.getValueIterator(true);
            while(dv.hasNext()){
                int next = dv.next();
                /*if(next == 0){
                    continue;
                }*/
                int convertedValue = mapping1.get(next);

                boolean contained = false;
                DisposableValueIterator dv2 = var2.getValueIterator(true);
                while(dv2.hasNext()){
                    int next2 = dv2.next();
                    /*if(next2 == 0){
                        continue;
                    }*/
                    int convertedValue2 = mapping2.get(next2);
                    if(convertedValue == convertedValue2){
                        contained = true;
                        break;
                    }
                }
                if(!contained /*&& !var2.isInstantiatedTo(0)*/){
                    valuestoRemove.add(next);
                }
            }
            var1.removeValues(valuestoRemove, this);

            //var2
            valuestoRemove = new IntIterableBitSet();
            dv = var2.getValueIterator(true);
            while(dv.hasNext()){
                int next = dv.next();
                /*if(next == 0){
                    continue;
                }*/
                int convertedValue = mapping2.get(next);

                boolean contained = false;
                DisposableValueIterator dv2 = var1.getValueIterator(true);
                while(dv2.hasNext()){
                    int next2 = dv2.next();
                    /*if(next2 == 0){
                        continue;
                    }*/
                    int convertedValue2 = mapping1.get(next2);
                    if(convertedValue == convertedValue2){
                        contained = true;
                        break;
                    }
                }
                //todo apaño gitano
                if(!contained /*&& !var1.isInstantiatedTo(0)*/){
                    valuestoRemove.add(next);
                }
            }
            var2.removeValues(valuestoRemove, this);
        }

    }

    @Override
    public ESat isEntailed() {

        //its only required from one side

        if(var1.isInstantiated() && var2.isInstantiated()){
            /*if(var1.isInstantiatedTo(0)){
                if(var2.isInstantiatedTo(0)){
                    return ESat.FALSE;
                } else {
                    return ESat.TRUE;
                }
            }
            if(var2.isInstantiatedTo(0)){
                return ESat.TRUE;
            }*/
            //TODO true gitanada

            if(var1.isInstantiatedTo(0) || var2.isInstantiatedTo(0)){
                return ESat.TRUE;
            }

            if(mapping1.get(var1.getValue()).equals(mapping2.get(var2.getValue()))){
                return ESat.TRUE;
            } else {
                return ESat.FALSE;
            }
        }

        boolean possible = false;
        if (firstCoincidenceFound(var1, var2, mapping1, mapping2) != null){
            possible = true;
        }

        if(possible){
            return ESat.UNDEFINED;
        } else {
            return ESat.FALSE;
        }

    }

    public static Tuple2<Integer, Integer> firstCoincidenceFound(IntVar v1, IntVar v2, Map<Integer,Integer> m1, Map<Integer,Integer> m2){

        Tuple2<Integer, Integer> solution = null;
        DisposableValueIterator dv1 = v1.getValueIterator(true);
        out:
        while (dv1.hasNext()){
            int next = dv1.next();
            if(next == 0){
                continue;
            }
            int convertedNext = m1.get(next);
            DisposableValueIterator dv2 = v2.getValueIterator(true);
            while(dv2.hasNext()){
                int next2 = dv2.next();
                if(next2 == 0){
                    continue;
                }
                int convertedNext2 = m2.get(next2);
                if(convertedNext == convertedNext2){
                    solution = new Tuple2<>(next, next2);
                    break out;
                }
            }
        }
        return solution;
    }
}
