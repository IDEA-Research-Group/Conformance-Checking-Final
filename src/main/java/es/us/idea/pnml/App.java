package es.us.idea.pnml;

import javax.xml.bind.JAXBException;

import PetriNet.PetriNet;
import es.idea.pnml.Pnml;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws JAXBException {

		String pnml = "/banktransfer_opennet.pnml";

		PetriNet net = new PetriNet(pnml);

		// SRP n184
		// ST n121
		System.out.println("hasCicle()_Test");
		String cicleResponse = "No cicle";
		for (Pnml.Net.Transition t : net.getTransitions()) {
			// if(t.getId().equals("n184")){
			if (t.getId().equals("n121")) {
				// cicleResponse = net.hasCicleForTransaction(t);
				System.out.println("Cicle in: " + cicleResponse);
				System.out.println("For: " + t.getName().getText() + "  id:" + t.getId());
			}
		}
		if (cicleResponse.equals("No cicle")) {
			System.out.println("    No cicle in outputs for this transaction");
		}
		System.out.println("End of hasCicle()_Test");

		// Pnml.Net.Transition trans=net.getTransitions().get(0);
		// net.getTokens().add("n6");
		// net.getTokens().add("n7");
		// net.getTokens().add("n8");

		//
		//// System.out.println("Trying activate transaction :
		// "+trans.getName().getText());
		////
		//// System.out.println(net.canActive(trans).getInputTokens());
		//// System.out.println(net.canActive(trans).getOutputTokens());
		//// System.out.println(net.canActive(trans).isCan());
		////
		//// System.out.println("Activating Next \n Tokens Before");
		////
		//// System.out.println(net.getTokens());
		//// Pnml.Net.Transition next=net.next();
		//// System.out.println("Transition activated:
		// "+next.getName().getText());
		//// System.out.println("tokens after");
		//// System.out.println(net.getTokens());
		////
		////

		// System.out.println("Transiciones:");
		// for (Transition t : net.getTransitions()) {
		// System.out.println(t.getId());
		// System.out.println(t.getName().getText());
		//
		// }
		// System.out.println("Place:");
		// for (Place t : net.getPlaces()) {
		// System.out.println(t.getId());
		//
		// }
		//
		// System.out.println("Arcos:");
		// for (Arc a : net.getArcs()) {
		// if(a.getTarget().equals("n122")){
		// System.out.println(a.getId()+" de: "+a.getSource()+" a:
		// "+a.getTarget());
		// }
		// }
		//
	}
}
