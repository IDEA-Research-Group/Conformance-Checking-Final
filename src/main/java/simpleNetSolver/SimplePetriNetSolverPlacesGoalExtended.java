package simpleNetSolver;

import java.util.List;

import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import ilog.concert.IloException;

public class SimplePetriNetSolverPlacesGoalExtended extends SimplePetriNetSolverPlacesGoal {

	public SimplePetriNetSolverPlacesGoalExtended(List<TransitionPetriNet> modelExt, List<PlacePetriNet> placesExt,
			List<String> logsExt) throws IloException {
		super(modelExt, placesExt, logsExt);
		
	}

	

}
