package test;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import PetriNet.PetriNet;
import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import ilog.concert.IloException;
import simpleNetSolver.*;

public class SimplePetriNetEjemplosTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*
		 * System.out.println("Para A");
		 * 
		 * long time_start = System.currentTimeMillis();
		 * makeTest2("/models/A_petri_pnml.xml","/xes/A.xes","+complete",
		 * "A_petri.txt", 0, 1199); long current_time_end =
		 * System.currentTimeMillis();
		 * System.out.println(" Tiempo de ejecuci�n De todas las iteraciones:  "
		 * +((double)current_time_end-(double)time_start)/1000);
		 */

		/*
		 * long time_start = System.currentTimeMillis();
		 * System.out.println("Para B");
		 * makeTest2("/models/B_petri_pnml.xml","/xes/B.xes","+complete",
		 * "B_petri.txt", 0,1199);//1200 long current_time_end =
		 * System.currentTimeMillis();
		 * System.out.println(" Tiempo de ejecuci�n De todas las iteraciones:  "
		 * +((double)current_time_end-(double)time_start)/1000);
		 */

		System.out.println("Para C");
		long time_start = System.currentTimeMillis();
		makeTest2("/models/C_petri_pnml.xml", "/xes/C.xes", "+complete", "C_petri.txt", 0, 499);// 500
		long current_time_end = System.currentTimeMillis();
		System.out.println(" Tiempo de ejecuci�n De todas las iteraciones:  "
				+ ((double) current_time_end - (double) time_start) / 1000);
		/*
		 * System.out.println("Para D");
		 * makeTest2("/models/D_petri_pnml.xml","/xes/D.xes","+complete",
		 * "D_petri.txt", 4,100);//1200
		 */
		/*
		 * System.out.println("Para E"); long time_start =
		 * System.currentTimeMillis();
		 * makeTest2("/models/E_petri_pnml.xml","/xes/E.xes","+complete",
		 * "E_petri.txt", 0,1199);//1200 long current_time_end =
		 * System.currentTimeMillis();
		 * System.out.println(" Tiempo de ejecuci�n De todas las iteraciones:  "
		 * +((double)current_time_end-(double)time_start)/1000);
		 */
		/*
		 * long time_start = System.currentTimeMillis();
		 * System.out.println("Para F");
		 * makeTest2("/models/F_petri_pnml.xml","/xes/F.xes","+complete",
		 * "F_petri.txt", 0,1199);//550
		 * 
		 * long current_time_end = System.currentTimeMillis();
		 * System.out.println(" Tiempo de ejecuci�n De todas las iteraciones:  "
		 * +((double)current_time_end-(double)time_start)/1000);
		 */
		/*
		 * System.out.println("Para G");
		 * makeTest2("/models/G_petri_pnml.xml","/xes/G.xes","+complete",
		 * "G_petri.txt", 4,100);//555
		 */

		// makeTest2("/models/E_petri_pnml.xml","E.xes","+complete","E_petri.txt",1200);

	}

	public static void makeTest(String pnmlPath, String xesFilename, String ficheroName, Integer iteraciones)
			throws Exception {
		//

		PetriNet net = new PetriNet(pnmlPath);
		List<TransitionPetriNet> transitions = net.constructTransitions();
		List<PlacePetriNet> places = net.constructPlaces();
		System.out.println("Transitions num: " + transitions.size());
		System.out.println("Places num: " + places.size());

		for (int i = 0; i < iteraciones; i++) {
			FileWriter fichero = new FileWriter(ficheroName, true);

			EscribeFichero.execute("\n--------Iteraci�n " + i + "  -------------\n", fichero);

			List<String> log = xesLog(xesFilename, net, i, fichero);

			fichero.close();

			FileWriter fichero2 = new FileWriter(ficheroName, true);

			try {
				SimplePetriNetSolverPlaces petriSolver = new SimplePetriNetSolverPlaces(transitions, places, log);
				petriSolver.createConstrains();
				petriSolver.DifferenceAligmentAndMinimize();
				petriSolver.search(fichero2);

			} catch (IloException error) {
				System.out.println("An error occurred : " + error);
			}
			fichero2.close();

		}

	}

	public static List<String> xesLog(String xesPath, PetriNet net, Integer index, FileWriter fichero)
			throws Exception {
		// String xesFilePath="/xes/2000-all-noises.xes";
		// String xesFileAbsolute =
		// "C:/Users/David/workspace/conformanceCheking/ConfCheck/src/main/resources/xes/2000-all-noise.xes";

		File xesFile = new File(SimplePetriNetEjemplosTest.class.getResource(xesPath).getFile());
		XesXmlParser xparser = new XesXmlParser();
		//
		boolean a = xparser.canParse(xesFile);
		// System.out.println(a);

		List<XLog> xLogList = xparser.parse(xesFile);
		//
		XLog b = xLogList.get(0);
		// System.out.println(b.size());

		// CAMBIAR PARA VARIAR TRAZA
		XTrace oneTrace = b.get(index);

		// XEvent oneEvent=oneTrace.get(0);

		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		List<TransitionPetriNet> transitions = net.constructTransitions();
		List<PlacePetriNet> places = net.constructPlaces();

		//
		for (XEvent x : oneTrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			logs_xes.add(net.nameToId(x.getAttributes().get("concept:name").toString(), transitions));
		}

		String resultLog = "Logs Sin traducir:" + logs_xesSinTraducir + "\n";
		resultLog = resultLog + "Logs traducido" + logs_xes + "\n";
		EscribeFichero.execute(resultLog, fichero);

		return logs_xes;
	}

	
	
	public static void makeTest2(String pnmlPath, String xesFilename, String add, String ficheroName,
			Integer iteracionesIni, Integer iteracionesFin) throws Exception {

		// String pnml="/banktransfer_mini.pnml";
		//

		PetriNet net = new PetriNet(pnmlPath);
		List<TransitionPetriNet> transitions = net.constructTransitions();
		List<PlacePetriNet> places = net.constructPlaces();
		System.out.println("Transitions num: " + transitions.size());
		System.out.println("Places num: " + places.size());

		for (int i = iteracionesIni; i <= iteracionesFin; i++) {
			FileWriter fichero = new FileWriter(ficheroName, true);

			EscribeFichero.execute("\n--------Iteraci�n " + i + "  -------------\n", fichero);

			List<String> log = xesLog2(xesFilename, i, net, add, fichero);

			fichero.close();

			FileWriter fichero2 = new FileWriter(ficheroName, true);

			try {
				// SimplePetriNetSolverPlaces petriSolver=new
				// SimplePetriNetSolverPlaces(transitions,places,log);
				SimplePetriNetSolverPlacesGoal petriSolver = new SimplePetriNetSolverPlacesGoal(transitions, places,
						log);
				// SimplePetriNetSolverPlacesExecuted petriSolver=new
				// SimplePetriNetSolverPlacesExecuted(transitions,places,log);
				petriSolver.obtainFloydMatrix();
				petriSolver.createVariables();
				petriSolver.createConstrains();
				petriSolver.DifferenceAligmentAndMinimize();

				long time_start = System.currentTimeMillis();
				petriSolver.search(fichero2);
				long current_time_end = System.currentTimeMillis();
				System.out.println(" Tiempo de ejecuci�n iteraci�n: " + i + "-->"
						+ ((double) current_time_end - (double) time_start) / 1000);

			} catch (IloException error) {
				System.out.println("An error occurred : " + error);
			}
			fichero2.close();

		}
	}

	public static List<String> xesLog2(String xesPath, Integer index, PetriNet net, String add, FileWriter fichero)
			throws Exception {
		// String xesFilePath="/xes/2000-all-noises.xes";
		// String xesFileAbsolute =
		// "C:/Users/David/workspace/conformanceCheking/ConfCheck/src/main/resources/xes/2000-all-noise.xes";

		File xesFile = new File(SimplePetriNetEjemplosTest.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();
		//
		boolean a = xparser.canParse(xesFile);

		// System.out.println(a);

		List<XLog> xLogList = xparser.parse(xesFile);
		//
		XLog b = xLogList.get(0);
		// System.out.println(b.size());

		// CAMBIAR PARA VARIAR TRAZA
		XTrace oneTrace = b.get(index);

		// XEvent oneEvent=oneTrace.get(0);

		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		List<TransitionPetriNet> transitions = net.constructTransitions();
		List<PlacePetriNet> places = net.constructPlaces();

		//
		for (XEvent x : oneTrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			logs_xes.add(net.nameToId2(x.getAttributes().get("concept:name").toString(), transitions, add));
		}

		String resultLog = "Logs Sin traducir:" + logs_xesSinTraducir + "\n";
		resultLog = resultLog + "Logs traducido" + logs_xes + "\n";
		EscribeFichero.execute(resultLog, fichero);

		return logs_xes;
	}

}
