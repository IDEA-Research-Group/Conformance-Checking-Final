package PetriNet.LPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.idea.pnml.lpo.Pnml;

public class TransitionLPO implements Serializable{

	protected List<Pnml.Lpo.LpoArc> inputArcs;
	protected List<Pnml.Lpo.LpoArc> outputArcs;
	protected List<String> inputs;
	protected List<String> outputs;
	// protected Boolean is_star;
	// protected Boolean is_end;
	protected String id;
	// protected String name;

	public TransitionLPO(List<Pnml.Lpo.LpoArc> arcs, String id/*, String name*/) {
		super();

		this.id = id;
		// this.name = name;

		inputArcs = new ArrayList<Pnml.Lpo.LpoArc>();
		outputArcs = new ArrayList<Pnml.Lpo.LpoArc>();

		for (Pnml.Lpo.LpoArc arc : arcs) {
			// getSource()getTarget()
			if (arc.getSource().equals(id)) {
				outputArcs.add(arc);
				outputs.add(arc.getTarget());
			} else if (arc.getTarget().equals(id)) {
				inputArcs.add(arc);
				inputs.add(arc.getSource());
			}
		}
		//
		// this.is_star = inputs.isEmpty();
		// this.is_end = outputs.isEmpty();
	}

	public List<Pnml.Lpo.LpoArc> getInputArcs() {
		return inputArcs;
	}

	public void setInputArcs(List<Pnml.Lpo.LpoArc> inputArcs) {
		this.inputArcs = inputArcs;
	}

	public List<Pnml.Lpo.LpoArc> getOutputArcs() {
		return outputArcs;
	}

	public void setOutputArcs(List<Pnml.Lpo.LpoArc> outputArcs) {
		this.outputArcs = outputArcs;
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

	// public Boolean getIs_star() {
	// return is_star;
	// }
	//
	// public void setIs_star(Boolean is_star) {
	// this.is_star = is_star;
	// }
	//
	// public Boolean getIs_end() {
	// return is_end;
	// }
	//
	// public void setIs_end(Boolean is_end) {
	// this.is_end = is_end;
	// }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }

	@Override
	public String toString() {
		return "TransitionId: " + getId();
	}

}
