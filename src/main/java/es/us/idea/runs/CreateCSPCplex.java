package es.us.idea.runs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import PetriNet.LPO.EventLpo;
import PetriNet.LPO.PetriNetLpo;
import es.idea.pnml.Pnml.Net.Arc;
import es.idea.pnml.Pnml.Net.Transition;
import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Goal;

public class CreateCSPCplex {

	// Model
	protected PetriNetLpo lpoFile;
	protected LinkedHashMap<String, Integer> repeatedActivityModel;
	protected List<String> listOfActivitiesModel;
	protected List<String> listOfActivitiesModelTranslated;
	protected List<Arc> arcs;

	// Log
	protected List<String> listOfActivitiesLog;
	protected LinkedHashMap<String, Integer> repeatedActivityLog;

	protected String[] finalArrayActivities;

	// CSP Solver
	protected IloCplex solver;

	// CSP Variables
	protected IloIntVar[] VarModel;
	protected IloIntVar[] VarLog;
	protected IloIntVar[] VarDiff;
	protected Map<String, IloIntVar[]> Xarray;
	protected IloIntVar VarAligment;

	// Derived attributes
	protected Integer[] VarModelPositions;
	protected HashMap<String, Integer> repeatedFinal;
	protected HashMap<String, Integer> varModelPostions;
	protected Integer tamFinal;

	// Last missalignment
	protected Double lastMissAlignment;

	public CreateCSPCplex(PetriNetLpo lpoFile, List<String> log, double n) throws IloException {

		lastMissAlignment = n;

		// Model y Log initializing
		listOfActivitiesLog = log;
		this.lpoFile = lpoFile;

		// Initializing variables
		solver = new IloCplex();
		repeatedActivityModel = new LinkedHashMap<>();
		listOfActivitiesModel = new ArrayList<>();
		listOfActivitiesModelTranslated = new ArrayList<>();
		arcs = new ArrayList<>();
		repeatedActivityLog = new LinkedHashMap<>();
		repeatedFinal = new LinkedHashMap<>();
		Xarray = new LinkedHashMap<>();
		varModelPostions = new LinkedHashMap<>();
		tamFinal = 0;

		// Population and processing data
		populateModelVariables(lpoFile);
		populateLogVariables(log);
		processCombination();

		// Creation of array of variables and restriction of domains
		createArrays();
		reduceDomains();

		// Creation of model constraints
		createModelConstraints();
		createLogConstraints();

		// Setup objective and constraints to avoid gaps
		setupObjectiveFunction();

	}

	public double runSearch() throws IloException {

		// Long start = 0L;
		//
		double rest = -1;

		Long duration = 0L;
		// for (int x = 0; x < ConfigParameters.NITERATIONS; x++) {
		// start = System.nanoTime();
		// solver.newSearch();
		// solver.endSearch();
		// duration += System.nanoTime() - start;
		// }

		// solver.setTraceAll(ConfigParameters.TRACE);

		// solver.setPropagationTimeLimit(6);

		// To print the CSP model
		if (ConfigParameters.PRINTMODEL)
			System.out.println(solver.getModel());

		if (ConfigParameters.SOL) {
			System.out.println("************* LPO: " + lpoFile.getName().getValue() + " *************");
		}

		solver.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Auto);
		// Disable the output of the solve function
		solver.setOut(null);

		Goal g = solver.constraintGoal(solver.ge(VarAligment, 0));

		if (solver.solve(g)) {

			for (int i = 0; i < VarModel.length && ConfigParameters.SOL; i++) {
				System.out.print(VarModel[i] + ":" + solver.getValue(VarModel[i]));
				System.out.println(" <--> " + (VarLog[i] + ":" + solver.getValue(VarLog[i])));
			}

			for (int i = 0; i < VarModel.length && ConfigParameters.SOL; i++) {
				System.out.println(VarDiff[i] + ":" + solver.getValue(VarDiff[i]));
			}

			if (Xarray.size() > 0 && ConfigParameters.SOL)
				System.out.println("---- Xarray ----");

			for (String xa : Xarray.keySet()) {
				for (int i = 0; i < Xarray.get(xa).length; i++) {
					if (Xarray.get(xa)[i]!=null) {
						if (ConfigParameters.SOL) {
							System.out
									.println((Xarray.get(xa)[i]).getName() + ";" + Xarray.get(xa)[i]);
						}
					} else {
						if (ConfigParameters.SOL) {
							System.out
									.println((Xarray.get(xa)[i]).getName() + ":" + solver.getValue(Xarray.get(xa)[i]));
						}
					}
				}
			}

			rest = solver.getValue(VarAligment);

			if (ConfigParameters.SOL) {
				System.out.println("================");
				System.out.println("Optimal: " + solver.getStatus());
				System.out.println("Sol: " + rest);
				System.out.println(
						"Average search time: " + TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS));
				System.out.println("================");
			}
		}

		// solver.endSearch();

		return rest;
	}

	private void setupObjectiveFunction() throws IloException {

		// Evitar que en instante t ocurra dos cosas diferentes entre VarModel y
		// VarLog

		for (int i = 0; i < VarModel.length; i++) {
			for (int j = 0; j < VarModel.length; j++) {
				if (i != j) {

					// solver.add(solver.imply(solver.neq(VarModel[i], 0),
					// solver.and(solver.neq(VarModel[i], VarModel[j]),
					// solver.neq(VarModel[i], VarLog[j]))));
					//
					// solver.add(solver.imply(solver.neq(VarLog[i], 0),
					// solver.neq(VarLog[i], VarLog[j])));

					solver.add(solver.or(solver.eq(VarModel[i], 0), solver.not(solver.eq(VarModel[i], VarModel[j]))));

					// solver.add(solver.ifThen(solver.not(solver.eq(VarModel[i],
					// 0)),
					// solver.not(solver.eq(VarModel[i], VarModel[j]))));

					// two different activities cannot be executed at the same
					// time
					// solver.add(solver.ifThen(solver.not(solver.eq(VarModel[i],
					// 0)),
					// solver.not(solver.eq(VarModel[i], VarLog[j]))));

					solver.add(solver.or(solver.eq(VarModel[i], 0), solver.not(solver.eq(VarModel[i], VarLog[j]))));

					// solver.add(solver.ifThen(solver.not(solver.eq(VarLog[i],
					// 0)),
					// solver.not(solver.eq(VarLog[i], VarLog[j]))));

					solver.add(solver.or(solver.eq(VarLog[i], 0), solver.not(solver.eq(VarLog[i], VarLog[j]))));
				}
			}

		}

		for (int i = 0; i < VarModel.length; i++) {
			solver.add(solver.ifThen(solver.eq(VarModel[i], VarLog[i]), solver.eq(VarDiff[i], 0)));
			solver.add(solver.ifThen(solver.and(solver.not(solver.eq(VarModel[i], VarLog[i])),
					solver.or(solver.eq(VarModel[i], 0), solver.eq(VarLog[i], 0))), solver.eq(VarDiff[i], 1)));
			solver.add(solver.ifThen(
					solver.and(solver.not(solver.eq(VarModel[i], VarLog[i])),
							solver.and(solver.not(solver.eq(VarModel[i], 0)), solver.not(solver.eq(VarLog[i], 0)))),
					solver.eq(VarDiff[i], 2)));
		}

		solver.add(solver.eq(solver.sum(VarDiff), VarAligment));

		for (int i = 0; i < VarModel.length - 1; i++) {
			for (int j = i + 1; j < VarModel.length; j++) {
				solver.add(solver.or(solver.not(solver.eq(VarModel[i], VarModel[j])),
						solver.or(solver.eq(VarModel[i], 0), solver.eq(VarModel[j], 0))));
				solver.add(solver.or(solver.not(solver.eq(VarLog[i], VarLog[j])),
						solver.or(solver.eq(VarLog[i], 0), solver.eq(VarLog[j], 0))));
			}
		}

		int tam = listOfActivitiesLog.size() + listOfActivitiesModel.size() + 1;

		IloConstraint[] valuesAsigned = new IloConstraint[tam];
		IloIntVar[] reifVarModel = new IloIntVar[tam];

		// Evitar huecos
		for (int i = 0; i < tam; i++) {
			reifVarModel[i] = solver.intVar(0, 1, i + "reifModel");
		}

		for (int i = 1; i < tam; i++) {
			valuesAsigned[i] = solver.or(solver.eq(VarModel[0], i), solver.eq(VarLog[0], i));
			for (int j = 1; j < VarModel.length; j++) {
				valuesAsigned[i] = solver.or(valuesAsigned[i],
						solver.or(solver.eq(VarModel[j], i), solver.eq(VarLog[j], i)));
			}
			solver.add(solver.eq(valuesAsigned[i], reifVarModel[i]));
		}
		for (int i = 2; i < tam; i++) {
			solver.add(solver.or(solver.not(solver.eq(1, reifVarModel[i])), solver.eq(1, reifVarModel[i - 1])));
		}

		// minimize the difference between the log and the model
		solver.add(solver.minimize(VarAligment));

	}

	// Version 2: new version to cover new cases with multiple repeated
	// activities
	private void createLogConstraints() throws IloException {
		List<String> repList = new ArrayList<>();

		HashMap<String, Integer> pointers = new HashMap<>();
		HashMap<String, List<Integer>> mapOrXarraVarLog = new HashMap<>();

		for (String x : Xarray.keySet()) {
			pointers.put(x, 0);
			mapOrXarraVarLog.put(x, new ArrayList<>());
		}

		for (int i = 0; i < listOfActivitiesLog.size() - 1; i++) {

			List<Integer> listIndiceFirst = new ArrayList<>();
			List<Integer> listIndiceSecond = new ArrayList<>();

			int firstindice = -1;
			int secondindice = -1;

			String first = listOfActivitiesLog.get(i);
			String second = listOfActivitiesLog.get(i + 1);

			int valorFirst = repeatedFinal.get(listOfActivitiesLog.get(i));
			int valorSecond = repeatedFinal.get(listOfActivitiesLog.get(i + 1));

			for (int j = 0; j < finalArrayActivities.length; j++) {
				if (finalArrayActivities[j].equals(first) || first.equals(finalArrayActivities[j] + "-NotInModel")) {
					listIndiceFirst.add(j);
				}
			}

			if (listIndiceFirst.size() == 1) {
				firstindice = listIndiceFirst.get(0);
			} else {

				int cont = 0;
				for (int h = 0; h < repList.size(); h++) {
					if (repList.get(h).equals(first) || first.equals(repList.get(h))) {
						cont++;
					}
				}

				firstindice = listIndiceFirst.get(cont);
			}

			repList.add(first);

			for (int j = 0; j < finalArrayActivities.length; j++) {
				if (finalArrayActivities[j].equals(second) || second.equals(finalArrayActivities[j] + "-NotInModel")) {
					listIndiceSecond.add(j);
				}
			}

			if (listIndiceSecond.size() == 1) {
				secondindice = listIndiceSecond.get(0);
			} else {

				int cont = 0;
				for (int h = 0; h < repList.size(); h++) {
					if (repList.get(h).equals(second) || second.equals(repList.get(h))) {
						cont++;
					}
				}

				secondindice = listIndiceSecond.get(cont);

			}

			if (i == 0) {
				solver.add(solver.le(1, VarLog[firstindice]));
				if (valorFirst != 1) {

					// OR treatment VarLog - Xarray
					IloConstraint preOr = solver.eq(VarLog[firstindice], Xarray.get(first)[0]);
					for (int x = 1; x < Xarray.get(first).length; x++) {
						preOr = solver.or(preOr, solver.eq(VarLog[firstindice], Xarray.get(first)[x]));
					}

					solver.add(preOr);

				}
			}

			solver.add(solver.and(solver.le(VarLog[firstindice], VarLog[secondindice]),
					solver.not(solver.eq(VarLog[firstindice], VarLog[secondindice]))));

			if (valorFirst == 1) {
				if (valorSecond != 1) {

					// OR treatment VarLog - Xarray
					IloConstraint preOr = solver.eq(VarLog[secondindice], Xarray.get(second)[0]);
					for (int x = 1; x < Xarray.get(second).length; x++) {
						preOr = solver.or(preOr, solver.eq(VarLog[secondindice], Xarray.get(second)[x]));
					}

					solver.add(preOr);

				}

			} else {

				if (valorSecond == 1) {

					// OR treatment VarLog - Xarray
					IloConstraint preOr = solver.eq(VarLog[firstindice], Xarray.get(first)[0]);
					for (int x = 1; x < Xarray.get(first).length; x++) {
						preOr = solver.or(preOr, solver.eq(VarLog[firstindice], Xarray.get(first)[x]));
					}

					solver.add(preOr);

				} else {
					// OR treatment VarLog - Xarray - VarLog
					IloConstraint preOr = solver.eq(VarLog[secondindice], Xarray.get(second)[0]);
					for (int x = 1; x < Xarray.get(second).length; x++) {
						preOr = solver.or(preOr, solver.eq(VarLog[secondindice], Xarray.get(second)[x]));
					}

					solver.add(preOr);

				}
			}

		}

		// OR treatment Xarray - VarLog

		for (String k : mapOrXarraVarLog.keySet()) {

			if (repeatedActivityLog.containsKey(k + "-NotInModel")) {
				for (int i = 0; i < Xarray.get(k).length; i++) {

					IloConstraint preOr = solver.eq(Xarray.get(k)[i], VarLog[mapOrXarraVarLog.get(k).get(0)]);

					for (int j = 1; j < mapOrXarraVarLog.get(k).size(); j++) {
						preOr = solver.or(preOr, solver.eq(Xarray.get(k)[i], VarLog[mapOrXarraVarLog.get(k).get(j)]));
					}

					solver.add(preOr);
				}
			} else {
				for (int i = 0; i < Xarray.get(k).length; i++) {
					solver.eq(Xarray.get(k)[i], 0);
				}

			}
		}
	}

	private void createModelConstraints() throws IloException {

		for (Arc arc : arcs) {

			EventLpo source = lpoFile.findEventsById(arc.getSource());
			EventLpo target = lpoFile.findEventsById(arc.getTarget());

			if (source.getIs_star()) {
				solver.add(solver.ge(VarModel[varModelPostions.get(source.getId())], 1));

			}

			int firstindice = varModelPostions.get(source.getId());
			int secondindice = varModelPostions.get(target.getId());

			solver.add(solver.and(solver.le(VarModel[firstindice], VarModel[secondindice]),
					solver.not(solver.eq(VarModel[firstindice], VarModel[secondindice]))));

		}

		// Controlar las no apariciones Log-Modelo
		List<String> repList = new LinkedList<>();

		for (int i = 0; i < finalArrayActivities.length; i++) {
			if (!listOfActivitiesModelTranslated.contains(finalArrayActivities[i])
					&& listOfActivitiesLog.contains(finalArrayActivities[i] + "-NotInModel")) {
				// Aparece en el Log pero no en el Modelo
				solver.add(solver.eq(VarModel[i], 0));
			} else if (listOfActivitiesModelTranslated.contains(finalArrayActivities[i])
					&& !listOfActivitiesLog.contains(finalArrayActivities[i])) {
				// Aparece en el Modelo pero no en el Log
				solver.add(solver.eq(VarLog[i], 0));
			} else {

				// Aparece en Log y Modelo pero la repeticion de las actividades
				// no es lo mismo
				if (repeatedActivityLog.get(finalArrayActivities[i]) > repeatedActivityModel
						.get(finalArrayActivities[i]) && !repList.contains(finalArrayActivities[i])) {

					int j = i + repeatedActivityModel.get(finalArrayActivities[i]);
					int cont = repeatedActivityLog.get(finalArrayActivities[i])
							- repeatedActivityModel.get(finalArrayActivities[i]);
					while (cont > 0 && j < finalArrayActivities.length) {
						solver.add(solver.eq(VarModel[j], 0));
						j++;
						cont--;
					}

				} else if (repeatedActivityLog.get(finalArrayActivities[i]) < repeatedActivityModel
						.get(finalArrayActivities[i]) && !repList.contains(finalArrayActivities[i])) {

					int j = i + repeatedActivityLog.get(finalArrayActivities[i]);
					int cont = repeatedActivityModel.get(finalArrayActivities[i])
							- repeatedActivityLog.get(finalArrayActivities[i]);
					while (cont > 0 && j < finalArrayActivities.length) {
						solver.add(solver.eq(VarLog[j], 0));
						j++;
						cont--;
					}

				}

				repList.add(finalArrayActivities[i]);
			}
		}

	}

	private void populateLogVariables(List<String> log) {
		for (String s : log) {
			if (repeatedActivityLog.keySet().contains(s)) {
				repeatedActivityLog.put(s, repeatedActivityLog.get(s) + 1);
			} else {
				repeatedActivityLog.put(s, 1);
			}

		}

	}

	private void populateModelVariables(PetriNetLpo lpoFile) {
		arcs = lpoFile.getArcs();
		for (Transition t : lpoFile.getTransitions()) {
			if (repeatedActivityModel.keySet().contains(t.getName().getText())) {
				repeatedActivityModel.put(t.getName().getText(), repeatedActivityModel.get(t.getName().getText()) + 1);
			} else {
				repeatedActivityModel.put(t.getName().getText(), 1);

			}
			listOfActivitiesModelTranslated.add(t.getName().getText());
			listOfActivitiesModel.add(t.getId());
		}

	}

	private void processCombination() {
		Set<String> activitiesModel = repeatedActivityModel.keySet();
		Set<String> activitiesLog = repeatedActivityLog.keySet();

		for (String s : activitiesLog) {
			if (activitiesModel.contains(s)) {
				if (repeatedActivityLog.get(s) <= repeatedActivityModel.get(s)) {
					repeatedFinal.put(s, repeatedActivityModel.get(s));

				} else {
					repeatedFinal.put(s, repeatedActivityLog.get(s));
				}
			} else {
				repeatedFinal.put(s, repeatedActivityLog.get(s));
			}

		}

		for (String s : activitiesModel) {
			if (activitiesLog.contains(s)) {
				if (repeatedActivityLog.get(s) <= repeatedActivityModel.get(s)) {
					repeatedFinal.put(s, repeatedActivityModel.get(s));
				} else {
					repeatedFinal.put(s, repeatedActivityLog.get(s));
				}

			} else {
				repeatedFinal.put(s, repeatedActivityModel.get(s));
			}

		}

	}

	private void createArrays() throws IloException {
		tamFinal = repeatedFinal.values().stream().reduce((i1, i2) -> i1 + i2).get();

		finalArrayActivities = new String[tamFinal];

		VarModel = new IloIntVar[tamFinal];
		VarModelPositions = new Integer[tamFinal];
		VarLog = new IloIntVar[tamFinal];
		VarDiff = new IloIntVar[tamFinal];

		// Considering previous missalignment
		if (lastMissAlignment != -1) {
			VarAligment = solver.intVar(0, lastMissAlignment.intValue(), "SumTotal");
		} else {
			VarAligment = solver.intVar(0, tamFinal * 2, "SumTotal");
		}

		for (String x : repeatedFinal.keySet()) {

			if (repeatedFinal.get(x) > 1) {
				Xarray.put(x, new IloIntVar[repeatedFinal.get(x)]);
			}

		}

	}

	private void reduceDomains() throws IloException {
		List<String> union = new ArrayList<>();

		for (String s : repeatedFinal.keySet()) {
			for (int i = 0; i < repeatedFinal.get(s); i++) {
				if (listOfActivitiesLog.contains(s)) {
					if (s.endsWith("-NotInModel")) {
						union.add(s.substring(0, s.lastIndexOf("-")));
					} else {
						union.add(s);
					}

				} else {
					union.add(s);
				}

			}
		}

		for (int i = 0; i < tamFinal; i++) {

			finalArrayActivities[i] = union.get(i);

			VarModel[i] = solver.intVar(0, listOfActivitiesLog.size() + listOfActivitiesModel.size(),
					"Variable " + union.get(i) + "_" + i + ": " + "VarModel");

			VarLog[i] = solver.intVar(0, listOfActivitiesLog.size() + listOfActivitiesModel.size(),
					"Variable " + union.get(i) + "_" + i + ": " + "VarLog");

			VarDiff[i] = solver.intVar(0, 2, "Variable " + union.get(i) + "_" + i + ": " + "VarDiff");

			VarModelPositions[i] = -1;
		}

		for (String key : Xarray.keySet()) {
			IloIntVar[] value = Xarray.get(key);
			for (int i = 0; i < value.length; i++) {

				if (repeatedActivityModel.containsKey(key) && !repeatedActivityLog.containsKey(key)) {
					value[i] = solver.intVar(0, 0, "Variable " + key + "_" + i + ": " + "Xarray");
				} else {
					value[i] = solver.intVar(0, listOfActivitiesLog.size() + listOfActivitiesModel.size(),
							"Variable " + key + "_" + i + ": " + "Xarray");
				}

			}

			Xarray.put(key, value);

		}

		for (Arc a : arcs) {

			List<Integer> auxList = new LinkedList<>();
			String source = lpoFile.findEventsById(a.getSource()).getName();
			String target = lpoFile.findEventsById(a.getTarget()).getName();

			for (int j = 0; j < finalArrayActivities.length; j++) {
				if (finalArrayActivities[j].equals(source)) {
					auxList.add(j);
				}
			}

			for (Integer i : auxList) {
				if (VarModelPositions[i] == -1
						&& !varModelPostions.containsKey(lpoFile.findEventsById(a.getSource()).getId())) {
					varModelPostions.put(lpoFile.findEventsById(a.getSource()).getId(), i);
					VarModelPositions[i] = i;
					break;
				}
			}

			auxList.clear();

			for (int j = 0; j < finalArrayActivities.length; j++) {
				if (finalArrayActivities[j].equals(target)) {
					auxList.add(j);
				}
			}

			for (Integer i : auxList) {
				if (VarModelPositions[i] == -1
						&& !varModelPostions.containsKey(lpoFile.findEventsById(a.getTarget()).getId())) {
					varModelPostions.put(lpoFile.findEventsById(a.getTarget()).getId(), i);
					VarModelPositions[i] = i;
					break;
				}
			}

		}
	}

	// Util methods

	@SuppressWarnings("unused")
	private boolean insert(int i, String x, HashMap<String, List<Integer>> mapOrXarraVarLog) {
		boolean res = false;
		if (!mapOrXarraVarLog.get(x).contains(i)) {
			ArrayList<Integer> aux = new ArrayList<>(mapOrXarraVarLog.get(x));
			aux.add(new Integer(i));
			mapOrXarraVarLog.put(x, aux);
			res = true;
		}

		return res;
	}

	@SuppressWarnings("unused")
	private List<String> getFinalListActivities() {
		List<String> l = new ArrayList<>();
		for (int i = 0; i < finalArrayActivities.length; i++) {
			l.add(finalArrayActivities[i]);
		}

		return l;
	}

	public LinkedHashMap<String, Integer> getRepeatedActivityLog() {
		return repeatedActivityLog;
	}

	public void setRepeatedActivityLog(LinkedHashMap<String, Integer> repeatedActivityLog) {
		this.repeatedActivityLog = repeatedActivityLog;
	}

}
