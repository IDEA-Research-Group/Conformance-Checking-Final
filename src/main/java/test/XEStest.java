package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XUniversalParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XElement;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.xstream.XLogConverter;

import PetriNet.PetriNet;
import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import ilog.concert.IloException;
import simpleNetSolver.SimplePetriNetSolverPlaces;

public class XEStest {
	public static void main(String[] args) throws Exception {



//		String pnml=
//		PetriNet net=new PetriNet(pnml);		
//		List<TransitionPetriNet> transitions=net.constructTransitions();
//		List<PlacePetriNet> places=net.constructPlaces();
		
		
	//XES
		String xesFilePath="/xes/2000-all-noises.xes";
		String xesFileAbsolute = "C:/Users/David/workspace/conformanceCheking/ConfCheck/src/main/resources/xes/2000-all-noise.xes";
		File xesFile= new File(xesFileAbsolute);
		XesXmlParser xparser=new XesXmlParser();
//		
		boolean a=xparser.canParse(xesFile);
		System.out.println(a);

		List<XLog> xLogList=xparser.parse(xesFile);
//		
		XLog b= xLogList.get(0);
		System.out.println(b.size());
		
		XTrace oneTrace=b.get(0);
		oneTrace.getAttributes();
		
		
		XEvent oneEvent=oneTrace.get(1);

		
			
		List<String> logs_xes=new ArrayList<>();	
			
		for(XEvent x: oneTrace){
			logs_xes.add(x.getAttributes().get("concept:name").toString());
		}
	
		System.out.println(logs_xes);
		
		
		

//		try{
//			SimplePetriNetSolverPlaces petriSolver=new SimplePetriNetSolverPlaces(transitions,places,logs);
//			petriSolver.createConstrains();
//			petriSolver.DifferenceAligmentAndMinimize();
//			petriSolver.search();
//
//			
//	    }
//		catch (IloException error) {
//	        System.out.println("An error occurred : "+error);
//	    } 	
		
	}
}
