package test;

import java.io.FileWriter;
import java.util.List;

import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import PetriNet.LPO.EventLpo;
import PetriNet.LPO.PetriNetLpo;
import PetriNet.LPO.TransitionLPO;
import es.idea.xes.XesUtils;
import ilog.concert.IloException;
import simpleNetSolver.SimplePetriNetSolverPlacesGoal;

public class TestRuns {

	public static final String runArchive = "/partialModels/Project_Petri-net_1469713665418/Project_Project_Petri-net_1469713665418.pnml_LPO_3_K-X-X-1469783108226.lpo";
	public static final String pnmlArchive = "/models/C_petri_pnml.xml";

	public static final String xesArchive = "/xes/C.xes";
	public static final String outputFileName = "C_petri.txt";
	public static final String add = "+complete";

	public static final Integer iteracionesInit = 0;
	public static final Integer iteracionesEnd = 20;

	public static void main(String[] args) throws Exception {

		PetriNetLpo lpo = new PetriNetLpo(runArchive);
		List<TransitionLPO> transitionsLpo = lpo.getTransitionsLPO();
		List<EventLpo> events = lpo.getEventLpo();
		List<TransitionPetriNet> transitions = lpo.constructTransitions();
		List<PlacePetriNet> places = lpo.constructPlaces();
		System.out.println("***** PetriNet info *****");
		System.out.println("Transitions num: " + transitions.size());
		System.out.println("Places num: " + places.size());
		System.out.println("Arcs num: " + lpo.getArcs().size());
		System.out.println("***** LPO info *****");
		System.out.println("Arcs num: " + transitionsLpo.size());
		System.out.println("Events num: " + events.size());

		for (int i = iteracionesInit; i < iteracionesEnd; i++) {
			FileWriter fichero = new FileWriter(outputFileName, true);
			EscribeFichero.execute("\n--------Iteración " + i + "  -------------\n", fichero);
			List<String> log = XesUtils.xesLog(xesArchive, i, lpo, add, fichero);
			fichero.close();

			FileWriter fichero2 = new FileWriter(outputFileName, true);

			try {

				SimplePetriNetSolverPlacesGoal petriSolver = new SimplePetriNetSolverPlacesGoal(
						transitions, places, log);

				petriSolver.obtainFloydMatrix();
				petriSolver.createVariables();
				petriSolver.createConstrains();
				petriSolver.DifferenceAligmentAndMinimize();

				long time_start = System.currentTimeMillis();
				petriSolver.search(fichero2);
				long current_time_end = System.currentTimeMillis();
				System.out.println(" Tiempo de ejecución iteración: " + i + "-->"
						+ ((double) current_time_end - (double) time_start) / 1000);

			} catch (IloException error) {
				System.out.println("An error occurred : " + error);
			}
			fichero2.close();

		}

	}

}
