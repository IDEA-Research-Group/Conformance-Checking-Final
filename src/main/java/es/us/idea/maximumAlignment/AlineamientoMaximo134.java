package es.us.idea.maximumAlignment;

import ilog.concert.IloException;
import ilog.solver.IlcConstraint;
import ilog.solver.IlcIntExpr;
import ilog.solver.IlcIntVar;
import ilog.solver.IlcSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AlineamientoMaximo134 {

	public static void main(String[] args) throws IloException{
		new AlineamientoMaximo134().run();
	}
	
	TraceComponent A,C,AA,AF,AJ,B,AC1,AH,AD1,AI,AC2,D,AG,AD2,AC3,AD3,AE1,AD4,AE2,AC4,AB,AE3;

	List<TraceComponent> ordering;
	public void run() throws IloException{
		IlcSolver solver = new IlcSolver();
		List<IlcIntVar> ABList = new LinkedList<>();
        ordering = new ArrayList<>();

		A = new TraceComponent("A", solver, 1, 0, 6); ABList.add(A.AB);ordering.add(A);
		C = new TraceComponent("C", solver, 1, 0, 6); ABList.add(C.AB);ordering.add(C);
		AA = new TraceComponent("AA", solver, 1, 0, 6); ABList.add(AA.AB);ordering.add(AA);

        AF = new TraceComponent("AF", solver, 3, 0, 3); ABList.add(AF.AB);ordering.add(AF);
        AJ = new TraceComponent("AJ", solver, 5, 0, 3); ABList.add(AJ.AB);ordering.add(AJ);
        B = new TraceComponent("B", solver, 1, 0, 6); ABList.add(B.AB);ordering.add(B);
		AC1 = new TraceComponent("AC1", solver, 3, 0, 14); ABList.add(AC1.AB);ordering.add(AC1);
        AH = new TraceComponent("AH", solver, 5, 0, 3); ABList.add(AH.AB);ordering.add(AH);
        AD1 = new TraceComponent("AD1", solver, 3, 0, 14); ABList.add(AD1.AB);ordering.add(AD1);
        AI = new TraceComponent("AI", solver, 5, 0, 3); ABList.add(AI.AB);ordering.add(AI);
        AC2 = new TraceComponent("AC2", solver, 3, 0, 14); ABList.add(AC2.AB);ordering.add(AC2);
        D = new TraceComponent("D", solver, 1, 0, 6); ABList.add(D.AB);ordering.add(D);
        AG = new TraceComponent("AG", solver, 3, 0, 3); ABList.add(AG.AB);ordering.add(AG);
        AD2 = new TraceComponent("AD2", solver, 3, 0, 14); ABList.add(AD2.AB);ordering.add(AD2);
        AC3 = new TraceComponent("AC3", solver, 3, 0, 14); ABList.add(AC3.AB);ordering.add(AC3);
        AD3 = new TraceComponent("AD3", solver, 3, 0, 14); ABList.add(AD3.AB);ordering.add(AD3);
        AE1 = new TraceComponent("AE1", solver, 3, 0, 14); ABList.add(AE1.AB);ordering.add(AE1);
        AD4 = new TraceComponent("AD4", solver, 3, 0, 14); ABList.add(AD4.AB);ordering.add(AD4);
        AE2 = new TraceComponent("AE2", solver, 3, 0, 14); ABList.add(AE2.AB);ordering.add(AE2);
        AC4 = new TraceComponent("AC4", solver, 3, 0, 14); ABList.add(AC4.AB);ordering.add(AC4);
        AB = new TraceComponent("AB", solver, 1, 0, 6); ABList.add(AB.AB);ordering.add(AB);
		AE3 = new TraceComponent("AE3", solver, 3, 0, 14); ABList.add(AE3.AB);ordering.add(AE3);

		// *********** No abnormal *************


        //solver.add(solver.imply(solver.eq(AE1.AB,0),solver.and(solver.and(solver.eq(AE1.pos[0],3),solver.eq(AE1.pos[1],2)),solver.or(solver.eq(AE1.pos[2],2), solver.eq(AE1.pos[2], 5)))));


        solver.add(solver.imply(solver.eq(A.AB,0),solver.eq(A.pos[0], 0)));
        solver.add(solver.imply(solver.eq(C.AB,0),solver.eq(C.pos[0],1)));
        solver.add(solver.imply(solver.eq(AA.AB,0),solver.eq(AA.pos[0],2)));
        solver.add(solver.imply(solver.eq(AF.AB,0),solver.and(solver.and(solver.eq(AF.pos[0],3),solver.eq(AF.pos[1],1)),solver.eq(AF.pos[2],0))));

        solver.add(solver.imply(solver.eq(AJ.AB,0),solver.and(solver.and(solver.and(solver.eq(AJ.pos[0],3),solver.eq(AJ.pos[1],1)),solver.and(solver.eq(AJ.pos[2],1),solver.eq(AJ.pos[3],2))),solver.eq(AJ.pos[4],0))));
        solver.add(solver.imply(solver.eq(B.AB,0),solver.eq(B.pos[0],6)));
        solver.add(solver.imply(solver.eq(AC1.AB,0),solver.and(solver.and(solver.eq(AC1.pos[0],3),solver.eq(AC1.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC1.pos[2],0),solver.eq(AC1.pos[2],3)),solver.or(solver.eq(AC1.pos[2],6),solver.eq(AC1.pos[2],9))),solver.eq(AC1.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AH.AB,0),solver.and(solver.and(solver.and(solver.eq(AH.pos[0],3),solver.eq(AH.pos[1],1)),solver.and(solver.eq(AH.pos[2],1),solver.eq(AH.pos[3],0))),solver.eq(AH.pos[4],0))));
        solver.add(solver.imply(solver.eq(AD1.AB,0),solver.and(solver.and(solver.eq(AD1.pos[0],3),solver.eq(AD1.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD1.pos[2],1),solver.eq(AD1.pos[2],4)),solver.or(solver.eq(AD1.pos[2],7),solver.eq(AD1.pos[2],10))),solver.eq(AD1.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AI.AB,0),solver.and(solver.and(solver.and(solver.eq(AI.pos[0],3),solver.eq(AI.pos[1],1)),solver.and(solver.eq(AI.pos[2],1),solver.eq(AI.pos[3],1))),solver.eq(AI.pos[4],0))));
        solver.add(solver.imply(solver.eq(AC2.AB,0),solver.and(solver.and(solver.eq(AC2.pos[0],3),solver.eq(AC2.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC2.pos[2],0),solver.eq(AC2.pos[2],3)),solver.or(solver.eq(AC2.pos[2],6),solver.eq(AC2.pos[2],9))),solver.eq(AC2.pos[2],12)))));
        solver.add(solver.imply(solver.eq(D.AB,0),solver.eq(D.pos[0],5)));
        solver.add(solver.imply(solver.eq(AG.AB,0),solver.and(solver.and(solver.eq(AG.pos[0],3),solver.eq(AG.pos[1],1)),solver.eq(AG.pos[2],2))));
        solver.add(solver.imply(solver.eq(AD2.AB,0),solver.and(solver.and(solver.eq(AD2.pos[0],3),solver.eq(AD2.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD2.pos[2],1),solver.eq(AD2.pos[2],4)),solver.or(solver.eq(AD2.pos[2],7),solver.eq(AD2.pos[2],10))),solver.eq(AD2.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AC3.AB,0),solver.and(solver.and(solver.eq(AC3.pos[0],3),solver.eq(AC3.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC3.pos[2],0),solver.eq(AC3.pos[2],3)),solver.or(solver.eq(AC3.pos[2],6),solver.eq(AC3.pos[2],9))),solver.eq(AC3.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AD3.AB,0),solver.and(solver.and(solver.eq(AD3.pos[0],3),solver.eq(AD3.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD3.pos[2],1),solver.eq(AD3.pos[2],4)),solver.or(solver.eq(AD3.pos[2],7),solver.eq(AD3.pos[2],10))),solver.eq(AD3.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AE1.AB,0),solver.and(solver.and(solver.eq(AE1.pos[0],3),solver.eq(AE1.pos[1],0)),
                solver.or(solver.or(solver.eq(AE1.pos[2],2),solver.eq(AE1.pos[2],5)),solver.or(solver.eq(AE1.pos[2],8),solver.eq(AE1.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AD4.AB,0),solver.and(solver.and(solver.eq(AD4.pos[0],3),solver.eq(AD4.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD4.pos[2],1),solver.eq(AD4.pos[2],4)),solver.or(solver.eq(AD4.pos[2],7),solver.eq(AD4.pos[2],10))),solver.eq(AD4.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AE2.AB,0),solver.and(solver.and(solver.eq(AE2.pos[0],3),solver.eq(AE2.pos[1],0)),
                solver.or(solver.or(solver.eq(AE2.pos[2],2),solver.eq(AE2.pos[2],5)),solver.or(solver.eq(AE2.pos[2],8),solver.eq(AE2.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AC4.AB,0),solver.and(solver.and(solver.eq(AC4.pos[0],3),solver.eq(AC4.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC4.pos[2],0),solver.eq(AC4.pos[2],3)),solver.or(solver.eq(AC4.pos[2],6),solver.eq(AC4.pos[2],9))),solver.eq(AC4.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AB.AB,0),solver.eq(AB.pos[0], 4)));
        solver.add(solver.imply(solver.eq(AE3.AB,0),solver.and(solver.and(solver.eq(AE3.pos[0],3),solver.eq(AE3.pos[1],0)),
                solver.or(solver.or(solver.eq(AE3.pos[2],2),solver.eq(AE3.pos[2],5)),solver.or(solver.eq(AE3.pos[2],8),solver.eq(AE3.pos[2],11))))));

        // *********** Parejas *************

        for(int i = 0; i < ordering.size(); i++){
            for(int j = i+1; j < ordering.size(); j++){
                solver.add(solver.imply(solver.and(solver.eq(ordering.get(i).AB, 0), solver.eq(ordering.get(j).AB, 0)), menorIgual(solver, ordering.get(i), ordering.get(j))));
            }
        }


        // *********** Alignment *************

		IlcIntExpr[] ABRefs = new IlcIntExpr[ABList.size()];
		for (int i = 0; i < ABList.size(); i++){
			ABRefs[i] = ABList.get(i);
		}
		IlcIntVar sumRefE = solver.intVar(0, ABList.size());//ABList.size() - 7
		solver.add(solver.eq(sumRefE, solver.sum(ABRefs)));
		solver.add(solver.minimize(sumRefE, 1)); // 1 0

        //TODO SABEMOS QUE LA MEJOR SOLUCION ESTA EN 10 Y QUEREMOS QUE LA ALCANCE
        IlcIntVar missAlignment = solver.intVar(10, 26);

        solver.add(solver.eq(missAlignment, solver.sum(solver.prod(sumRefE,2),26-22)));

        List<IlcIntVar> hardVars = new ArrayList<>();
        hardVars.addAll(Arrays.asList(AC1.pos));
        hardVars.addAll(Arrays.asList(AC2.pos));
        hardVars.addAll(Arrays.asList(AC3.pos));
        hardVars.addAll(Arrays.asList(AC4.pos));
        hardVars.addAll(Arrays.asList(AD1.pos));
        hardVars.addAll(Arrays.asList(AD2.pos));
        hardVars.addAll(Arrays.asList(AD3.pos));
        hardVars.addAll(Arrays.asList(AD4.pos));
        hardVars.addAll(Arrays.asList(AE1.pos));
        hardVars.addAll(Arrays.asList(AE2.pos));
        hardVars.addAll(Arrays.asList(AE3.pos));

        //TODO hacks

        /*solver.add(solver.eq(AC1.AB, 0));
        solver.add(solver.eq(AF.AB, 0));
        solver.add(solver.eq(AJ.AB, 0));*/
        //solver.add(solver.eq(AH.AB, 0));
        solver.printModel();
        solver.newSearch(solver.and(solver.generate(ABRefs), solver.generate(hardVars.toArray(new IlcIntVar[hardVars.size()]))));
		//solver.newSearch(solver.generate(ABRefs));

		long start = System.currentTimeMillis();
		while (solver.next()) {
			System.out.println("********************************************");
			System.out.println("Alineamiento: " + missAlignment);
            //System.out.println("Alineamiento: " + (2* sumRefE.getDomainValue() + 35 - 30));
			System.out.println(this.toString());
			System.out.println("********************************************");
		}

		long end = System.currentTimeMillis();
		long res = end - start;
		System.out.println("MiliSegundos: " + res);
	}
	
	private IlcConstraint menorIgual(IlcSolver solver, TraceComponent c1, TraceComponent c2){
		int c1Size = c1.pos.length;
		int c2Size = c2.pos.length;
		if (c1Size == 1 || c2Size == 1){ 
			// c1.pos[0] < c2.pos[0]
			return solver.lt(c1.pos[0], c2.pos[0]);
		}else if (c1Size == 3 || c2Size == 3){
			// (c1.pos[0] < c2.pos[0]) || 
			// (c1.pos[0] == c2.pos[0] && 
			//		(c1.pos[1] != c2.pos[1] || c1.pos[2] < c2.pos[2])
			return solver.or(solver.lt(c1.pos[0], c2.pos[0]),
							 solver.and(solver.eq(c1.pos[0], c2.pos[0]),
							            solver.or(solver.neq(c1.pos[1], c2.pos[1]), solver.lt(c1.pos[2], c2.pos[2]))
							));
		}else {// if (c1Size == 5 || c2Size == 5) 
			// Pongo esto que sólo sirve en este ejemplo
			return solver.eq(c1.pos[2], c2.pos[2]);
		}
	}

	public String toString(){
		String s = "";
		for(TraceComponent t: ordering){
			s+= t + "\n";
		}
		return s;
	}

}
