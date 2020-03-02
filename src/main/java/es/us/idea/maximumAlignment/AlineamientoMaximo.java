package es.us.idea.maximumAlignment;
import java.util.*;
import ilog.concert.IloException;
import ilog.solver.*;

public class AlineamientoMaximo {

	public static void main(String[] args) throws IloException{
		new AlineamientoMaximo().run();
	}
	
	TraceComponent A,AA,C,AC1,D,AD1,AD2,AE1,AH,AI,AE2,AG,AC2,AD3,AS,AC3,AB,AF,B;

	public void run() throws IloException{
		IlcSolver solver = new IlcSolver();
		List<IlcIntVar> ABList = new LinkedList<IlcIntVar>();
		
		A = new TraceComponent("A", solver, 1, 0, 6); ABList.add(A.AB);
		AA = new TraceComponent("AA", solver, 1, 0, 6); ABList.add(AA.AB);
		C = new TraceComponent("C", solver, 1, 0, 6); ABList.add(C.AB);
		AC1 = new TraceComponent("AC1", solver, 3, 0, 6); ABList.add(AC1.AB);
		D = new TraceComponent("D", solver, 1, 0, 6); ABList.add(D.AB);
		AD1 = new TraceComponent("AD1", solver, 3, 0, 6); ABList.add(AD1.AB);
		AD2 = new TraceComponent("AD2", solver, 3, 0, 6); ABList.add(AD2.AB);
		AE1 = new TraceComponent("AE1", solver, 3, 0, 6); ABList.add(AE1.AB);
		AH = new TraceComponent("AH", solver, 5, 0, 6); ABList.add(AH.AB);
		AI = new TraceComponent("AI", solver, 5, 0, 6); ABList.add(AI.AB);
		AE2 = new TraceComponent("AE2", solver, 3, 0, 6); ABList.add(AE2.AB);
		AG = new TraceComponent("AG", solver, 3, 0, 6); ABList.add(AG.AB);
		AC2 = new TraceComponent("AC2", solver, 3, 0, 6); ABList.add(AC2.AB);
		AD3 = new TraceComponent("AD3", solver, 3, 0, 6); ABList.add(AD3.AB);
		AS = new TraceComponent("AS", solver, 5, 0, 6); ABList.add(AS.AB);
		AC3 = new TraceComponent("AC3", solver, 3, 0, 6); ABList.add(AC3.AB);
		AB = new TraceComponent("AB", solver, 1, 0, 6); ABList.add(AB.AB);
		AF = new TraceComponent("AF", solver, 3, 0, 6); ABList.add(AF.AB);
		B = new TraceComponent("B", solver, 1, 0, 6); ABList.add(B.AB);
		// *********** No abnormal *************
		// !AB(A) -> p(A) = 0
		solver.add(solver.imply(solver.eq(A.AB, 0),solver.eq(A.pos[0], 0)));
		// !AB(AA) -> p(AA) = 2
		solver.add(solver.imply(solver.eq(AA.AB, 0),solver.eq(AA.pos[0], 2)));
		// !AB(C) -> p(C) = 1
		solver.add(solver.imply(solver.eq(C.AB, 0),solver.eq(C.pos[0], 1)));
		// !AB(AC1) -> p(AC1) = 30(0|3)
		solver.add(solver.imply(solver.eq(AC1.AB, 0),solver.and(solver.and(solver.eq(AC1.pos[0], 3),solver.eq(AC1.pos[1], 0)),solver.or(solver.eq(AC1.pos[2], 0),solver.eq(AC1.pos[2], 3)))));
		// !AB(D) -> p(D) = 5
		solver.add(solver.imply(solver.eq(D.AB, 0),solver.eq(D.pos[0], 5)));
		// !AB(AD1) -> p(AD1) = 30(1|4)
		solver.add(solver.imply(solver.eq(AD1.AB, 0),solver.and(solver.and(solver.eq(AD1.pos[0], 3),solver.eq(AD1.pos[1], 0)),solver.or(solver.eq(AD1.pos[2], 1),solver.eq(AD1.pos[2], 4)))));
		// !AB(AD2) -> p(AD2) = 30(1|4)
		solver.add(solver.imply(solver.eq(AD2.AB, 0),solver.and(solver.and(solver.eq(AD2.pos[0], 3),solver.eq(AD2.pos[1], 0)),solver.or(solver.eq(AD2.pos[2], 1),solver.eq(AD2.pos[2], 4)))));
		// !AB(AE1) -> p(AE1) = 302
		solver.add(solver.imply(solver.eq(AE1.AB, 0),solver.and(solver.eq(AE1.pos[0], 3),solver.and(solver.eq(AE1.pos[1], 0),solver.eq(AE1.pos[2], 2)))));
		// !AB(AH) -> p(AH) = 31100
		solver.add(solver.imply(solver.eq(AH.AB, 0),solver.and(solver.and(solver.eq(AH.pos[0], 3),solver.eq(AH.pos[1], 1)),solver.and(solver.eq(AH.pos[2], 1),solver.and(solver.eq(AH.pos[3], 0),solver.eq(AH.pos[4], 0))))));
		// !AB(AI) -> p(AI) = 31110
		solver.add(solver.imply(solver.eq(AI.AB, 0),solver.and(solver.and(solver.eq(AI.pos[0], 3),solver.eq(AI.pos[1], 1)),solver.and(solver.eq(AI.pos[2], 1),solver.and(solver.eq(AI.pos[3], 1),solver.eq(AI.pos[4], 0))))));
		// !AB(AE2) -> p(AE2) = 302
		solver.add(solver.imply(solver.eq(AE2.AB, 0),solver.and(solver.eq(AE2.pos[0], 3),solver.and(solver.eq(AE2.pos[1], 0),solver.eq(AE2.pos[2], 2)))));
		// !AB(AG) -> p(AG) = 312
		solver.add(solver.imply(solver.eq(AG.AB, 0),solver.and(solver.eq(AG.pos[0], 3),solver.and(solver.eq(AG.pos[1], 1),solver.eq(AG.pos[2], 2)))));
		// !AB(AC2) -> p(AC2) = 30(0|3)
		solver.add(solver.imply(solver.eq(AC2.AB, 0),solver.and(solver.and(solver.eq(AC2.pos[0], 3),solver.eq(AC2.pos[1], 0)),solver.or(solver.eq(AC2.pos[2], 0),solver.eq(AC2.pos[2], 3)))));
		// !AB(AD3) -> p(AD3) = 30(1|4)
		solver.add(solver.imply(solver.eq(AD3.AB, 0),solver.and(solver.and(solver.eq(AD3.pos[0], 3),solver.eq(AD3.pos[1], 0)),solver.or(solver.eq(AD3.pos[2], 1),solver.eq(AD3.pos[2], 4)))));
		// !AB(AS) -> p(AS) = 31120
		solver.add(solver.imply(solver.eq(AS.AB, 0),solver.and(solver.and(solver.eq(AS.pos[0], 3),solver.eq(AS.pos[1], 1)),solver.and(solver.eq(AS.pos[2], 1),solver.and(solver.eq(AS.pos[3], 2),solver.eq(AS.pos[4], 0))))));
		// !AB(AC3) -> p(AC3) = 30(0|3)
		solver.add(solver.imply(solver.eq(AC3.AB, 0),solver.and(solver.and(solver.eq(AC3.pos[0], 3),solver.eq(AC3.pos[1], 0)),solver.or(solver.eq(AC3.pos[2], 0),solver.eq(AC3.pos[2], 3)))));
		// !AB(AB) -> p(AB) = 4
		solver.add(solver.imply(solver.eq(AB.AB, 0),solver.eq(AB.pos[0], 4)));
		// !AB(AF) -> p(AF) = 310
		solver.add(solver.imply(solver.eq(AF.AB, 0),solver.and(solver.eq(AF.pos[0], 3),solver.and(solver.eq(AF.pos[1], 1),solver.eq(AF.pos[2], 0)))));
		// !AB(B) -> p(B) = 6
		solver.add(solver.imply(solver.eq(B.AB, 0),solver.eq(B.pos[0], 6)));

		// *********** Parejas *************

		// !AB(A) && !AB(AA) -> p(A) <= p(AA)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AA.AB, 0)), menorIgual(solver, A, AA)));
		// !AB(A) && !AB(C) -> p(A) <= p(C)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(C.AB, 0)), menorIgual(solver, A, C)));
		// !AB(A) && !AB(AC1) -> p(A) <= p(AC1)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AC1.AB, 0)), menorIgual(solver, A, AC1)));
		// !AB(A) && !AB(D) -> p(A) <= p(D)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(D.AB, 0)), menorIgual(solver, A, D)));
		// !AB(A) && !AB(AD1) -> p(A) <= p(AD1)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AD1.AB, 0)), menorIgual(solver, A, AD1)));
		// !AB(A) && !AB(AD2) -> p(A) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, A, AD2)));
		// !AB(A) && !AB(AE1) -> p(A) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, A, AE1)));
		// !AB(A) && !AB(AH) -> p(A) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, A, AH)));
		// !AB(A) && !AB(AI) -> p(A) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, A, AI)));
		// !AB(A) && !AB(AE2) -> p(A) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, A, AE2)));
		// !AB(A) && !AB(AG) -> p(A) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, A, AG)));
		// !AB(A) && !AB(AC2) -> p(A) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, A, AC2)));
		// !AB(A) && !AB(AD3) -> p(A) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, A, AD3)));
		// !AB(A) && !AB(AS) -> p(A) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, A, AS)));
		// !AB(A) && !AB(AC3) -> p(A) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, A, AC3)));
		// !AB(A) && !AB(AB) -> p(A) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, A, AB)));
		// !AB(A) && !AB(AF) -> p(A) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, A, AF)));
		// !AB(A) && !AB(B) -> p(A) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(A.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, A, B)));

		// !AB(AA) && !AB(C) -> p(AA) <= p(C)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(C.AB, 0)), menorIgual(solver, AA, C)));
		// !AB(AA) && !AB(AC1) -> p(AA) <= p(AC1)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AC1.AB, 0)), menorIgual(solver, AA, AC1)));
		// !AB(AA) && !AB(D) -> p(AA) <= p(D)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(D.AB, 0)), menorIgual(solver, AA, D)));
		// !AB(AA) && !AB(AD1) -> p(AA) <= p(AD1)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AD1.AB, 0)), menorIgual(solver, AA, AD1)));
		// !AB(AA) && !AB(AD2) -> p(AA) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, AA, AD2)));
		// !AB(AA) && !AB(AE1) -> p(AA) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, AA, AE1)));
		// !AB(AA) && !AB(AH) -> p(AA) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, AA, AH)));
		// !AB(AA) && !AB(AI) -> p(AA) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AA, AI)));
		// !AB(AA) && !AB(AE2) -> p(AA) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AA, AE2)));
		// !AB(AA) && !AB(AG) -> p(AA) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AA, AG)));
		// !AB(AA) && !AB(AC2) -> p(AA) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AA, AC2)));
		// !AB(AA) && !AB(AD3) -> p(AA) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AA, AD3)));
		// !AB(AA) && !AB(AS) -> p(AA) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AA, AS)));
		// !AB(AA) && !AB(AC3) -> p(AA) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AA, AC3)));
		// !AB(AA) && !AB(AB) -> p(AA) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AA, AB)));
		// !AB(AA) && !AB(AF) -> p(AA) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AA, AF)));
		// !AB(AA) && !AB(B) -> p(AA) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AA.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AA, B)));

		// !AB(C) && !AB(AC1) -> p(C) <= p(AC1)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AC1.AB, 0)), menorIgual(solver, C, AC1)));
		// !AB(C) && !AB(D) -> p(C) <= p(D)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(D.AB, 0)), menorIgual(solver, C, D)));
		// !AB(C) && !AB(AD1) -> p(C) <= p(AD1)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AD1.AB, 0)), menorIgual(solver, C, AD1)));
		// !AB(C) && !AB(AD2) -> p(C) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, C, AD2)));
		// !AB(C) && !AB(AE1) -> p(C) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, C, AE1)));
		// !AB(C) && !AB(AH) -> p(C) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, C, AH)));
		// !AB(C) && !AB(AI) -> p(C) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, C, AI)));
		// !AB(C) && !AB(AE2) -> p(C) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, C, AE2)));
		// !AB(C) && !AB(AG) -> p(C) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, C, AG)));
		// !AB(C) && !AB(AC2) -> p(C) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, C, AC2)));
		// !AB(C) && !AB(AD3) -> p(C) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, C, AD3)));
		// !AB(C) && !AB(AS) -> p(C) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, C, AS)));
		// !AB(C) && !AB(AC3) -> p(C) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, C, AC3)));
		// !AB(C) && !AB(AB) -> p(C) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, C, AB)));
		// !AB(C) && !AB(AF) -> p(C) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, C, AF)));
		// !AB(C) && !AB(B) -> p(C) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(C.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, C, B)));

		// !AB(AC1) && !AB(D) -> p(AC1) <= p(D)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(D.AB, 0)), menorIgual(solver, AC1, D)));
		// !AB(AC1) && !AB(AD1) -> p(AC1) <= p(AD1)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AD1.AB, 0)), menorIgual(solver, AC1, AD1)));
		// !AB(AC1) && !AB(AD2) -> p(AC1) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, AC1, AD2)));
		// !AB(AC1) && !AB(AE1) -> p(AC1) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, AC1, AE1)));
		// !AB(AC1) && !AB(AH) -> p(AC1) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, AC1, AH)));
		// !AB(AC1) && !AB(AI) -> p(AC1) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AC1, AI)));
		// !AB(AC1) && !AB(AE2) -> p(AC1) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AC1, AE2)));
		// !AB(AC1) && !AB(AG) -> p(AC1) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AC1, AG)));
		// !AB(AC1) && !AB(AC2) -> p(AC1) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AC1, AC2)));
		// !AB(AC1) && !AB(AD3) -> p(AC1) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AC1, AD3)));
		// !AB(AC1) && !AB(AS) -> p(AC1) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AC1, AS)));
		// !AB(AC1) && !AB(AC3) -> p(AC1) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AC1, AC3)));
		// !AB(AC1) && !AB(AB) -> p(AC1) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AC1, AB)));
		// !AB(AC1) && !AB(AF) -> p(AC1) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AC1, AF)));
		// !AB(AC1) && !AB(B) -> p(AC1) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AC1.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AC1, B)));

		// !AB(D) && !AB(AD1) -> p(D) <= p(AD1)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AD1.AB, 0)), menorIgual(solver, D, AD1)));
		// !AB(D) && !AB(AD2) -> p(D) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, D, AD2)));
		// !AB(D) && !AB(AE1) -> p(D) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, D, AE1)));
		// !AB(D) && !AB(AH) -> p(D) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, D, AH)));
		// !AB(D) && !AB(AI) -> p(D) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, D, AI)));
		// !AB(D) && !AB(AE2) -> p(D) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, D, AE2)));
		// !AB(D) && !AB(AG) -> p(D) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, D, AG)));
		// !AB(D) && !AB(AC2) -> p(D) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, D, AC2)));
		// !AB(D) && !AB(AD3) -> p(D) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, D, AD3)));
		// !AB(D) && !AB(AS) -> p(D) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, D, AS)));
		// !AB(D) && !AB(AC3) -> p(D) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, D, AC3)));
		// !AB(D) && !AB(AB) -> p(D) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, D, AB)));
		// !AB(D) && !AB(AF) -> p(D) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, D, AF)));
		// !AB(D) && !AB(B) -> p(D) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(D.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, D, B)));

		// !AB(AD1) && !AB(AD2) -> p(AD1) <= p(AD2)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AD2.AB, 0)), menorIgual(solver, AD1, AD2)));
		// !AB(AD1) && !AB(AE1) -> p(AD1) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, AD1, AE1)));
		// !AB(AD1) && !AB(AH) -> p(AD1) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, AD1, AH)));
		// !AB(AD1) && !AB(AI) -> p(AD1) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AD1, AI)));
		// !AB(AD1) && !AB(AE2) -> p(AD1) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AD1, AE2)));
		// !AB(AD1) && !AB(AG) -> p(AD1) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AD1, AG)));
		// !AB(AD1) && !AB(AC2) -> p(AD1) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AD1, AC2)));
		// !AB(AD1) && !AB(AD3) -> p(AD1) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AD1, AD3)));
		// !AB(AD1) && !AB(AS) -> p(AD1) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AD1, AS)));
		// !AB(AD1) && !AB(AC3) -> p(AD1) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AD1, AC3)));
		// !AB(AD1) && !AB(AB) -> p(AD1) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AD1, AB)));
		// !AB(AD1) && !AB(AF) -> p(AD1) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AD1, AF)));
		// !AB(AD1) && !AB(B) -> p(AD1) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AD1.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AD1, B)));

		// !AB(AD2) && !AB(AE1) -> p(AD2) <= p(AE1)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AE1.AB, 0)), menorIgual(solver, AD2, AE1)));
		// !AB(AD2) && !AB(AH) -> p(AD2) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, AD2, AH)));
		// !AB(AD2) && !AB(AI) -> p(AD2) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AD2, AI)));
		// !AB(AD2) && !AB(AE2) -> p(AD2) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AD2, AE2)));
		// !AB(AD2) && !AB(AG) -> p(AD2) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AD2, AG)));
		// !AB(AD2) && !AB(AC2) -> p(AD2) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AD2, AC2)));
		// !AB(AD2) && !AB(AD3) -> p(AD2) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AD2, AD3)));
		// !AB(AD2) && !AB(AS) -> p(AD2) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AD2, AS)));
		// !AB(AD2) && !AB(AC3) -> p(AD2) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AD2, AC3)));
		// !AB(AD2) && !AB(AB) -> p(AD2) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AD2, AB)));
		// !AB(AD2) && !AB(AF) -> p(AD2) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AD2, AF)));
		// !AB(AD2) && !AB(B) -> p(AD2) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AD2.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AD2, B)));

		// !AB(AE1) && !AB(AH) -> p(AE1) <= p(AH)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AH.AB, 0)), menorIgual(solver, AE1, AH)));
		// !AB(AE1) && !AB(AI) -> p(AE1) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AE1, AI)));
		// !AB(AE1) && !AB(AE2) -> p(AE1) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AE1, AE2)));
		// !AB(AE1) && !AB(AG) -> p(AE1) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AE1, AG)));
		// !AB(AE1) && !AB(AC2) -> p(AE1) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AE1, AC2)));
		// !AB(AE1) && !AB(AD3) -> p(AE1) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AE1, AD3)));
		// !AB(AE1) && !AB(AS) -> p(AE1) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AE1, AS)));
		// !AB(AE1) && !AB(AC3) -> p(AE1) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AE1, AC3)));
		// !AB(AE1) && !AB(AB) -> p(AE1) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AE1, AB)));
		// !AB(AE1) && !AB(AF) -> p(AE1) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AE1, AF)));
		// !AB(AE1) && !AB(B) -> p(AE1) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AE1.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AE1, B)));

		// !AB(AH) && !AB(AI) -> p(AH) <= p(AI)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AI.AB, 0)), menorIgual(solver, AH, AI)));
		// !AB(AH) && !AB(AE2) -> p(AH) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AH, AE2)));
		// !AB(AH) && !AB(AG) -> p(AH) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AH, AG)));
		// !AB(AH) && !AB(AC2) -> p(AH) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AH, AC2)));
		// !AB(AH) && !AB(AD3) -> p(AH) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AH, AD3)));
		// !AB(AH) && !AB(AS) -> p(AH) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AH, AS)));
		// !AB(AH) && !AB(AC3) -> p(AH) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AH, AC3)));
		// !AB(AH) && !AB(AB) -> p(AH) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AH, AB)));
		// !AB(AH) && !AB(AF) -> p(AH) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AH, AF)));
		// !AB(AH) && !AB(B) -> p(AH) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AH.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AH, B)));

		// !AB(AI) && !AB(AE2) -> p(AI) <= p(AE2)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AE2.AB, 0)), menorIgual(solver, AI, AE2)));
		// !AB(AI) && !AB(AG) -> p(AI) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AI, AG)));
		// !AB(AI) && !AB(AC2) -> p(AI) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AI, AC2)));
		// !AB(AI) && !AB(AD3) -> p(AI) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AI, AD3)));
		// !AB(AI) && !AB(AS) -> p(AI) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AI, AS)));
		// !AB(AI) && !AB(AC3) -> p(AI) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AI, AC3)));
		// !AB(AI) && !AB(AB) -> p(AI) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AI, AB)));
		// !AB(AI) && !AB(AF) -> p(AI) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AI, AF)));
		// !AB(AI) && !AB(B) -> p(AI) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AI.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AI, B)));

		// !AB(AE2) && !AB(AG) -> p(AE2) <= p(AG)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AG.AB, 0)), menorIgual(solver, AE2, AG)));
		// !AB(AE2) && !AB(AC2) -> p(AE2) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AE2, AC2)));
		// !AB(AE2) && !AB(AD3) -> p(AE2) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AE2, AD3)));
		// !AB(AE2) && !AB(AS) -> p(AE2) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AE2, AS)));
		// !AB(AE2) && !AB(AC3) -> p(AE2) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AE2, AC3)));
		// !AB(AE2) && !AB(AB) -> p(AE2) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AE2, AB)));
		// !AB(AE2) && !AB(AF) -> p(AE2) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AE2, AF)));
		// !AB(AE2) && !AB(B) -> p(AE2) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AE2.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AE2, B)));

		// !AB(AG) && !AB(AC2) -> p(AG) <= p(AC2)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AC2.AB, 0)), menorIgual(solver, AG, AC2)));
		// !AB(AG) && !AB(AD3) -> p(AG) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AG, AD3)));
		// !AB(AG) && !AB(AS) -> p(AG) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AG, AS)));
		// !AB(AG) && !AB(AC3) -> p(AG) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AG, AC3)));
		// !AB(AG) && !AB(AB) -> p(AG) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AG, AB)));
		// !AB(AG) && !AB(AF) -> p(AG) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AG, AF)));
		// !AB(AG) && !AB(B) -> p(AG) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AG.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AG, B)));

		// !AB(AC2) && !AB(AD3) -> p(AC2) <= p(AD3)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(AD3.AB, 0)), menorIgual(solver, AC2, AD3)));
		// !AB(AC2) && !AB(AS) -> p(AC2) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AC2, AS)));
		// !AB(AC2) && !AB(AC3) -> p(AC2) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AC2, AC3)));
		// !AB(AC2) && !AB(AB) -> p(AC2) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AC2, AB)));
		// !AB(AC2) && !AB(AF) -> p(AC2) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AC2, AF)));
		// !AB(AC2) && !AB(B) -> p(AC2) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AC2.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AC2, B)));

		// !AB(AD3) && !AB(AS) -> p(AD3) <= p(AS)
		solver.add(solver.imply(solver.and(solver.eq(AD3.AB, 0), solver.eq(AS.AB, 0)), menorIgual(solver, AD3, AS)));
		// !AB(AD3) && !AB(AC3) -> p(AD3) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AD3.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AD3, AC3)));
		// !AB(AD3) && !AB(AB) -> p(AD3) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AD3.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AD3, AB)));
		// !AB(AD3) && !AB(AF) -> p(AD3) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AD3.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AD3, AF)));
		// !AB(AD3) && !AB(B) -> p(AD3) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AD3.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AD3, B)));

		// !AB(AS) && !AB(AC3) -> p(AS) <= p(AC3)
		solver.add(solver.imply(solver.and(solver.eq(AS.AB, 0), solver.eq(AC3.AB, 0)), menorIgual(solver, AS, AC3)));
		// !AB(AS) && !AB(AB) -> p(AS) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AS.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AS, AB)));
		// !AB(AS) && !AB(AF) -> p(AS) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AS.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AS, AF)));
		// !AB(AS) && !AB(B) -> p(AS) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AS.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AS, B)));

		// !AB(AC3) && !AB(AB) -> p(AC3) <= p(AB)
		solver.add(solver.imply(solver.and(solver.eq(AC3.AB, 0), solver.eq(AB.AB, 0)), menorIgual(solver, AC3, AB)));
		// !AB(AC3) && !AB(AF) -> p(AC3) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AC3.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AC3, AF)));
		// !AB(AC3) && !AB(B) -> p(AC3) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AC3.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AC3, B)));

		// !AB(AB) && !AB(AF) -> p(AB) <= p(AF)
		solver.add(solver.imply(solver.and(solver.eq(AB.AB, 0), solver.eq(AF.AB, 0)), menorIgual(solver, AB, AF)));
		// !AB(AB) && !AB(B) -> p(AB) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AB.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AB, B)));

		// !AB(AF) && !AB(B) -> p(AF) <= p(B)
		solver.add(solver.imply(solver.and(solver.eq(AF.AB, 0), solver.eq(B.AB, 0)), menorIgual(solver, AF, B)));
		

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
			System.out.println("Alineamiento: " + ((17 - (19 - sumRefE.getDomainValue())) + sumRefE.getDomainValue()));
			//System.out.println("Alineamiento: " + (2*sumRefE.getDomainValue()+17-19));
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
		return A + "\n" + AA + "\n" + C + "\n" + AC1 + "\n" + D + "\n" + AD1 + "\n" + AD2 + "\n" + AE1 + "\n" + AH + "\n" + AI + "\n" + AE2 + "\n" + AG + "\n" + AC2 + "\n" + AD3 + "\n" + AS + "\n" + AC3 + "\n" + AB + "\n" + AF + "\n" + B;
	}

}
