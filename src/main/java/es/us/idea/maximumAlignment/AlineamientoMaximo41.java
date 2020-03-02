package es.us.idea.maximumAlignment;

import ilog.concert.IloException;
import ilog.solver.IlcConstraint;
import ilog.solver.IlcIntExpr;
import ilog.solver.IlcIntVar;
import ilog.solver.IlcSolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlineamientoMaximo41 {

	public static void main(String[] args) throws IloException{
		new AlineamientoMaximo41().run();
	}

	List<TraceComponent> ordering;

	TraceComponent AE1,AB,A,C,AA,AC1,AH,AF,AJ,AD1,AI,AE2,AG,AC2,AD2,AC3,AD3,D,B,AC4,AD4,AE3,AC5,AD5,AE4,AD6,AC6,AE5;

	public void run() throws IloException{
		IlcSolver solver = new IlcSolver();
		List<IlcIntVar> ABList = new LinkedList<IlcIntVar>();

		A = new TraceComponent("A", solver, 1, 0, 6); ABList.add(A.AB);
		C = new TraceComponent("C", solver, 1, 0, 6); ABList.add(C.AB);
		AA = new TraceComponent("AA", solver, 1, 0, 6); ABList.add(AA.AB);
		AE1 = new TraceComponent("AE1", solver, 3, 0, 14); ABList.add(AE1.AB);
		AC1 = new TraceComponent("AC1", solver, 3, 0, 14); ABList.add(AC1.AB);
		AH = new TraceComponent("AH", solver, 5, 0, 14); ABList.add(AH.AB);
		AC2 = new TraceComponent("AC2", solver, 3, 0, 14); ABList.add(AC2.AB);
		AD1 = new TraceComponent("AD1", solver, 3, 0, 14); ABList.add(AD1.AB);
		AE2 = new TraceComponent("AE2", solver, 3, 0, 14); ABList.add(AE2.AB);
		AI = new TraceComponent("AI", solver, 5, 0, 14); ABList.add(AI.AB);
		AD2 = new TraceComponent("AD2", solver, 3, 0, 14); ABList.add(AD2.AB);
		AG = new TraceComponent("AG", solver, 3, 0, 3); ABList.add(AG.AB);
		AD3 = new TraceComponent("AD3", solver, 3, 0, 14); ABList.add(AD3.AB);
		AC3 = new TraceComponent("AC3", solver, 3, 0, 14); ABList.add(AC3.AB);
		AF = new TraceComponent("AF", solver, 3, 0, 8); ABList.add(AF.AB);
		AC4 = new TraceComponent("AC4", solver, 3, 0, 14); ABList.add(AC4.AB);
		AD4 = new TraceComponent("AD4", solver, 3, 0, 14); ABList.add(AD4.AB);
		AE3 = new TraceComponent("AE3", solver, 3, 0, 14); ABList.add(AE3.AB);
        AC5 = new TraceComponent("AC5", solver, 3, 0, 14); ABList.add(AC5.AB);
        AE4 = new TraceComponent("AE4", solver, 3, 0, 14); ABList.add(AE4.AB);
		AC6 = new TraceComponent("AC6", solver, 3, 0, 14); ABList.add(AC6.AB);
		AD5 = new TraceComponent("AD5", solver, 3, 0, 14); ABList.add(AD5.AB);
		AE5 = new TraceComponent("AE5", solver, 3, 0, 14); ABList.add(AE5.AB);
		AJ = new TraceComponent("AJ", solver, 5, 0, 3); ABList.add(AJ.AB);
		AD6 = new TraceComponent("AD6", solver, 3, 0, 14); ABList.add(AD6.AB);
		AB = new TraceComponent("AB", solver, 1, 0, 6); ABList.add(AB.AB);
		D = new TraceComponent("D", solver, 1, 0, 6); ABList.add(D.AB);
		B = new TraceComponent("B", solver, 1, 0, 6); ABList.add(B.AB);

        ordering = new ArrayList<>();
        ordering.add(A);
        ordering.add(C);
        ordering.add(AA);
        ordering.add(AE1);
        ordering.add(AC1);
        ordering.add(AH);
        ordering.add(AC2);
        ordering.add(AD1);
        ordering.add(AE2);
        ordering.add(AI);
        ordering.add(AD2);
        ordering.add(AG);
        ordering.add(AD3);
        ordering.add(AC3);
        ordering.add(AF);
        ordering.add(AC4);
        ordering.add(AD4);
        ordering.add(AE3);
        ordering.add(AC5);
        ordering.add(AE4);
        ordering.add(AC6);
        ordering.add(AD5);
        ordering.add(AE5);
        ordering.add(AJ);
        ordering.add(AD6);
        ordering.add(AB);
        ordering.add(D);
        ordering.add(B);

		// *********** No abnormal *************

        solver.add(solver.imply(solver.eq(A.AB,0),solver.eq(A.pos[0], 0)));
        solver.add(solver.imply(solver.eq(C.AB,0),solver.eq(C.pos[0],1)));
        solver.add(solver.imply(solver.eq(AA.AB,0),solver.eq(AA.pos[0],2)));
        solver.add(solver.imply(solver.eq(AE1.AB,0),solver.and(solver.and(solver.eq(AE1.pos[0],3),solver.eq(AE1.pos[1],0)),
                solver.or(solver.or(solver.eq(AE1.pos[2],2),solver.eq(AE1.pos[2],5)),solver.or(solver.eq(AE1.pos[2],8),solver.eq(AE1.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AC1.AB,0),solver.and(solver.and(solver.eq(AC1.pos[0],3),solver.eq(AC1.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC1.pos[2],0),solver.eq(AC1.pos[2],3)),solver.or(solver.eq(AC1.pos[2],6),solver.eq(AC1.pos[2],9))),solver.eq(AC1.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AH.AB,0),solver.and(solver.and(solver.and(solver.eq(AH.pos[0],3),solver.eq(AH.pos[1],1)),solver.and(solver.eq(AH.pos[2],1),solver.eq(AH.pos[3],0))),solver.eq(AH.pos[4],0))));
        solver.add(solver.imply(solver.eq(AC2.AB,0),solver.and(solver.and(solver.eq(AC2.pos[0],3),solver.eq(AC2.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC2.pos[2],0),solver.eq(AC2.pos[2],3)),solver.or(solver.eq(AC2.pos[2],6),solver.eq(AC2.pos[2],9))),solver.eq(AC2.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AD1.AB,0),solver.and(solver.and(solver.eq(AD1.pos[0],3),solver.eq(AD1.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD1.pos[2],1),solver.eq(AD1.pos[2],4)),solver.or(solver.eq(AD1.pos[2],7),solver.eq(AD1.pos[2],10))),solver.eq(AD1.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AE2.AB,0),solver.and(solver.and(solver.eq(AE2.pos[0],3),solver.eq(AE2.pos[1],0)),
                solver.or(solver.or(solver.eq(AE2.pos[2],2),solver.eq(AE2.pos[2],5)),solver.or(solver.eq(AE2.pos[2],8),solver.eq(AE2.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AI.AB,0),solver.and(solver.and(solver.and(solver.eq(AI.pos[0],3),solver.eq(AI.pos[1],1)),solver.and(solver.eq(AI.pos[2],1),solver.eq(AI.pos[3],2))),solver.eq(AI.pos[4],0))));
        solver.add(solver.imply(solver.eq(AD2.AB,0),solver.and(solver.and(solver.eq(AD2.pos[0],3),solver.eq(AD2.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD2.pos[2],1),solver.eq(AD2.pos[2],4)),solver.or(solver.eq(AD2.pos[2],7),solver.eq(AD2.pos[2],10))),solver.eq(AD2.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AG.AB,0),solver.and(solver.and(solver.eq(AG.pos[0],3),solver.eq(AG.pos[1],1)),solver.eq(AG.pos[2],2))));
        solver.add(solver.imply(solver.eq(AD3.AB,0),solver.and(solver.and(solver.eq(AD3.pos[0],3),solver.eq(AD3.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD3.pos[2],1),solver.eq(AD3.pos[2],4)),solver.or(solver.eq(AD3.pos[2],7),solver.eq(AD3.pos[2],10))),solver.eq(AD3.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AC3.AB,0),solver.and(solver.and(solver.eq(AC3.pos[0],3),solver.eq(AC3.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC3.pos[2],0),solver.eq(AC3.pos[2],3)),solver.or(solver.eq(AC3.pos[2],6),solver.eq(AC3.pos[2],9))),solver.eq(AC3.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AF.AB,0),solver.and(solver.and(solver.eq(AF.pos[0],3),solver.eq(AF.pos[1],1)),solver.eq(AF.pos[2],0))));
        solver.add(solver.imply(solver.eq(AC4.AB,0),solver.and(solver.and(solver.eq(AC4.pos[0],3),solver.eq(AC4.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC4.pos[2],0),solver.eq(AC4.pos[2],3)),solver.or(solver.eq(AC4.pos[2],6),solver.eq(AC4.pos[2],9))),solver.eq(AC4.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AD4.AB,0),solver.and(solver.and(solver.eq(AD4.pos[0],3),solver.eq(AD4.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD4.pos[2],1),solver.eq(AD4.pos[2],4)),solver.or(solver.eq(AD4.pos[2],7),solver.eq(AD4.pos[2],10))),solver.eq(AD4.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AE3.AB,0),solver.and(solver.and(solver.eq(AE3.pos[0],3),solver.eq(AE3.pos[1],0)),
                solver.or(solver.or(solver.eq(AE3.pos[2],2),solver.eq(AE3.pos[2],5)),solver.or(solver.eq(AE3.pos[2],8),solver.eq(AE3.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AC5.AB,0),solver.and(solver.and(solver.eq(AC5.pos[0],3),solver.eq(AC5.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC5.pos[2],0),solver.eq(AC5.pos[2],3)),solver.or(solver.eq(AC5.pos[2],6),solver.eq(AC5.pos[2],9))),solver.eq(AC5.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AE4.AB,0),solver.and(solver.and(solver.eq(AE4.pos[0],3),solver.eq(AE4.pos[1],0)),
                solver.or(solver.or(solver.eq(AE4.pos[2],2),solver.eq(AE4.pos[2],5)),solver.or(solver.eq(AE4.pos[2],8),solver.eq(AE4.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AC6.AB,0),solver.and(solver.and(solver.eq(AC6.pos[0],3),solver.eq(AC6.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AC6.pos[2],0),solver.eq(AC6.pos[2],3)),solver.or(solver.eq(AC6.pos[2],6),solver.eq(AC6.pos[2],9))),solver.eq(AC6.pos[2],12)))));
        solver.add(solver.imply(solver.eq(AD5.AB,0),solver.and(solver.and(solver.eq(AD5.pos[0],3),solver.eq(AD5.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD5.pos[2],1),solver.eq(AD5.pos[2],4)),solver.or(solver.eq(AD5.pos[2],7),solver.eq(AD5.pos[2],10))),solver.eq(AD5.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AE5.AB,0),solver.and(solver.and(solver.eq(AE5.pos[0],3),solver.eq(AE5.pos[1],0)),
                solver.or(solver.or(solver.eq(AE5.pos[2],2),solver.eq(AE5.pos[2],5)),solver.or(solver.eq(AE5.pos[2],8),solver.eq(AE5.pos[2],11))))));
        solver.add(solver.imply(solver.eq(AJ.AB,0),solver.and(solver.and(solver.and(solver.eq(AJ.pos[0],3),solver.eq(AJ.pos[1],1)),solver.and(solver.eq(AJ.pos[2],1),solver.eq(AJ.pos[3],1))),solver.eq(AJ.pos[4],0))));
        solver.add(solver.imply(solver.eq(AD6.AB,0),solver.and(solver.and(solver.eq(AD6.pos[0],3),solver.eq(AD6.pos[1],0)),
                solver.or(solver.or(solver.or(solver.eq(AD6.pos[2],1),solver.eq(AD6.pos[2],4)),solver.or(solver.eq(AD6.pos[2],7),solver.eq(AD6.pos[2],10))),solver.eq(AD6.pos[2],13)))));
        solver.add(solver.imply(solver.eq(AB.AB,0),solver.eq(AB.pos[0], 4)));
        solver.add(solver.imply(solver.eq(D.AB,0),solver.eq(D.pos[0],5)));
        solver.add(solver.imply(solver.eq(B.AB,0),solver.eq(B.pos[0],6)));

        // *********** Parejas *************

        for(int i = 0; i < ordering.size(); i++){
            for(int j = i+1; j < ordering.size(); j++){
                solver.add(solver.imply(solver.and(solver.eq(ordering.get(i).AB, 0), solver.eq(ordering.get(j).AB, 0)), menorIgual(solver, ordering.get(i), ordering.get(j))));
            }
        }

		//

		IlcIntExpr[] ABRefs = new IlcIntExpr[ABList.size()];
		for (int i = 0; i < ABList.size(); i++){
			ABRefs[i] = ABList.get(i);
		}
		IlcIntVar sumRefE = solver.intVar(0, ABList.size());//ABList.size() - 7
		solver.add(solver.eq(sumRefE, solver.sum(ABRefs)));
		solver.add(solver.minimize(sumRefE, 1)); // 1 0
		solver.newSearch(solver.generate(ABRefs));
		long start = System.currentTimeMillis();
		while (solver.next()) {
			System.out.println("********************************************");
			//System.out.println("Alineamiento: " + ((17 - (19 - sumRefE.getDomainValue())) + sumRefE.getDomainValue()));
            System.out.println("Alineamiento: " + (2* sumRefE.getDomainValue() + 4)); //pasamos... ya simplemente ponemos aqui el numero de unos
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
			// Pongo esto que s�lo sirve en este ejemplo
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
