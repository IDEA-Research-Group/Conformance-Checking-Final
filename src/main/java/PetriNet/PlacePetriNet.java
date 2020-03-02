package PetriNet;

import java.util.ArrayList;
import java.util.List;

import es.idea.pnml.Pnml;
import javax.xml.bind.JAXBException;

public class PlacePetriNet implements Cloneable{
	protected List<Pnml.Net.Arc> inputArcs;
	protected List<Pnml.Net.Arc> outputArcs;
	protected List<String> inputs;
	protected List<String> outputs;
	protected String id;
	protected Boolean is_star;
	protected Boolean is_end;

	
	public Boolean getIs_end() {
		return is_end;
	}


	public void setIs_end(Boolean is_end) {
		this.is_end = is_end;
	}


	public PlacePetriNet(List<Pnml.Net.Arc> arcs, String placeId) throws JAXBException{
		this.id=placeId;
		inputArcs= new ArrayList<Pnml.Net.Arc>();
		outputArcs= new ArrayList<Pnml.Net.Arc>();
		inputs= new ArrayList<String>();
		outputs= new ArrayList<String>();

		for (Pnml.Net.Arc arc : arcs) {
			//getSource()getTarget()
			if(arc.getSource().equals(placeId)){
				outputArcs.add(arc);
				outputs.add(arc.getTarget());
			}
			else if(arc.getTarget().equals(placeId)){
				inputArcs.add(arc);
				inputs.add(arc.getSource());
			}
		}
		
		is_star=inputs.size()==0;
		is_end=outputs.size()==0;
		
	}

	
	public PlacePetriNet(PlacePetriNet p, String duplicated){
		inputArcs=p.getInputArcs();
		outputArcs=p.getOutputArcs();
		inputs=p.getInputs();
		outputs=p.getOutputs();
		is_star=p.getIs_star();
		is_end=p.getIs_end();
		id=duplicated;
	}
	
	public PlacePetriNet(String notInModel){
		this.id=notInModel;
		inputArcs= new ArrayList<Pnml.Net.Arc>();
		outputArcs= new ArrayList<Pnml.Net.Arc>();
		inputs= new ArrayList<String>();
		outputs= new ArrayList<String>();
		is_star=false;
		is_end=false;

	}
	
	
	public List<String> getInputs() {
		return inputs;
	}

	public void setInputs(List<String> inputs) {
		this.inputs = inputs;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	public List<Pnml.Net.Arc> getInputArcs() {
		return inputArcs;
	}

	public void setInputArcs(List<Pnml.Net.Arc> inputArcs) {
		this.inputArcs = inputArcs;
	}

	public List<Pnml.Net.Arc> getOutputArcs() {
		return outputArcs;
	}

	public void setOutputArcs(List<Pnml.Net.Arc> outputArcs) {
		this.outputArcs = outputArcs;
	}

	public Boolean getIs_star() {
		return is_star;
	}

	public void setIs_star(Boolean is_star) {
		this.is_star = is_star;
	}

	
	public String toString(){
		String res="For place: "+this.id;
		res+="\nInputs: "
				+ "[";
		for (Pnml.Net.Arc a : inputArcs) {
			res+=" "+a.getSource()+",";
		}
		res+="]\nOutputs:"
				+ "[";
		for (Pnml.Net.Arc a : outputArcs) {
			res+=" "+a.getTarget()+" ,";
		}
		res+="]\n";
		return res;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}




	

	
	
	
	
	
	
	

}
