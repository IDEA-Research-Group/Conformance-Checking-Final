package PetriNet;

import java.util.ArrayList;
import java.util.List;

import es.idea.pnml.Pnml;
import es.idea.pnml.Pnml.Net.Arc;

import javax.xml.bind.JAXBException;

public class TransitionPetriNet implements Cloneable {
	protected List<Pnml.Net.Arc> inputArcs;
	protected List<Pnml.Net.Arc> outputArcs;
	protected List<String> inputs;
	protected List<String> outputs;
	protected Boolean is_star;
	protected String id;
	protected String name;

	public TransitionPetriNet(List<Pnml.Net.Arc> arcs, String transitionId, String transitionName)
			throws JAXBException {
		this.id = transitionId;
		this.name = transitionName;
		inputArcs = new ArrayList<Pnml.Net.Arc>();
		outputArcs = new ArrayList<Pnml.Net.Arc>();
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();

		for (Pnml.Net.Arc arc : arcs) {
			// getSource()getTarget()
			if (arc.getSource().equals(transitionId)) {
				outputArcs.add(arc);
				outputs.add(arc.getTarget());
			} else if (arc.getTarget().equals(transitionId)) {
				inputArcs.add(arc);
				inputs.add(arc.getSource());
			}
		}

		is_star = inputs.size() == 0;

	}

	public TransitionPetriNet(List<Arc> inputArcs, List<Arc> outputArcs, List<String> inputs, List<String> outputs,
			Boolean is_star, String id, String name) {
		super();
		this.inputArcs = inputArcs;
		this.outputArcs = outputArcs;
		this.inputs = inputs;
		this.outputs = outputs;
		this.is_star = is_star;
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransitionPetriNet(TransitionPetriNet t, String duplicated, String prime) {
		inputArcs = t.getInputArcs();
		outputArcs = t.getOutputArcs();
		inputs = t.getInputs();
		outputs = t.getOutputs();
		is_star = t.getIs_star();
		id = duplicated;
		this.name = t.getName() + prime;

	}

	public TransitionPetriNet(String notInModel) {
		this.id = notInModel;
		inputArcs = new ArrayList<Pnml.Net.Arc>();
		outputArcs = new ArrayList<Pnml.Net.Arc>();
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();
		is_star = false;
		this.name = notInModel + "(NotInMdl)";
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

	public String toString() {
		String res = "\n\nFor transition: " + this.id;
		res += "\nInputs:" + "[";
		for (Pnml.Net.Arc a : inputArcs) {
			res += " " + a.getSource() + ",";
		}
		res += "]\nOutputs:" + "[";
		for (Pnml.Net.Arc a : outputArcs) {
			res += " " + a.getTarget() + ",";
		}
		res += "]\n";
		return res;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
