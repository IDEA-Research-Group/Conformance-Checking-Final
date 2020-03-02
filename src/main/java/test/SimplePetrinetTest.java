package test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import PetriNet.PetriNet;
import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import ilog.concert.IloException;
import simpleNetSolver.SimplePetriNetSolverPlaces;

public class SimplePetrinetTest {
	public static void main(String[] args) throws JAXBException {

		
///PETRI NET MINI

		
//		String pnml="/banktransfer_mini.pnml";
//		
//		PetriNet net=new PetriNet(pnml);		
//		List<TransitionPetriNet> transitions=net.constructTransitions();
//		List<PlacePetriNet> places=net.constructPlaces();
//
//		
//		
////MINI
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");//st
//		logs.add("n123");//STRR
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");//st
//		logs.add("n122"); // st'
//		logs.add("n122");//ST''
//		logs.add("n123");//STRR

//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");//ST
//		logs.add("n12");//STRR
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");//st
//		logs.add("NE");//NO EXISTE	

		
		

	
		

		
		
		
/////////PETRI NET SIMPLE 		
		
		
		
//		String pnml="/banktransfer_simple.pnml";
//		PetriNet net=new PetriNet(pnml);		
//		List<TransitionPetriNet> transitions=net.constructTransitions();
//		List<PlacePetriNet> places=net.constructPlaces();
		

//SIMPLE
//		//correct trace
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");
//		logs.add("n123");
//		logs.add("n124");
//		logs.add("n125");
//		logs.add("n126");
//		logs.add("n127");
		
		//one doesn't exist (one more)
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");
//		logs.add("n123");
//		logs.add("n124");
//		logs.add("n125");
//		logs.add("n126");
//		logs.add("n127");
//		logs.add("n128");
		
//		//one doesn't exist
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");
//		logs.add("n123");
//		logs.add("n124");
//		logs.add("n125");
//		logs.add("n126");
//		logs.add("n128");
//		
//		//one less
//		List<String> logs=new ArrayList<String>();
//		logs.add("n122");
//		logs.add("n123");
//		logs.add("n124");
//		logs.add("n125");
//		logs.add("n126");
	
	
////INOUT
//		String pnml="/simpleMultiInOut.pnml";
//		PetriNet net=new PetriNet(pnml);		
//		List<TransitionPetriNet> transitions=net.constructTransitions();
//		List<PlacePetriNet> places=net.constructPlaces();

//		List<String> logs=new ArrayList<String>();
//		logs.add("x");
//		logs.add("a");
//		logs.add("c");
		

//		List<String> logs=new ArrayList<String>();
//		logs.add("x");
//		logs.add("NE");
//
//		List<String> logs=new ArrayList<String>();
//		logs.add("x");
//		logs.add("a");
//		logs.add("a");
//		logs.add("c");
		
		
/*Grande*/
		
		String pnml="/Grande.pnml";
		PetriNet net=new PetriNet(pnml);		
		List<TransitionPetriNet> transitions=net.constructTransitions();
		List<PlacePetriNet> places=net.constructPlaces();
		
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("A");
//		logs.add("B");
//		logs.add("E");
//		logs.add("D");
//		logs.add("G");
//		logs.add("I");
//		logs.add("F");
//		logs.add("H");
//		logs.add("J");
		
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("A");
//		logs.add("B");
//		logs.add("E");
//		logs.add("G");
//		logs.add("I");
//		logs.add("J");

//		List<String> logs=new ArrayList<String>();
//		logs.add("A");
//		logs.add("B");
//		logs.add("B");
//		logs.add("E");
//		logs.add("C");
//		logs.add("G");
//		logs.add("I");
//		logs.add("I");
//		logs.add("F");
//		logs.add("H");
//		logs.add("J");

		
		List<String> logs=new ArrayList<String>();
		logs.add("A");
		logs.add("B");
		logs.add("E");
		logs.add("C");
		logs.add("G");
		logs.add("I");
		logs.add("B");
		logs.add("I");
		logs.add("F");
		logs.add("H");
		logs.add("J");
		
		
//		List<String> logs=new ArrayList<String>();
//		logs.add("A");
//		logs.add("B");
//		logs.add("E");
//		logs.add("D");
//		logs.add("C");
//		logs.add("G");
//		logs.add("I");
//		logs.add("F");
//		logs.add("H");
//		logs.add("J");		
		

		try{
			SimplePetriNetSolverPlaces petriSolver=new SimplePetriNetSolverPlaces(transitions,places,logs);
			petriSolver.createConstrains();
			petriSolver.DifferenceAligmentAndMinimize();
			//TODO: XXX Esto estaba descomentado y fallaba: 
			// petriSolver.search();

			
	    }
		catch (IloException error) {
	        System.out.println("An error occurred : "+error);
	    } 	
		
	}
}
