package es.us.idea.runs;

import PetriNet.LPO.EventLpo;
import PetriNet.LPO.PetriNetLpo;
import com.google.common.collect.Collections2;
import es.idea.pnml.Pnml;
import es.idea.pnml.Pnml.Net.Arc;
import es.idea.pnml.Pnml.Net.Transition;
import es.idea.xes.XesUtils;
import es.us.idea.runs.constraints.BelongsToConstraint;
import es.us.idea.runs.constraints.EqualSumsConstraint;
import org.apache.commons.lang3.ArrayUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.lns.INeighborFactory;
import org.chocosolver.solver.search.loop.lns.neighbors.INeighbor;
import org.chocosolver.solver.search.loop.monitors.IMonitorContradiction;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.decision.IntDecision;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.Solver;
import org.chocosolver.util.PoolManager;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.deckfour.xes.model.XTrace;
import scala.Tuple2;

import java.time.LocalDateTime;
import java.util.*;

import static org.chocosolver.solver.search.strategy.Search.intVarSearch;

public class CreateCSPChoco {

	// Model
	private PetriNetLpo lpoFile;
	private String lpoName;
	protected LinkedHashMap<String, Integer> repeatedActivityModel;
	private List<String> listOfActivitiesModel;
	private List<String> listOfActivitiesModelTranslated;
	protected List<Arc> arcs;

	// Log
    private XTrace trace;
	private List<String> listOfActivitiesLog;
	protected LinkedHashMap<String, Integer> repeatedActivityLog;

	private String[] finalArrayActivities;

	// CSP Solver
	protected Model model;

	// CSP Variables
	protected IntVar[] VarModel;
	protected IntVar[] VarLog;
	private IntVar[] VarDiff;
	private IntVar[] Xarray;
	private IntVar VarAligment;

	// Derived attributes
	private Integer[] VarModelPositions;
	protected HashMap<String, Integer> repeatedFinal;
	private HashMap<String, Integer> varModelPostions;
	private Integer tamFinal;
	private int[] floydMinimumsModel;
	private int[] floydMaximumsModel;
    private HashMap<Integer,Integer> logToVarLogPositions;

	// Last missalignment
	private Integer lastMissAlignment;

	protected Integer maxExecutionTime;

	//If initialized, the problem both was initialized and the domains are correct
	protected boolean initialized = false;
    private int minAlignment;

	CreateCSPChoco(RunProblem.LPOContainer lpo, XTrace trace, int minAlignment, int lastMissAlignment, int maxExecutionTime){

		// Model y Log initializing
        this.trace = trace;
        this.lpoFile = lpo.lpo;
		listOfActivitiesLog = XesUtils.getLog(trace, lpoFile);
		lpoName = lpo.lpoFileName;

		// Initializing variables
		model = new Model();
		repeatedActivityModel = new LinkedHashMap<>();
		listOfActivitiesModel = new ArrayList<>();
		listOfActivitiesModelTranslated = new ArrayList<>();
		arcs = new ArrayList<>();
		repeatedActivityLog = new LinkedHashMap<>();
		repeatedFinal = new LinkedHashMap<>();
		varModelPostions = new LinkedHashMap<>();
		tamFinal = 0;
        logToVarLogPositions = new HashMap<>();
		// Population and processing data
		populateModelVariables(lpoFile);
		populateLogVariables();
		processCombination();

        tamFinal = repeatedFinal.values().stream().reduce((i1, i2) -> i1 + i2).get();

        this.minAlignment = minAlignment;
		this.lastMissAlignment = lastMissAlignment;
		if(!(minAlignment > tamFinal * 2 || minAlignment > lastMissAlignment && lastMissAlignment >= 0)){
			// Creation of array of variables and restriction of domains
			createArrays();
			initializeIntermediateVariables();
			reduceDomains();

			// Creation of model constraints
			createModelConstraints();
			createLogConstraints();

			// Setup objective and constraints to avoid gaps
			setupObjectiveFunction();

			initialized = true;
			this.maxExecutionTime = maxExecutionTime;

			if(detectHighRepetitionNumber(lpoFile, listOfActivitiesLog)){
				this.maxExecutionTime = Math.round(this.maxExecutionTime * ConfigParameters.NUMBER_REPETITIONS_MULTIPLIER);
			}
		}
	}


	public int runSearch(){
	    if(!initialized){
	        return -1;
        }
		Solver solver = model.getSolver();

		if(maxExecutionTime > 0){
		    solver.limitTime(maxExecutionTime);
        }

        if(ConfigParameters.SOL){
            solver.plugMonitor(new IMonitorSolution() {
                @Override
                public void onSolution() {
                    //TODO imprimir aqui la lpo y la traza
                    System.out.println(LocalDateTime.now().toLocalTime().toString() + " Solution found with " + VarAligment + " on " +
                            lpoName + " - " + trace.getAttributes().get("concept:name").toString());
                }
            });
        }

        /*solver.plugMonitor((IMonitorContradiction) (e) -> {
            System.out.println(e);
        });*/

		/*solver.plugMonitor(new IMonitorUpBranch() {
			@Override
			public void beforeUpBranch() {
				System.out.println("");
			}

			@Override
			public void afterUpBranch() {
				System.out.println("");
			}
		});

		solver.plugMonitor(new IMonitorRestart() {
			@Override
			public void beforeRestart() {
				System.out.println("");
			}

			@Override
			public void afterRestart() {
				System.out.println("");
			}
		});*/


		List<IntVar> varList = new ArrayList<>();
		varList.addAll(Arrays.asList(VarModel));
		varList.addAll(Arrays.asList(VarLog));
		varList.addAll(Arrays.asList(Xarray));

		/*INeighbor ni = INeighborFactory.reversedPropagationGuided(varList.toArray(new IntVar[VarModel.length*3]));
		solver.setLNS(ni);*/

        List<IntVar> listVars = new ArrayList<>();

        List<IntVar> allVars = new ArrayList<>();
        allVars.addAll(Arrays.asList(VarModel));
        allVars.addAll(Arrays.asList(VarLog));
        allVars.addAll(Arrays.asList(Xarray));

        //xarray y varmodel
        for (int i = 0; i < listOfActivitiesLog.size(); i++){
            int varPosition = logToVarLogPositions.get(i);
            listVars.add(Xarray[varPosition]);
            listVars.add(VarModel[varPosition]);
            allVars.remove(Xarray[varPosition]);
            allVars.remove(VarModel[varPosition]);
        }

        //varlog va despues
        for (int i = 0; i < listOfActivitiesLog.size(); i++){
            int varPosition = logToVarLogPositions.get(i);
            listVars.add(VarLog[varPosition]);
            allVars.remove(VarLog[varPosition]);
        }

        //los que todavia no se hayan añadido (no estan en varlog pero se añaden por completitud)
        listVars.addAll(allVars);

        solver.setSearch(new AbstractStrategy<IntVar>(listVars.toArray(new IntVar[listVars.size()])) {
            PoolManager<IntDecision> pool = new PoolManager<>();

            @Override
            public Decision<IntVar> getDecision() {
                IntDecision d = pool.getE();
                if(d==null) d = new IntDecision(pool);

                IntVar next = null;
                int i;
                for(i = 0; i < vars.length; i++){
                    IntVar v = vars[i];
                    if(!v.isInstantiated()){
                        next = v;
                        break;
                    }
                }

                if(next != null){

                    IntVar tryAssignTo = null;
                    if(next.getName().contains("VarLog")){
                        int position = ArrayUtils.indexOf(VarLog, next);
                        if(varLogIndexToLogIndex(position) != -1){
                            //El caso del varlog es especial. Intentamos agresivamente colocar valores iterando sobre todas las actividades del log
                            //si es que está en el log, eso es
                            boolean success = false;
                            while (logToVarLogPositions.get(i) != null){
                                position = logToVarLogPositions.get(i);
                                if(!VarLog[position].isInstantiated() && VarLog[position].contains(VarModel[position].getValue())){
                                    d.set(VarLog[position], VarModel[position].getValue(), DecisionOperatorFactory.makeIntEq());
                                    success = true;
                                    break;
                                }
                                i++;
                            }
                            if (!success && VarModel[position].isInstantiated()){
                                tryAssignTo = VarModel[position];
                            }
                        } else {
                            if(VarModel[position].isInstantiated()){
                                tryAssignTo = VarModel[position];
                            }
                        }

                    } else if (next.getName().contains("VarModel")){
                        int position = ArrayUtils.indexOf(VarModel, next);
                        if(VarLog[position].isInstantiated()){
                            tryAssignTo = VarLog[position];
                        }
                    } else {
                        //Xarray
                        int position = ArrayUtils.indexOf(Xarray, next);
                        if(VarModel[position].isInstantiated()){
                            tryAssignTo = VarModel[position];
                        }
                    }


                    if(tryAssignTo != null){
                        //intentamos instanciar al mismo o al mas cercano
                        int valueToTry = tryAssignTo.getValue();

                        if(next.contains(valueToTry)){
                            //mismo valor
                            d.set(next, valueToTry, DecisionOperatorFactory.makeIntEq());
                        } else {
                            int nextValue;
                            if(valueToTry > next.getUB()){
                                //se pasa por encima
                                nextValue = next.getUB();
                            } else if (valueToTry < next.getLB()){
                                //se pasa por abajo
                                nextValue = next.getLB();
                            } else {
                                int nearestVarDifference = Integer.MAX_VALUE;
                                DisposableValueIterator dv = next.getValueIterator(true);
                                nextValue = next.getLB();
                                while(dv.hasNext()){
                                    int t = dv.next();
                                    int diff = Math.abs(t - valueToTry);
                                    if(diff <= nearestVarDifference){
                                        nextValue = t;
                                        nearestVarDifference = diff;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            d.set(next, nextValue, DecisionOperatorFactory.makeIntEq());

                        }

                    } else {
                        //si no hay variable a la que intentar asignar el valor, cogemos el menor valor posible
                        d.set(next, next.getLB(), DecisionOperatorFactory.makeIntEq());
                    }

                } else {
                    return null;
                }

                if(ConfigParameters.PRINTMODEL){
                    try{
                        RunProblem.printDebugData("decisions_" + lpoName + " - " + trace.getAttributes().get("concept:name"),d.toString() + "\n");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return d;
            }
        });


        //Build up vars for the search strategy
        /*List<IntVar> var = new LinkedList<>();
        var.add(VarAligment);

        Collections.addAll(var, VarDiff);
        Collections.addAll(var, Xarray);
        Collections.addAll(var, VarModel);
        Collections.addAll(var, VarLog);

        IntVar[] array = var.toArray(new IntVar[var.size()]);


        solver.setSearch(intVarSearch(vars -> {
            for(int i = 0; i < vars.length; i++){
                IntVar v = vars[i];
                if(!v.isInstantiated()){
                    if(ConfigParameters.PRINTMODEL){
                        try{
                            RunProblem.printDebugData("decisions_" + lpoName + " - " + trace.getAttributes().get("concept:name"),vars[i].toString() + "\n");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    return v;
                }
            }
            return null;

        }, v -> v.getLB(),array));*/


		//<editor-fold desc="Estrategias de búsqueda antiguas">


        //Estrategia de busqueda en orden de varlog con xarray - varmodel y varlog que en el caso de varlog intenta fijar primero variables del varmodel
        /*IntVar[] orderedVars = new IntVar[VarLog.length*3];
        int biggestIndex = -1;
        List<Integer> lostvars = new LinkedList<>();

        for(int i = 0; i < VarLog.length; i++){
            int logindex = logToVarLogPositions.get(i);
            if(logindex != -1){
                orderedVars[logindex*2] = Xarray[i];
                orderedVars[logindex*2+1] = VarModel[i];
                biggestIndex = Math.max(biggestIndex, logindex*2+1);
            } else {
                lostvars.add(i);
            }
        }

        for(Integer lostVar : lostvars){
            biggestIndex++;
            orderedVars[biggestIndex] = Xarray[lostVar];
            biggestIndex++;
            orderedVars[biggestIndex] = VarModel[lostVar];
        }

        for(IntVar i : VarLog){
            biggestIndex++;
            orderedVars[biggestIndex] = i;
        }


        solver.setSearch(new AbstractStrategy<IntVar>(orderedVars) {
            PoolManager<IntDecision> pool = new PoolManager<>();

            @Override
            public Decision<IntVar> getDecision() {
                IntDecision d = pool.getE();
                if(d==null) d = new IntDecision(pool);

                int vmodelxarraylength = vars.length*2/3;

                IntVar next = null;
                int i;
                for(i = 0; i < vmodelxarraylength; i++){
                    IntVar v = vars[i];
                    if(!v.isInstantiated()){
                        next = v;
                        break;
                    }
                }

                if(next == null){
                    //varlog
                    IntVar firstNotInstantiated = null;
                    boolean picked = false;
                    for(i = vmodelxarraylength; i < vars.length; i++){
                        next = vars[i];
                        int varmodelindex = (i - vmodelxarraylength)*2 + 1;
                        if(!next.isInstantiated()){
                            if(firstNotInstantiated == null){
                                firstNotInstantiated = next;
                            }
                            if(next.contains(vars[varmodelindex].getValue())){
                                d.set(next, vars[varmodelindex].getValue(), DecisionOperatorFactory.makeIntEq());
                                picked = true;
                                break;
                            }
                        }
                    }
                    if(!picked){
                        if(firstNotInstantiated != null){
                            d.set(firstNotInstantiated, firstNotInstantiated.getLB(), DecisionOperatorFactory.makeIntEq());
                        } else {
                            return null;
                        }
                    }


                } else {
                    if(i % 2 == 0){
                        //xarray
                        if(i+1 < vars.length && vars[i+1].isInstantiated() && vars[i+1].getValue() != 0 && next.contains(vars[i + 1].getValue())){
                            //en este caso lo instanciamos a el (se puede porque los dominios son enumerados)
                            d.set(next, vars[i + 1].getValue(), DecisionOperatorFactory.makeIntEq());
                        } else {
                            //si no, al menor
                            d.set(next, next.getLB(), DecisionOperatorFactory.makeIntEq());
                        }

                    } else {
                        //varmodel
                        if(vars[i-1].isInstantiated() && vars[i-1].getValue() != 0 && next.contains(vars[i-1].getValue())){
                            //en este caso lo instanciamos a el (se puede porque los dominios son enumerados)
                            d.set(next, vars[i - 1].getValue(), DecisionOperatorFactory.makeIntEq());
                        } else {
                            d.set(next, next.getLB(), DecisionOperatorFactory.makeIntEq());
                        }
                    }

                }

                if(ConfigParameters.PRINTMODEL){
                    try{
                        RunProblem.printDebugData("test3",d.toString() + "\n");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return d;
            }
        });*/

        /*solver.setSearch(intVarSearch(new VariableSelector<IntVar>() {

            List<IntVar> varList = null;

            @Override
            public IntVar getVariable(IntVar[] intVars) {

                if(varList == null){
                    varList = Arrays.asList(intVars);
                    varList.sort(Comparator.comparingInt((a) -> fitness((IntVar) a)).reversed());
                } else {
                    //System.out.println("reusado");
                }

                for (IntVar v : varList) {
                    if (!v.isInstantiated()) {
                        if (ConfigParameters.PRINTMODEL) {
                            try {
                                RunProblem.printDebugData("test3", v.toString() + "\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return v;
                    }
                }
                return null;
            }
        }, v -> v.getLB(), array));*/

		/*solver.setSearch(intVarSearch((IntVar[] vars) -> {
			IntVar best = null;
			int bestValue = Integer.MAX_VALUE;

			if(!vars[0].isInstantiated()){
			    return vars[0];
            }

			for(IntVar v : vars){
				if(!v.isInstantiated() && v.getLB() < bestValue){
					best = v;
					bestValue = v.getValue();
				}
			}

			if(ConfigParameters.PRINTMODEL){
				try{
					RunProblem.printDebugData("test3",best.toString() + "\n");
				} catch (Exception e){
					e.printStackTrace();
				}
			}

			return best;
		}, IntVar::getLB,array));*/



        //TODO estrategia de busqueda que prioriza valores sin repeticiones
        /*solver.setSearch(intVarSearch(vars -> {

            if(!vars[0].isInstantiated()){
                return vars[0];
            }

            //Priorizamos las variables que no tienen repeticiones de model
            for(int i = 1; i <= vars.length/2; i++) {
                String varName = finalArrayActivities[i-1];
                try{
                    if(!vars[i].isInstantiated() && repeatedFinal.get(varName) == 1){
                        return vars[i];
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            //Priorizamos las variables que no tienen repeticiones de log
            for(int i = vars.length/2+1; i < vars.length; i++) {
                String varName = finalArrayActivities[i-vars.length/2-1];

                if (!vars[i].isInstantiated()){
                    Integer reps = repeatedFinal.get(varName);
                    if(reps == null){
                        reps = repeatedFinal.get(varName + "-NotInModel");
                    }
                    if(reps == 1) {
                        return vars[i];
                    }
                }

            }


            //Despues podemos tomar los valores con repeticiones (los sin repeticiones ya estaran instanciados)
            for(int i = 0; i < vars.length; i++){
                if(!vars[i].isInstantiated()){
                    return vars[i];
                }
            }
            return null;

        }, v -> v.getLB(),array));*/


		//TODO estrategia de búsqueda que va hacia atrás y prioriza el array (SIN Varalignment)
		/*solver.setSearch(intVarSearch(vars -> {
			for(int i = vars.length-1; i >= 0; i--){
				IntVar v = vars[i];
				if(!v.isInstantiated()){
					if(ConfigParameters.PRINTMODEL){
						try{
							RunProblem.printDebugData("test",vars[i].toString() + "\n");
						} catch (Exception e){
							e.printStackTrace();
						}
					}
					return v;
				}
			}
			return null;

		}, v -> v.getLB(),array));*/

        //TODO estrategia de busqueda que va hacia atrás y prioriza la posición (SIN Varalignment)
        /*solver.setSearch(intVarSearch(vars -> {
                //Aquí lo que hacemos es abusar del hecho de que el tamaño de todos los arrays es el mismo.
                //Sabiendo el índice de cada elemento de uno, sabemos el índice de cada elemento de todos.
                for(int i = vars.length/2-1; i >= 0; i--){
                    if(!vars[i].isInstantiated()){
                        return vars[i];
                    } else if (!vars[i+vars.length/2].isInstantiated()){
                        return vars[i+vars.length/2];
                    }
                }
                return null;


        }, v -> v.getLB(),array));*/

		//TODO estrategia de busqueda que va hacia atrás y prioriza la posición (con Varalignment)
        /*solver.setSearch(intVarSearch(vars -> {
            if(!vars[0].isInstantiated()){
                return vars[0];
            } else {

                //Aquí lo que hacemos es abusar del hecho de que el tamaño de todos los arrays es el mismo.
                //Sabiendo el índice de cada elemento de uno, sabemos el índice de cada elemento de todos.
                for(int i = vars.length/2; i >= 1; i--){
                    if(!vars[i].isInstantiated()){
                        return vars[i];
                    } else if (!vars[i+vars.length/2].isInstantiated()){
                        return vars[i+vars.length/2];
                    }
                }
                return null;

            }

        }, v -> v.getLB(),array));*/

        //</editor-fold>


		//TODO Activar test
        /*try{
			solver.propagate();
		} catch(ContradictionException e){
			e.printStackTrace();
			solver.getEngine().flush();
			System.out.println("ay");
		}
		solver.reset();*/


		int rest = -1;

		while (solver.solve()) {
			rest = VarAligment.getValue();
		}

		/*while (solver.solve());
        int rest = solver.getBestSolutionValue().intValue();*/

        solver.setOut(RunProblem.solverStream);
        solver.printStatistics();

        if(ConfigParameters.SOL && ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS != 0 && solver.getTimeCount() >= ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS / 1000){
			System.out.println(LocalDateTime.now().toLocalTime().toString() + " The combination " + lpoName + " - " + trace.getAttributes().get("concept:name").toString() +
			" was aborted due to time limit.");
            //return -1;
        }

		//rest = solver.getBestSolutionValue().intValue();
		return rest;
	}


	private void setupObjectiveFunction(){

		//TODO Activar test
        //model.arithm(VarAligment,"=",5).post();
		//model.arithm(VarModel[5],"=",28).post();
        //model.arithm(VarModel[0],"=",1).post();
        //model.arithm(VarDiff[6],"=",0).post();
        //model.arithm(VarDiff[20],"=",0).post();
        //model.arithm(VarModel[19],"=",14).post();
		//model.arithm(VarLog[5],"=",21).post();

		// Evitar que en instante t ocurra dos cosas diferentes entre VarModel y VarLog
		for (int i = 0; i < VarModel.length; i++) {
			for (int j = 0; j < VarModel.length; j++) {
				if (i != j) {

				    //If any variable is in the model, no other variable nor log must share that position
				    model.ifThen(model.arithm(VarModel[i],"!=", 0),
                            model.and(model.arithm(VarModel[i], "!=", VarModel[j]), model.arithm(VarModel[i], "!=",VarLog[j])));

                    //you can't execute two activities at the same time (alldifferent)
                    //Now with chocosolver (alldifferentexcept0) on xarra
					//model.ifThen(model.arithm(VarLog[i],"!=",0),model.arithm(VarLog[i],"!=",VarLog[j]));

					// two different activities cannot be executed at the same
					// time

					// solver.add(solver.imply(solver.neq(VarModel[i], 0),
					// solver.neq(VarModel[i], VarLog[j])));
				}
			}

			// solver.add(solver.or(solver.eq(VarLog[i],VarModel[i]),solver.or(solver.eq(0,VarLog[i]),
			// solver.eq(0,VarModel[i])) ));
		}

        model.allDifferentExcept0(VarLog).post();
		model.allDifferentExcept0(Xarray).post();

		for (int i = 0; i < VarModel.length; i++) {

			//Se ha tenido que descomprimir el ifthenelse que había interior en el createcsp original en sus partes
            //Esto calcula el VarDiff como 0, 1 ó 2 dependiendo de si concuerda o no
			model.ifOnlyIf(model.arithm(VarModel[i], "=", VarLog[i]),model.arithm(VarDiff[i],"=",0));
			model.ifOnlyIf(model.or(model.arithm(VarModel[i],"=",0),
					model.arithm(VarLog[i],"=",0)),model.arithm(VarDiff[i],"=",1));

			//It would not be necessary to define the restrictions for VarDiff 2, since it's the only domain value remaining
		}

		//Calculamos el VarAlignment
		model.sum(VarDiff,"=", VarAligment).post();

		int tam = listOfActivitiesLog.size() + listOfActivitiesModel.size() + 1;

		Constraint[] valuesAsigned = new Constraint[tam];
		BoolVar[] reifVarModel = new BoolVar[tam];

		// Evitar huecos
		for (int i = 0; i < tam; i++) {
			reifVarModel[i] = model.boolVar(i + "reifModel");
		}

		for (int i = 1; i < tam; i++) {
		    //Esta constraint no posteada significa "cada posición > 0 debe estar al menos una vez en el log o en el modelo"
			valuesAsigned[i] = model.or(model.arithm(VarModel[0], "=", i), model.arithm(VarLog[0], "=", i));
			for (int j = 1; j < VarModel.length; j++) {
				valuesAsigned[i] = model.or(valuesAsigned[i],model.arithm(VarModel[j], "=", i), model.arithm(VarLog[j], "=", i));
			}
            /*valuesAsigned[i] = model.or(model.arithm(VarModel[0], "=", i), model.arithm(Xarray[0], "=", i));
            for (int j = 1; j < VarModel.length; j++) {
                valuesAsigned[i] = model.or(valuesAsigned[i],model.arithm(VarModel[j], "=", i), model.arithm(Xarray [j], "=", i));
            }*/

			//Al reificar en choco no debe postearse
			valuesAsigned[i].reifyWith(reifVarModel[i]);
		}

		for (int i = 2; i < tam; i++) {
		    //Si la posición i está en el modelo o en el log al menos una vez, las anteriores también. Esto se postea solo.
			model.ifThen(reifVarModel[i],model.arithm(reifVarModel[i - 1],"=",1));
		}

        //Esto lo que hace es comprobar que las variables de varmodel y varlog como mucho llegarán hasta su posición relativa + varalignment
		/*EventLpo finalEvent = null;
		for(Arc arc : arcs){
			EventLpo target = lpoFile.findEventsById(arc.getTarget());
			if(target.getIs_end()){
				finalEvent = target;
				break;
			}
		}
		IntVar[] temparray = {model.intVar(VarModel.length),VarAligment};
		model.sum(temparray, ">=",VarModel[varModelPostions.get(finalEvent.getId())]).post();

		IntVar[] temparray2 = {model.intVar(VarLog.length),VarAligment};
		model.sum(temparray2, ">=", VarLog[VarLog.length-1]).post();*/

		//Verdadera funcion de busqueda, el resto son restricciones
		model.setObjective(Model.MINIMIZE,VarAligment);
	}


	// Version 2: new version to cover new cases with multiple repeated
	// activities
	private void createLogConstraints() {
		List<String> repList = new ArrayList<>();

		for (int i = 0; i < listOfActivitiesLog.size() - 1; i++) {

			List<Integer> listIndiceFirst = new ArrayList<>();
			List<Integer> listIndiceSecond = new ArrayList<>();

			int firstindice;
			int secondindice;

			//Activity name
			String first = listOfActivitiesLog.get(i);
			String second = listOfActivitiesLog.get(i + 1);

			//Number of repetitions
			/*int valorFirst = repeatedFinal.get(first);
			int valorSecond = repeatedFinal.get(second);*/

			for (int j = 0; j < finalArrayActivities.length; j++) {
				if (finalArrayActivities[j].equals(first) || first.equals(finalArrayActivities[j] + "-NotInModel")) {
					listIndiceFirst.add(j);
				}
			}

			if (listIndiceFirst.size() == 1) {
				firstindice = listIndiceFirst.get(0);
			} else {

				int cont = 0;
                for (String aRepList : repList) {
                    if (aRepList.equals(first) || first.equals(aRepList)) {
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
                for (String aRepList : repList) {
                    if (aRepList.equals(second) || second.equals(aRepList)) {
                        cont++;
                    }
                }

				secondindice = listIndiceSecond.get(cont);
			}

			//Todas las restricciones de precedencia de actividades de log se hacen en el xarray para desacoplar nombres
			if(i == 0){
				model.arithm(Xarray[firstindice], ">", 0).post();
			}

			model.arithm(Xarray[firstindice], "<", Xarray[secondindice]).post();
		}

		//Hora de asociar xarray y varlog
		int i = 0;

		while (i < finalArrayActivities.length){
		    int numReps;
		    if(listOfActivitiesModelTranslated.contains(finalArrayActivities[i])){
                numReps = repeatedFinal.get(finalArrayActivities[i]);
            } else {
                numReps = repeatedFinal.get(finalArrayActivities[i]+"-NotInModel");
            }

		    if(numReps > 1){
                //Si hay repeticiones hay que ver todos los posibles casos entre xarray y varlog
                IntVar[] xarraytomatch = subarrayWithoutCopy(Xarray,i,i+numReps);
                IntVar[] varlogtomatch = subarrayWithoutCopy(VarLog,i,i+numReps);

                //Por ejemplo, que sabemos que las longitudes de los arrays son iguales

                model.post(new EqualSumsConstraint(xarraytomatch, varlogtomatch));

                for (int j = 0; j < varlogtomatch.length; j++) {
                    //En sentido xarray -> varlog

                    model.post(new BelongsToConstraint(xarraytomatch[j], varlogtomatch));
                    //En sentido varlog -> xarray
                    model.post(new BelongsToConstraint(varlogtomatch[j], xarraytomatch));
                }

                i += numReps;
            } else {
		        //En caso de no haber repeticiones el desacoplamiento no nos hace falta, lo quitamos con una igualdad
		        model.arithm(Xarray[i], "=",VarLog[i]).post();
		        i++;
            }
        }
	}

	private void createModelConstraints() {

		for (Arc arc : arcs) {

			EventLpo source = lpoFile.findEventsById(arc.getSource());
			EventLpo target = lpoFile.findEventsById(arc.getTarget());

			//if it's the model start
			if (source.getIs_star()) {
				model.arithm(VarModel[varModelPostions.get(source.getId())],">",0).post();
			}

			int firstindice = varModelPostions.get(source.getId());
			int secondindice = varModelPostions.get(target.getId());

			//model activities should be sequential: f.e. if A -> B A should happen before B in the model.
			model.arithm(VarModel[firstindice],"<",VarModel[secondindice]).post();
		}

		// Controlar las no apariciones Log-Modelo
		List<String> repList = new LinkedList<>();

		for (int i = 0; i < finalArrayActivities.length; i++) {
			if (!listOfActivitiesModelTranslated.contains(finalArrayActivities[i])
					&& listOfActivitiesLog.contains(finalArrayActivities[i] + "-NotInModel")) {
				// Aparece en el Log pero no en el Modelo
				model.arithm(VarModel[i],"=",0).post();
			} else if (listOfActivitiesModelTranslated.contains(finalArrayActivities[i])
					&& !listOfActivitiesLog.contains(finalArrayActivities[i])) {
				// Aparece en el Modelo pero no en el Log, cambiamos el xarray para que pueda ser cualquier elemento
                //TODO realmente es necesario? no podemos simplemente cambiar el log y ya?
				model.arithm(Xarray[i],"=",0).post();
			} else {

				// Aparece en Log y Modelo pero la repeticion de las actividades
				// no es lo mismo
				if (repeatedActivityLog.get(finalArrayActivities[i]) > repeatedActivityModel
						.get(finalArrayActivities[i]) && !repList.contains(finalArrayActivities[i])) {

					int j = i + repeatedActivityModel.get(finalArrayActivities[i]);
					int cont = repeatedActivityLog.get(finalArrayActivities[i])
							- repeatedActivityModel.get(finalArrayActivities[i]);
					while (cont > 0 && j < finalArrayActivities.length) {
						model.arithm(VarModel[j],"=",0).post();
						j++;
						cont--;
					}

					//si aparece más en el modelo, las que sobren son 0
				} else if (repeatedActivityLog.get(finalArrayActivities[i]) < repeatedActivityModel
						.get(finalArrayActivities[i]) && !repList.contains(finalArrayActivities[i])) {

                    // Restriction on number of zeroes
                    int numberOfElements = repeatedFinal.get(finalArrayActivities[i]);
                    int cont = repeatedActivityModel.get(finalArrayActivities[i])
                            - repeatedActivityLog.get(finalArrayActivities[i]);
                    IntVar[] var = new IntVar[numberOfElements];
                    int max = i + numberOfElements;
                    int offset = i;
                    while (i < max) {
                        var[i - offset] = Xarray[i];
                        i++;
                    }
                    model.count(0, var, model.intVar(cont)).post();
                }


				repList.add(finalArrayActivities[i]);
			}
		}

	}

	private void populateLogVariables() {
		for (String s : listOfActivitiesLog) {
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

	private void createArrays(){

		finalArrayActivities = new String[tamFinal];

		VarModel = new IntVar[tamFinal];
		VarModelPositions = new Integer[tamFinal];
		VarLog = new IntVar[tamFinal];
		VarDiff = new IntVar[tamFinal];
		Xarray = new IntVar[tamFinal];
        // Considering previous missalignment and using the optimization based on lack of repetitions of variables between varlog and varmodel

        if (lastMissAlignment != -1) {
            VarAligment = model.intVar("SumTotal", minAlignment , lastMissAlignment);
        } else {
            VarAligment = model.intVar("SumTotal", minAlignment, tamFinal * 2);
        }

	}

	private void initializeIntermediateVariables(){
        int arrayIndex = 0;

        //FinalArray will be a list of all activities, without the "-notinmodel" and including all duplicates
        for (String s : repeatedFinal.keySet()) {
            for (int i = 0; i < repeatedFinal.get(s); i++) {
                if (listOfActivitiesLog.contains(s) && s.endsWith("-NotInModel")) {
                    finalArrayActivities[arrayIndex] = s.substring(0, s.lastIndexOf("-"));
                } else {
                    finalArrayActivities[arrayIndex] = s;
                }
                arrayIndex++;
            }
        }

        //Initializing
        Arrays.fill(VarModelPositions,-1);


        //Esto genera una correlación entre las posiciones originales de las actividades del modelo y su posición en el array de actividades combinado modelo-log
        //Esto genera en varpostions una correlación id del evento -> posición en varmodel. varmodelpositions no sirve para nada, no tiene más que flags para que este metodo recuerde
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

        //Now we calculate all floyd mins and maxes in one go for efficiency
        floydMinimumsModel = calculateModelMinimums();
        floydMaximumsModel = calculateModelMaximumPosition();
    }

	private void reduceDomains(){

		//Initializing array variables
		for (int i = 0; i < tamFinal; i++) {

			VarModel[i] = model.intVar("Variable " + finalArrayActivities[i] + "_" + i + ": " + "VarModel",floydMinimumsModel[i],
					listOfActivitiesLog.size() + listOfActivitiesModel.size() - floydMaximumsModel[i]);

            /*VarModel[i] = model.intVar("Variable " + finalArrayActivities[i] + "_" + i + ": " + "VarModel",0,
                    listOfActivitiesLog.size() + listOfActivitiesModel.size() - bestDistanceToEndTransition);*/

			String currentElement = finalArrayActivities[i];

			/*VarModel[i] = model.intVar("Variable " + currentElement + "_" + i + ": " + "VarModel",0,
					listOfActivitiesLog.size() + listOfActivitiesModel.size());*/

			int logindex = varLogIndexToLogIndex(i);
            logToVarLogPositions.put(logindex,i);

			if(logindex == -1){
                VarLog[i] = model.intVar("Variable " + currentElement + "_" + i + ": " + "VarLog",0,
                        listOfActivitiesLog.size() + listOfActivitiesModel.size());
            } else {
                VarLog[i] = model.intVar("Variable " + finalArrayActivities[i] + "_" + i + ": " + "VarLog",0,
                        listOfActivitiesModel.size() + logindex);
            }

			VarDiff[i] = model.intVar("Variable " + currentElement + "_" + i + ": " + "VarDiff",0, 2);

			//This array associates log to model, not the opposite
			if (repeatedActivityModel.containsKey(currentElement) && !repeatedActivityLog.containsKey(currentElement)) {
				Xarray[i] = model.intVar("Variable " + currentElement + "_" + i + ": " + "Xarray",0);
			} else {
				Xarray[i] = model.intVar("Variable " + currentElement + "_" + i + ": " + "Xarray",0, listOfActivitiesLog.size() + listOfActivitiesModel.size());
			}

			//TODO optimization with maximums and constraints based on VarAlignment

            int lowerSize =  Math.min(listOfActivitiesLog.size(), listOfActivitiesModel.size());

			model.arithm(VarModel[i], "<=", model.intOffsetView(VarAligment, lowerSize - floydMaximumsModel[i])).post();

			if(logindex == -1){
                model.arithm(VarLog[i], "<=", model.intOffsetView(VarAligment, listOfActivitiesLog.size())).post();
            } else {
                int numreps = repeatedFinal.get(finalArrayActivities[i]) == null ? repeatedFinal.get(finalArrayActivities[i] + "-NotInModel") : repeatedFinal.get(finalArrayActivities[i]);
                if(numreps == 1) {
                    model.arithm(VarLog[i], "<=", model.intOffsetView(VarAligment, logindex + 1)).post();
                } else {
                    //we pick the last activity in the log to avoid contradictions on the swapping (xarray's job)
					model.arithm(VarLog[i], "<=", model.intOffsetView(VarAligment, lastLogIndexWithActivityName(finalArrayActivities[i]) + 1)).post();
                }
            }



		}
	}

    private int varLogIndexToLogIndex(int varlogIndex){
        String activityName = finalArrayActivities[varlogIndex];

        //We look backwards for the number of repetitions
        int numRepetitions = 0;

        for(int i = varlogIndex; i>=0; i--){
            if(finalArrayActivities[i].equals(activityName)){
                numRepetitions++;
            } else{
                break;
            }
        }

        //Now we go forwards knowing the activity name and number of repetitions and search for it
        int currentRepetition = 0;
        for(int i = 0; i < listOfActivitiesLog.size(); i++){
            String s = listOfActivitiesLog.get(i);
            if(s.equals(activityName) || s.equals(activityName + "-NotInModel")){
                currentRepetition++;
                if(currentRepetition == numRepetitions){
                    return i;
                }
            }
        }
        return -1;
    }

    private int lastLogIndexWithActivityName(String activityName){
		for(int i = listOfActivitiesLog.size()-1; i>=0; i--){
			String s = listOfActivitiesLog.get(i);
			if(s.equals(activityName) || s.equals(activityName + "-NotInModel")){
				return i;
			}
		}
		return -1;
	}


    //Calcula el numero de incongruencias entre los dos mapas
    private static int sumDiffMap(Map<String,Integer> map1, Map<String,Integer> map2){
	    List<String> usedVariables = new ArrayList<>();

	    int total = 0;

	    //Basandonos en un mapa comprobamos el numero de repeticiones de las del otro
	    for(String s : map1.keySet()){
	        int numReps1 = map1.get(s);
	        int numReps2 = 0;
	        if(map2.containsKey(s)){
	            numReps2 = map2.get(s);
            }

            total += Math.abs(numReps1-numReps2);
	        usedVariables.add(s);
        }

        //Si hay variables en este mapa que no estaban en el otro pues eso son mas incongruencias que hay
        for(String s : map2.keySet()){
	        if(!usedVariables.contains(s)){
	            total += map2.get(s);
            }
        }

        return total;
    }


    private IntVar[] subarrayWithoutCopy(IntVar[] originalArray, int start, int end){
		IntVar[] arr = new IntVar[end-start];
        System.arraycopy(originalArray, start, arr, 0, end - start);
		/*for(int i = start; i < end; i++){
			arr[i - start] = originalArray[i];
		}*/
		return arr;
	}

	public boolean detectHighRepetitionNumber(PetriNetLpo lpoFile, List<String> logActivities){
		//Model repetitions
		Map<String, Integer> repeatedModel = new HashMap<>();

		for (Pnml.Net.Transition t : lpoFile.getTransitions()) {
			if (repeatedModel.keySet().contains(t.getName().getText())) {
				repeatedModel.put(t.getName().getText(), repeatedModel.get(t.getName().getText()) + 1);
			} else {
				repeatedModel.put(t.getName().getText(), 1);

			}
		}

		//Log repetitions
		Map<String, Integer> repeatedLog = new HashMap<>();

		for (String s : logActivities) {
			if (repeatedLog.keySet().contains(s)) {
				repeatedLog.put(s, repeatedLog.get(s) + 1);
			} else {
				repeatedLog.put(s, 1);
			}

		}

		boolean repetition = false;

		for(String s : repeatedModel.keySet()){
			if(repeatedLog.containsKey(s) && (repeatedModel.get(s) >= ConfigParameters.NUMBER_REPETITIONS_HARD || repeatedLog.get(s) >= ConfigParameters.NUMBER_REPETITIONS_HARD)){
				repetition = true;
				break;
			}
		}
		return repetition;
	}

	public static int minMissAlignment(PetriNetLpo lpoFile, List<String> logActivities){

		//Model repetitions
		Map<String, Integer> repeatedModel = new HashMap<>();

		for (Transition t : lpoFile.getTransitions()) {
			if (repeatedModel.keySet().contains(t.getName().getText())) {
				repeatedModel.put(t.getName().getText(), repeatedModel.get(t.getName().getText()) + 1);
			} else {
				repeatedModel.put(t.getName().getText(), 1);

			}
		}

		//Log repetitions
		Map<String, Integer> repeatedLog = new HashMap<>();

		for (String s : logActivities) {
			if (repeatedLog.keySet().contains(s)) {
				repeatedLog.put(s, repeatedLog.get(s) + 1);
			} else {
				repeatedLog.put(s, 1);
			}

		}

		return sumDiffMap(repeatedModel,repeatedLog);
	}

    public String debugData(){
	    String data = "";
        if(ConfigParameters.SOL){
            for (int i = 0; i < VarModel.length; i++) {
                data += VarModel[i] + " <--> " + (VarLog[i]) + "\n";
            }

            data += "\n" + ("---- Xarray ----");

            for (IntVar aXarray : Xarray) {
                if (aXarray.getRange() == 0) {
                    //System.out.println((Xarray.get(xa)[i]).getName() + ": " + Xarray.get(xa)[i].getValue());
                    data+="\n" + aXarray.getValue();
                } else {
                    //System.out.println((Xarray.get(xa)[i]).getName() + ": " + Xarray.get(xa)[i]);
                    data+="\n" + aXarray;

                }
            }

        }
        if(ConfigParameters.PRINTMODEL){
            data += "\n" + model.toString();
        }
        return data;
    }
    private int[] calculateModelMinimums(){

	    int[] results = new int[VarModel.length];

        Set<EventLpo> events = new HashSet<>();
        Set<EventLpo> nextEvents;

        Set<String> pastEventIDs = new HashSet<>();
	    //find the start event
        for(EventLpo e: lpoFile.getEventLpo()){
            if(e.getIs_star()){
                events.add(e);
                break;
            }
        }

        //Now iterate through going ahead through transitions
		//Since this is a LPO this should end
        int minimum = 0;
        while(!events.isEmpty()){
        	nextEvents = new HashSet<>();
        	//for each event we add to the iterated id list and set the minimum to the current index
            for(EventLpo e : events){
                results[varModelPostions.get(e.getId())] = minimum;
                List<String> out = e.getOutputs();
                pastEventIDs.add(e.getId());

                //for each edge, we check if all the targets sources have already been passed, if they have, then we can iterate through its target
                for(String o : out){
                	EventLpo newev = lpoFile.findEventsById(o);
                	if(pastEventIDs.containsAll(newev.getInputs())){
						nextEvents.add(newev);
					}
				}
            }
            //readying for next iteration
            minimum++;
            events = nextEvents;
        }
        return results;
    }

    //This algorithm calculates the minimum number of activities that come after every one.
    //This implementation uses paths that are stored
    //If this becomes a problem, a recursive approach that branches on multiple input edges and stops after finding
    // multiple output edges to that element could be more efficient in memory
    private int[] calculateModelMaximumPosition(){

        int[] results = new int[VarModel.length];

        Arrays.fill(results, 0);

        Set<Tuple2<String,Set<String>>> events = new HashSet<>();
        Set<Tuple2<String,Set<String>>> nextEvents;


        Map<String,Set<String>> pastEvents = new HashMap<>();

        //find the start event
        for(EventLpo e: lpoFile.getEventLpo()){
            if(e.getIs_end()){
                events.add(new Tuple2<>(e.getId(),new HashSet<>()));
                break;
            }
        }

        //Now iterate through going ahead through transitions
        //Since this is a LPO this should end
        while(!events.isEmpty()){
            nextEvents = new HashSet<>();
            for(Tuple2<String,Set<String>> eventData : events){
                results[varModelPostions.get(eventData._1)] = eventData._2.size();
                EventLpo e = lpoFile.findEventsById(eventData._1);
                pastEvents.put(eventData._1,eventData._2);
                List<String> in = e.getInputs();
                for(String o : in){
                    EventLpo input = lpoFile.findEventsById(o);
                    if(pastEvents.keySet().containsAll(input.getOutputs())){
                        Set<String> fullElementPath = new HashSet<>();
                        for(String successor : input.getOutputs()){
                            fullElementPath.addAll(pastEvents.get(successor));
                            fullElementPath.add(successor);
                        }
                        nextEvents.add(new Tuple2<>(o, fullElementPath));
                    }

                }
            }
            events = nextEvents;


        }
        return results;
	}

    //TODO comprobar si el permutations funciona
    public int testSearchStrategyVariables(){
        if(!initialized){
            return -1;
        }
        Solver solver = model.getSolver();

        solver.limitTime(10000);

        //Build up vars for the search strategy
        List<IntVar> var = new LinkedList<>();
        var.add(VarAligment);

        Collections.addAll(var, VarDiff);
        Collections.addAll(var, Xarray);
        Collections.addAll(var, VarModel);
        Collections.addAll(var, VarLog);

        List<IntVar[]> collectionArray = new LinkedList<>();

        IntVar[] arr = new IntVar[1];
        arr[0] = VarAligment;

        collectionArray.add(arr);
        collectionArray.add(VarDiff);
        collectionArray.add(Xarray);
        collectionArray.add(VarModel);
        collectionArray.add(VarLog);

        Collection<List<IntVar[]>> col = Collections2.permutations(collectionArray);

        /*for(int i = 2; i <= collectionArray.size(); i++){
            for(int j = 0; j < collectionArray.size()-i; j++){
                int[] combinations = new int[i];
                for(int startI = combinations.length-1; startI>=0;startI--){
                    combinations[startI] =
                }
            }
        }*/

        IntVar[] array = var.toArray(new IntVar[var.size()]);
        solver.setSearch(intVarSearch((IntVar[] vars) -> {
            IntVar best = null;
            int bestValue = Integer.MAX_VALUE;

            for(IntVar v : vars){
                if(!v.isInstantiated() && v.getLB() < bestValue){
                    best = v;
                    bestValue = v.getValue();
                }
            }

            if(ConfigParameters.PRINTMODEL){
                try{
                    RunProblem.printDebugData("test3",best.toString() + "\n");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            return best;
        }, IntVar::getLB,array));

        int rest = -1;

        while (solver.solve()) {
            rest = VarAligment.getValue();
        }

        if(ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS != 0 && solver.getTimeCount() >= ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS / 1000){
            return -1;
        }

        return rest;
    }

}
