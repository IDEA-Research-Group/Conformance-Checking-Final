package PetriNet.LPO;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.xml.bind.JAXBException;

import es.idea.pnml.lpo.Pnml;

public class EventLpo implements Serializable{

	protected List<Pnml.Lpo.LpoArc> inputArcs;
	protected List<Pnml.Lpo.LpoArc> outputArcs;
	protected List<String> inputs;
	protected List<String> outputs;
	protected String id;
	protected String name;
	protected Boolean is_star;
	protected Boolean is_end;

	public EventLpo(List<Pnml.Lpo.LpoArc> arcs, String eventId, String name) throws JAXBException {
		this.id = eventId;
		this.name = name;
		inputArcs = new ArrayList<Pnml.Lpo.LpoArc>();
		outputArcs = new ArrayList<Pnml.Lpo.LpoArc>();
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();

		for (Pnml.Lpo.LpoArc arc : arcs) {
			// getSource()getTarget()
			if (arc.getSource().equals(eventId)) {
				outputArcs.add(arc);
				outputs.add(arc.getTarget());
			} else if (arc.getTarget().equals(eventId)) {
				inputArcs.add(arc);
				inputs.add(arc.getSource());
			}
		}

		is_star = inputs.size() == 0;
		is_end = outputs.size() == 0;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIs_star() {
		return is_star;
	}

	public void setIs_star(Boolean is_star) {
		this.is_star = is_star;
	}

	public Boolean getIs_end() {
		return is_end;
	}

	public void setIs_end(Boolean is_end) {
		this.is_end = is_end;
	}

	@Override
	public String toString() {
		return "EventLpoId: " + id + "\n EventName: " + name;
	}
	
	

}
