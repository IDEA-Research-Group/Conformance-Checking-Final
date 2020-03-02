package PetriNet.LPO;

import java.io.Serializable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import PetriNet.PetriNet;
import es.idea.pnml.Pnml.Net.Arc;
import es.idea.pnml.Pnml.Net.Transition;
import es.idea.pnml.lpo.Pnml;
import es.idea.pnml.lpo.Pnml.Lpo;
import es.us.idea.pnml.App;


public class PetriNetLpo extends PetriNet implements Serializable{

	protected List<TransitionLPO> transitionsLpo;
	protected List<EventLpo> eventsLpo;

	protected List<Pnml.Lpo.Event> events;
	protected List<Pnml.Lpo.LpoArc> arcsLpo;
	protected Pnml.Lpo.Name name;
	// protected Pnml pnml;


	public PetriNetLpo(InputStream pnmlContent) throws JAXBException {
		super();

		JAXBContext jc = JAXBContext.newInstance(Pnml.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Pnml pnml = (Pnml)unmarshaller.unmarshal(pnmlContent);

		InitializeLPO(pnml);
	}

	public PetriNetLpo(String pnmlArchive) throws JAXBException {
		super();

		JAXBContext jc = JAXBContext.newInstance(Pnml.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Pnml pnml = (Pnml) unmarshaller.unmarshal(App.class.getResourceAsStream(pnmlArchive));

		InitializeLPO(pnml);
	}

	public void InitializeLPO(Pnml pnml) throws JAXBException{

		Lpo lpo = pnml.getLpo();

		events = lpo.getEvent();
		arcsLpo = lpo.getLpoArc();
		name = lpo.getName();

		transitionsLpo = new ArrayList<TransitionLPO>();
/*
		for (Pnml.Lpo.LpoArc arc : arcsLpo) {

			if (arc.getGraphics().isUserDrawn()) {
				TransitionLPO t = new TransitionLPO(arcsLpo, arc.getId());
				transitionsLpo.add(t);

				// Setting arc for PetriNet
				Arc a = new Arc();
				a.setId(arc.getId());
				a.setSource(arc.getSource());
				a.setTarget(arc.getTarget());
				arcs.add(a);

			}
		}
*/
		eventsLpo = new ArrayList<EventLpo>();

		for (Pnml.Lpo.Event event : events) {

			EventLpo t = new EventLpo(arcsLpo, event.getId(), event.getName().getValue());
			eventsLpo.add(t);

			// Setting transitions for PetriNet
			Transition tr = new Transition();
			tr.setId(event.getId());
			es.idea.pnml.Pnml.Net.Transition.Name nam = new es.idea.pnml.Pnml.Net.Transition.Name();
			nam.setText(event.getName().getValue());
			tr.setName(nam);
			transitions.add(tr);
		}
	}

	public List<Pnml.Lpo.Event> getEvents() {
		return events;
	}

	public void setEvents(List<Pnml.Lpo.Event> events) {
		this.events = events;
	}

	public List<Pnml.Lpo.LpoArc> getArcsLpo() {
		return arcsLpo;
	}

	public void setArcsLpo(List<Pnml.Lpo.LpoArc> arcs) {
		this.arcsLpo = arcs;
	}

	public Pnml.Lpo.Name getName() {
		return name;
	}

	public void setName(Pnml.Lpo.Name name) {
		this.name = name;
	}

	// public Pnml getPnml() {
	// return pnml;
	// }
	//
	// public void setPnml(Pnml pnml) {
	// this.pnml = pnml;
	// }

	@Override
	public String toString() {
		String res = "PetriNetLPO [" + "Name: ->" + name.getValue() + "\n" + "Events: \n";

		for (EventLpo e : eventsLpo) {
			res += "EventName: " + e.getName() + ", EventId: " + e.getId() + "\n";
		}
		res += "Arcs: \n";/*
		for (Pnml.Lpo.LpoArc a : arcsLpo) {
			if (a.getGraphics().isUserDrawn())
				res += "ArcName: " + a.getId() + " _ " + a.getSource() + " -> " + a.getTarget() + "\n";
		}*/

		return res;
	}

	public List<TransitionLPO> getTransitionsLPO() {

		return this.transitionsLpo;

	}

	public List<EventLpo> getEventLpo() {
		return eventsLpo;

	}

	public List<String> getTransitionsString() {
		return transitions.stream().map(t -> t.getName().getText()).collect(Collectors.toList());
	}

	public List<String> getListTransitionsIds() {
		return transitions.stream().map(t -> t.getId()).collect(Collectors.toList());
	}

	public List<Transition> findTransitionByName(String name) {
		return transitions.stream().filter(t -> t.getName().getText().equals(name)).collect(Collectors.toList());
	}

	public Transition findTransitionById(String id) {
		Transition result = null;
		for (Transition t : transitions) {
			if (t.getId().equals(id)) {
				result = t;
				break;
			}
		}

		return result;
		// return transitions.stream().filter(t ->
		// t.getId().equals(id)).collect(Collectors.toList()).get(0);
	}

	public EventLpo findEventsById(String id) {
		EventLpo result = null;
		for (EventLpo t : eventsLpo) {
			if (t.getId().equals(id)) {
				result = t;
				break;
			}
		}

		return result;

		// return
		// eventsLpo.stream().filter(t->t.getId().equals(id)).collect(Collectors.toList()).get(0);
	}

}
