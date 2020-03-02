package es.us.idea.runs.constraints;

import org.apache.commons.lang3.ArrayUtils;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

import java.util.Arrays;
import java.util.Comparator;

public class EqualSumsPropagator extends Propagator {
    private IntVar[] xArray;
    private IntVar[] varLog;

    public EqualSumsPropagator(IntVar[] xArray, IntVar[] varLog) {
        super(ArrayUtils.addAll(xArray,varLog), PropagatorPriority.VERY_SLOW, false);
        //Here we have to sort xArray, because there could be zeroes at the end of it (due to a variable having more repetitions in the model than in the log)
        //TODO

        this.xArray = xArray;
        this.varLog = varLog;
    }

    @Override
    public void propagate(int i) throws ContradictionException {

        /*if(vars1.length == 5 && vars1[0].getLB() == 12 && vars1[1].getLB() == 19 && vars1[2].getLB() == 22 && vars1[3].getLB() == 25 && vars1[4].getLB() == 29){
            System.out.println();
        } else if (vars2.length == 5 && vars2[0].getLB() == 12 && vars2[1].getLB() == 19 && vars2[2].getLB() == 22 && vars2[3].getLB() == 25 && vars2[4].getLB() == 29){
            System.out.println();
        }*/

        int minSum1 = 0;
        int minSum2 = 0;
        int maxSum1 = 0;
        int maxSum2 = 0;

        for(IntVar v : xArray){
            minSum1 += v.getLB();
            maxSum1 += v.getUB();
        }

        for(IntVar v : varLog){
            minSum2 += v.getLB();
            maxSum2 += v.getUB();
        }

        if(minSum1 > maxSum2 || minSum2 > maxSum1){
            throw new ContradictionException();
        }

        if(minSum1 == maxSum2){
            for(IntVar v : xArray){
                v.instantiateTo(v.getLB(),this);
            }
            for(IntVar v : varLog){
                v.instantiateTo(v.getUB(),this);
            }
        } else if (minSum2 == maxSum1){
            for(IntVar v : xArray){
                v.instantiateTo(v.getUB(),this);
            }
            for(IntVar v : varLog){
                v.instantiateTo(v.getLB(),this);
            }
        }/*else {
            //TODO nuevos updates que pillan bien
            for(IntVar v : vars1){
                v.updateLowerBound(min2, this);
                v.updateUpperBound(max2, this);
                //v.updateBounds(min2,max2,this);
            }
            for(IntVar v : vars2){
                v.updateLowerBound(min1, this);
                v.updateUpperBound(max1, this);
                //v.updateBounds(min1,max1,this);
            }
        }*/

        //TODO algoritmo que intenta fijar variables
        int startIndex = 0;
        int endIndex = varLog.length-1;
        boolean iterateLeftSide = true;
        boolean iterateRightSide = true;

        //TODO aquí hay un error que provoca que se quiten los ceros de los intervalos (solo se ignoran con lo cual no cuentan en la ecuacion)
        //Arrays.sort(this.xArray, Comparator.comparingInt(IntVar::getUB));


        int minimumOfNotInstantiatedVars = xArray[startIndex].getLB();

        if(xArray[endIndex].getUB() == 0){
            minimumOfNotInstantiatedVars = 0;
            startIndex--;
            while(xArray[endIndex].getUB() == 0 && endIndex > 0){
                endIndex--;
            }
        }

        int maximumOfNotInstantiatedVars = xArray[endIndex].getUB();

        do{
            for(IntVar v : varLog){
                if(!v.isInstantiated()){
                    if(v.getLB() > maximumOfNotInstantiatedVars || v.getUB() < minimumOfNotInstantiatedVars){
                        throw new ContradictionException();
                    }
                    if(iterateRightSide && v.getUB() == minimumOfNotInstantiatedVars){
                        v.instantiateTo(v.getUB(),this);
                        iterateRightSide = true;
                    } else {
                        iterateRightSide = false;
                        if (iterateLeftSide && v.getLB() == maximumOfNotInstantiatedVars){
                            v.instantiateTo(v.getLB(),this);
                            iterateLeftSide = true;
                        } else {
                            iterateLeftSide = false;
                        }
                    }
                }
            }

            if(iterateLeftSide){
                startIndex++;
            }

            if(iterateRightSide){
                endIndex--;
            }

            if(startIndex >= endIndex){
                break;
            }

        } while(iterateLeftSide || iterateRightSide);


        //TODO si no hemos podido instanciar las variables al menos podemos fijar minimos y maximos basandonos en los minimos y maximos del xarray
        for(IntVar variableToLimit : varLog){
            if(!variableToLimit.isInstantiated()){
                variableToLimit.updateBounds(minimumOfNotInstantiatedVars,maximumOfNotInstantiatedVars,this);
            }
        }



        /*
        //Correcting lower values to minimums possible to achieve the minimum sum of the other
        if(minSum1 < minSum2){
            for(IntVar v : vars1){
                int maxSumWithoutVariable = maxSum1 - v.getUB();
                int minimumValueOfVToReachMinimumSum = minSum2 - maxSumWithoutVariable;
                if(minimumValueOfVToReachMinimumSum > v.getLB()){
                    v.updateLowerBound(minimumValueOfVToReachMinimumSum,this);
                }
            }
        } else if (minSum2 < minSum1){
            for(IntVar v : vars2){
                int maxSumWithoutVariable = maxSum2 - v.getUB();
                int minimumValueOfVToReachMinimumSum = minSum1 - maxSumWithoutVariable;
                if(minimumValueOfVToReachMinimumSum > v.getLB()){
                    v.updateLowerBound(minimumValueOfVToReachMinimumSum,this);
                }
            }
        }

        if(maxSum1 < maxSum2){
            for(IntVar v : vars1){
                int minSumWithoutVariable = minSum1 - v.getLB();
                int maximumValueOfVToReachMaximumSum = maxSum2 - minSumWithoutVariable;
                if(maximumValueOfVToReachMaximumSum < v.getUB()){
                    v.updateUpperBound(maximumValueOfVToReachMaximumSum,this);
                }
            }
        } else if (maxSum2 < maxSum1){
            for(IntVar v : vars2){
                int minSumWithoutVariable = minSum2 - v.getLB();
                int maximumValueOfVToReachMaximumSum = maxSum1 - minSumWithoutVariable;
                if(maximumValueOfVToReachMaximumSum < v.getUB()){
                    v.updateUpperBound(maximumValueOfVToReachMaximumSum,this);
                }
            }
        }
*/


    }

    @Override
    public ESat isEntailed() {
        int minSum1 = 0;
        int minSum2 = 0;
        int maxSum1 = 0;
        int maxSum2 = 0;

        for(IntVar v : xArray){
            minSum1 += v.getLB();
            maxSum1 += v.getUB();
        }

        for(IntVar v : varLog){
            minSum2 += v.getLB();
            maxSum2 += v.getUB();
        }

        if(minSum1 > maxSum2 || minSum2 > maxSum1){
            return ESat.FALSE;
        } else if (minSum1 == maxSum2 || minSum2 == maxSum1){
            return ESat.TRUE;
        } else {
            return ESat.UNDEFINED;
        }

    }

}
