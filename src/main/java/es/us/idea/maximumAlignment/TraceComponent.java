package es.us.idea.maximumAlignment;

import java.util.Arrays;
import java.util.List;

import ilog.concert.*;
import ilog.solver.*;

public class TraceComponent {

	// Identificador
	String id;

	//Nombre
	String name;

	// Posición
	IlcIntVar[] pos; 

	// Abnormal
	IlcIntVar AB;

    public TraceComponent(String id, String name, IlcSolver solver, List<List<Integer>> values)  throws IloException {
        this.id = id;
        this.name = name;
        pos = new IlcIntVar[values.size()];

        for (int i = 0; i < pos.length; i++) {
            IloIntDomain d = solver.intDomain(values.get(i).stream().mapToInt(j -> j).toArray());
            pos[i] = solver.intVar(d, name + " Position " + i);
        }

        this.AB = solver.intVar(0, 1, name + " normal");
    }

	/*public TraceComponent(String id, String name, IlcSolver solver, List<Integer> minValues, List<Integer> maxValues)  throws IloException{
		this.id = id;
		this.name = name;
		pos = new IlcIntVar[minValues.size()];
		for(int i = 0; i < pos.length; i++){
			pos[i] = solver.intVar(minValues.get(i),maxValues.get(i), name + " Position " + i);
		}

		this.AB = solver.intVar(0, 1, name + " normal");
	}*/

	public TraceComponent(String id, String name, IlcSolver solver, int posSize,
			int minValue, int maxValue) throws IloException {
		this.id = id;
		this.name = name;
        pos = new IlcIntVar[posSize];
        for(int i = 0; i < pos.length; i++){
            pos[i] = solver.intVar(minValue,maxValue, name + " Position " + i);
        }
		//this.pos = solver.intVarArray(posSize, minValue, maxValue, name + " Positions");
		this.AB = solver.intVar(0, 1, name + " normal");
	}

	public TraceComponent(String id, IlcSolver solver, int posSize,
						  int minValue, int maxValue) throws IloException {
	    this(id,id, solver,posSize,minValue,maxValue);
	}

	@Override
	public String toString() {
		return "AB=" + AB + " " + id + " -> "
				+ Arrays.toString(pos);
	}

}
