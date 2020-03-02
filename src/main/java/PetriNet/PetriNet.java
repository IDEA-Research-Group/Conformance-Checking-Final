package PetriNet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import es.idea.pnml.Pnml;
import es.idea.pnml.Pnml.Net;
import es.us.idea.pnml.App;
import javax.xml.bind.JAXBException;
public class PetriNet implements Serializable{
	
	protected List<Pnml.Net.Transition> transitions;
	protected List<Pnml.Net.Place> places;
	protected List<Pnml.Net.Arc> arcs;
	protected Pnml pnml;
	
	protected PetriNet() throws JAXBException{
		
		transitions=new ArrayList<>();
		places=new ArrayList<>();
		arcs=new ArrayList<>();

	}
	
	public PetriNet(String pnmlArchive) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(Pnml.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		pnml = (Pnml) unmarshaller.unmarshal(App.class
				.getResourceAsStream(pnmlArchive));
		//"/petripequena.pnml"
		Net net = pnml.getNet();

		
		transitions=net.getTransition();
		places=net.getPlace();
		arcs=net.getArc();

	}

	
	public String nameToId(String name, List<TransitionPetriNet> trans ){
		String res=name+"-NotInModel";

		for (TransitionPetriNet t : trans) {


			if(t.getName().equals(name)){
				res=t.getId();
			}
		}
		return res;
	}
	
	public String nameToId2(String name, List<TransitionPetriNet> trans,String add ){
		String res=name+add+"-NotInModel";
		for (TransitionPetriNet t : trans) {
			if(t.getName().equals(name+add)){
				res=t.getId();
			}
		}
		return res;
	}
	
	
	public List<Pnml.Net.Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Pnml.Net.Transition> transitions) {
		this.transitions = transitions;
	}

	public List<Pnml.Net.Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Pnml.Net.Place> places) {
		this.places = places;
	}

	public List<Pnml.Net.Arc> getArcs() {
		return arcs;
	}

	public void setArcs(List<Pnml.Net.Arc> arcs) {
		this.arcs = arcs;
	}
	

	public Pnml getPnml() {
		return pnml;
	}


	public void setPnml(Pnml pnml) {
		this.pnml = pnml;
	}
	
	
	
	
	public List<TransitionPetriNet> constructTransitions() throws JAXBException{
		List<TransitionPetriNet> result =new ArrayList<TransitionPetriNet>();
		
		for (Pnml.Net.Transition transition : transitions) {
			TransitionPetriNet t=new TransitionPetriNet(arcs, transition.getId(), transition.getName().getText());
			result.add(t);
		}

		return result;
	}
	
	public List<PlacePetriNet> constructPlaces() throws JAXBException{
		List<PlacePetriNet> result =new ArrayList<PlacePetriNet>();
		
		for (Pnml.Net.Place place : places) {
			PlacePetriNet t=new PlacePetriNet(arcs, place.getId());
			result.add(t);
		}

		return result;
	}
	
	
	public String toString(){
		String res = "Transiciones:\n";
		for (Pnml.Net.Transition t : getTransitions()) {
			res+=t.getId()+" --> "+t.getName().getText()+"\n";

		}
		res+="\nPlace:\n";
		for (Pnml.Net.Place t : getPlaces()) {
			res+=t.getId()+"\n";

		}
		
		res+="\nArcos:\n";
		for (Pnml.Net.Arc a : getArcs()) {
				res+=a.getId()+":  "+a.getSource()+" --> "+a.getTarget()+"\n";
		}
		res+="\n";
		
		return res;
	}
	

}
