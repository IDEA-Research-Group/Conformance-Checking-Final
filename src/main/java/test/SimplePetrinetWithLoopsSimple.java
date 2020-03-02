package test;

import java.io.File;
import java.util.ArrayList;
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
import simpleNetSolver.SimplePetriNetSolverPlaces;
import simpleNetSolver.SimplePetriNetSolverPlacesWithLoops;

public class SimplePetrinetWithLoopsSimple {
	public static void main(String[] args) throws Exception {
	
		
		String pnml="/banktransfer_simple-LOOP.pnml";
		
		PetriNet net=new PetriNet(pnml);	
		List<TransitionPetriNet> transitions=net.constructTransitions();
		List<PlacePetriNet> places=net.constructPlaces();
		
		
		List<String> logs=new ArrayList<String>();
		logs.add("n122");//ST
		logs.add("n123");//STRR
		logs.add("n125");//RRR
		logs.add("n123");//STRR
		logs.add("n124");//RRS


		try{
			SimplePetriNetSolverPlacesWithLoops petriSolver=new SimplePetriNetSolverPlacesWithLoops(transitions,places,logs);

			petriSolver.createConstrains();
			petriSolver.DifferenceAligmentAndMinimize();
			petriSolver.search();

			
	    }
		catch (IloException error) {
	        System.out.println("An error occurred : "+error);
	    } 	
		
	}
}
