package es.us.idea.runs;

import es.us.idea.runs.constraints.BelongsToConstraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.util.LinkedList;
import java.util.List;

public class TestCSP {
    public static void main(String[] args){
        Model model;
        IntVar variable;
        IntVar[] array;
        Solver solver;

        //1) Contradiction
        model = new Model();
        array = new IntVar[2];
        variable = model.intVar(0,2);
        array[0] = model.intVar(3,4);
        array[1] = model.intVar(5,6);
        model.post(new BelongsToConstraint(variable,array));
        solver = model.getSolver();

        boolean exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert exception;

        //2) Adjusting only the variable domain due to multiple options
        model = new Model();

        variable = model.intVar("test variable",0,3);
        array = new IntVar[3];
        for(int i = 1; i <= 5; i=i+2){
           array[(i-1)/2] = model.intVar(i,i+1);
        }

        model.post(new BelongsToConstraint(variable, array));

        solver = model.getSolver();

        exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert !exception;

        assert variable.getDomainSize() == 3;


        //3) Adjusting a list value
        model = new Model();

        array = new IntVar[2];
        variable = model.intVar(1,3);
        array[0] = model.intVar(1,4);
        array[1] = model.intVar(5,6);
        model.post(new BelongsToConstraint(variable,array));
        solver = model.getSolver();

        exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert !exception;

        assert array[0].getDomainSize() == 3;


        //4) Adjusting only the variable domain due to the only one option being already well limited
        model = new Model();

        array = new IntVar[2];
        variable = model.intVar(1,3);
        array[0] = model.intVar(1,2);
        array[1] = model.intVar(5,6);
        model.post(new BelongsToConstraint(variable,array));
        solver = model.getSolver();

        exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert !exception;

        assert array[0].getDomainSize() == 2;

        //5) Both variable and list item have to be adjusted
        model = new Model();

        array = new IntVar[2];
        variable = model.intVar(1,3);
        array[0] = model.intVar(0,2);
        array[1] = model.intVar(4,5);
        model.post(new BelongsToConstraint(variable,array));
        solver = model.getSolver();

        exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert !exception;

        assert array[0].getDomainSize() == 2;
        assert variable.getDomainSize() == 2;

        //6) Adjusting an inside value that is not on the borders
        model = new Model();

        array = new IntVar[2];
        variable = model.intVar(new int[]{1,2,4,5});
        array[0] = model.intVar(new int[]{1,2,5});
        array[1] = model.intVar(6,7);
        model.post(new BelongsToConstraint(variable,array));
        solver = model.getSolver();

        exception = false;
        try{
            solver.propagate();
        } catch (ContradictionException e){
            exception = true;
        }
        assert !exception;

        assert variable.getDomainSize() == 3;
    }

    @SuppressWarnings("unused")
    //Utility method for debugging purposes
    public static List<Integer> getAllPossibleValues(IntVar var){
        DisposableValueIterator it = var.getValueIterator(true);
        List<Integer> values = new LinkedList<>();

        while(it.hasNext()){
            values.add(it.next());
        }

        return values;
    }
}
