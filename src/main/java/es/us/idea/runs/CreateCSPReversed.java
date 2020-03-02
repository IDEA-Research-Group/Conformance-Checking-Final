package es.us.idea.runs;

import PetriNet.LPO.EventLpo;
import PetriNet.LPO.PetriNetLpo;
import es.idea.pnml.Pnml;
import es.idea.xes.XesUtils;
import es.us.idea.runs.constraints.Ascending1Except0Constraint;
import es.us.idea.runs.constraints.BelongsToConstraint;
import es.us.idea.runs.constraints.EqualAfterMappingConstraint;
import org.apache.commons.lang3.ArrayUtils;
import org.chocosolver.memory.IStateBitSet;
import org.chocosolver.memory.IStateInt;
import org.chocosolver.memory.IStateIntVector;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.IMonitorContradiction;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.decision.IntDecision;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.PoolManager;
import org.deckfour.xes.model.XTrace;

import java.time.LocalDateTime;
import java.util.*;

import static org.chocosolver.solver.search.strategy.Search.intVarSearch;

public class CreateCSPReversed {

    // Model
    private PetriNetLpo lpoFile;
    private String lpoName;
    protected LinkedHashMap<String, Integer> repeatedActivityModel;
    private List<String> listOfActivitiesModel;
    private List<String> listOfActivitiesModelTranslated;
    protected List<Pnml.Net.Arc> arcs;

    // Log
    private XTrace trace;
    private List<String> listOfActivitiesInstance;
    protected LinkedHashMap<String, Integer> repeatedActivityInstance;


    // CSP Solver
    protected Model model;

    // CSP Variables
    private IntVar[] modelActivities;
    private IntVar[] instanceActivities;
    private IntVar VarAligment;
    private IntVar[] modelActivityPositions;
    private IntVar[] VarCalculation;

    private IntVar[] ReverseVarCalculation;
    private IntVar solutionLength;
    private IntVar ReverseVarAlignment;

    // Derived attributes
    protected HashMap<String, Integer> repeatedFinal;
    private Integer tamFinal;
    private int[] floydMinimumsModel;
    private int[] floydMaximumsModel;
    private HashMap<Integer, Integer> modelTranslationRepeatedInts;
    private HashMap<Integer, Integer> instanceTranslationRepeatedInts;
    private HashMap<String, Integer> modelTranslationIDs;

    private HashMap<Integer, EventLpo> modelTranslationNode;
    // Last missalignment
    private Integer lastMissAlignment;

    //If initialized, the problem both was initialized and the domains are correct
    protected boolean initialized = false;
    private int minAlignment;

    private Integer startEventIndex;


    CreateCSPReversed(RunProblem.LPOContainer lpo, XTrace trace, int minAlignment, int lastMissAlignment){

        // Model y Log initializing
        this.trace = trace;
        this.lpoFile = lpo.lpo;
        listOfActivitiesInstance = XesUtils.getLog(trace, lpoFile);
        lpoName = lpo.lpoFileName;

        // Initializing variables
        model = new Model();
        repeatedActivityModel = new LinkedHashMap<>();
        listOfActivitiesModel = new ArrayList<>();
        listOfActivitiesModelTranslated = new ArrayList<>();
        arcs = new ArrayList<>();
        repeatedActivityInstance = new LinkedHashMap<>();
        repeatedFinal = new LinkedHashMap<>();
        tamFinal = 0;

        modelTranslationRepeatedInts = new LinkedHashMap<>();
        instanceTranslationRepeatedInts = new LinkedHashMap<>();

        modelTranslationIDs = new LinkedHashMap<>();
        modelTranslationNode = new LinkedHashMap<>();

        // Population and processing data
        populateModelVariables(lpoFile);
        populateLogVariables();
        processCombination();

        //tamFinal = repeatedFinal.values().stream().reduce((i1, i2) -> i1 + i2).get();
        tamFinal = listOfActivitiesInstance.size()+listOfActivitiesModel.size()-2;

        this.minAlignment = minAlignment;
        this.lastMissAlignment = lastMissAlignment;
        if(!(minAlignment > tamFinal || minAlignment > lastMissAlignment && lastMissAlignment >= 0)){
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
        }
    }

    private void populateLogVariables() {
        for (String s : listOfActivitiesInstance) {
            if (repeatedActivityInstance.keySet().contains(s)) {
                repeatedActivityInstance.put(s, repeatedActivityInstance.get(s) + 1);
            } else {
                repeatedActivityInstance.put(s, 1);
            }
        }
    }

    private void populateModelVariables(PetriNetLpo lpoFile) {
        arcs = lpoFile.getArcs();
        for (Pnml.Net.Transition t : lpoFile.getTransitions()) {
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
        Set<String> activitiesLog = repeatedActivityInstance.keySet();

        for (String s : activitiesLog) {
            if (activitiesModel.contains(s)) {
                if (repeatedActivityInstance.get(s) <= repeatedActivityModel.get(s)) {
                    repeatedFinal.put(s, repeatedActivityModel.get(s));
                } else {
                    repeatedFinal.put(s, repeatedActivityInstance.get(s));
                }
            } else {
                repeatedFinal.put(s, repeatedActivityInstance.get(s));
            }
        }

        for (String s : activitiesModel) {
            if (activitiesLog.contains(s)) {
                if (repeatedActivityInstance.get(s) <= repeatedActivityModel.get(s)) {
                    repeatedFinal.put(s, repeatedActivityModel.get(s));
                } else {
                    repeatedFinal.put(s, repeatedActivityInstance.get(s));
                }
            } else {
                repeatedFinal.put(s, repeatedActivityModel.get(s));
            }

        }

    }

    private void createArrays() {
        modelActivities = new IntVar[listOfActivitiesInstance.size() + listOfActivitiesModel.size()-2];
        instanceActivities = new IntVar[listOfActivitiesInstance.size() + listOfActivitiesModel.size()-2];
        VarCalculation = new IntVar[listOfActivitiesInstance.size() + listOfActivitiesModel.size()-2];
        ReverseVarCalculation = new IntVar[listOfActivitiesInstance.size() + listOfActivitiesModel.size()-2];

        modelActivityPositions = new IntVar[listOfActivitiesModel.size()];
        // Considering previous missalignment and using the optimization based on lack of repetitions of variables between varlog and varmodel

        if (lastMissAlignment != -1) {
            VarAligment = model.intVar("Conformance", minAlignment, lastMissAlignment);
        } else {
            VarAligment = model.intVar("Conformance", minAlignment, tamFinal * 2);
        }
    }

    private void initializeIntermediateVariables(){
        //translation table IDs and node
        //model
        for(int i = 0; i < listOfActivitiesModelTranslated.size(); i++){
            modelTranslationIDs.put(listOfActivitiesModel.get(i), i+1);
            modelTranslationNode.put(i+1, lpoFile.findEventsById(listOfActivitiesModel.get(i)));
        }


        List<String> alreadyIn = new ArrayList<>();

        //translation table repeated integers (for efficient repeated activity checking)
        //model
        for(int i = 0; i < listOfActivitiesModelTranslated.size(); i++){
            if(alreadyIn.contains(listOfActivitiesModelTranslated.get(i))){
                modelTranslationRepeatedInts.put(i+1, alreadyIn.indexOf(listOfActivitiesModelTranslated.get(i))+1);
            } else {
                modelTranslationRepeatedInts.put(i+1,alreadyIn.size()+1);
                alreadyIn.add(listOfActivitiesModelTranslated.get(i));
            }
        }
        //log
        for(int i = 0; i < listOfActivitiesInstance.size(); i++){
            if(alreadyIn.contains(listOfActivitiesInstance.get(i))){
                instanceTranslationRepeatedInts.put(i+1, alreadyIn.indexOf(listOfActivitiesInstance.get(i))+1);
            } else {
                instanceTranslationRepeatedInts.put(i+1,alreadyIn.size()+1);
                alreadyIn.add(listOfActivitiesInstance.get(i));
            }
        }

    }

    private void reduceDomains(){

        //variables de problema

        Integer[] modeloptions = modelTranslationRepeatedInts.keySet().toArray(new Integer[modelTranslationRepeatedInts.size()+1]);
        modeloptions[modeloptions.length-1] = 0;

        Integer[] instanceoptions = instanceTranslationRepeatedInts.keySet().toArray(new Integer[instanceTranslationRepeatedInts.size()+1]);
        instanceoptions[instanceoptions.length-1] = 0;

        for(int i = 0; i < tamFinal; i++){
            modelActivities[i] = model.intVar("Model Position " + i, ArrayUtils.toPrimitive(modeloptions));
            instanceActivities[i] = model.intVar("Instance Position " + i, ArrayUtils.toPrimitive(instanceoptions));
            VarCalculation[i] = model.boolVar("Alignment Partial Position " + i);
            ReverseVarCalculation[i] = model.boolVar("Optimization Partial Position " + i);
        }

        //variables de posiciones de modelo
        for(int i = 0; i < listOfActivitiesModel.size(); i++){
            modelActivityPositions[i] = model.intVar("Model activity " + listOfActivitiesModelTranslated.get(i) + " position",
                    1, listOfActivitiesModel.size()+listOfActivitiesInstance.size());
        }

    }

    private void createModelConstraints(){
        //fijando posiciones por variable
        for(int i = 0; i < tamFinal; i++){
            for(int j = 0; j < listOfActivitiesModel.size(); j++){
                model.ifThen(model.arithm(modelActivities[i], "=", j+1),model.arithm(modelActivityPositions[j], "=", i+1));
            }
        }

        //y ahora establecemos precedencias de variables basandonos en el modelo en si
        for (Pnml.Net.Arc arc : arcs) {
            EventLpo source = lpoFile.findEventsById(arc.getSource());
            EventLpo target = lpoFile.findEventsById(arc.getTarget());
            //model activities should be sequential: f.e. if A -> B A should happen before B in the model.
            model.arithm(modelActivityPositions[listOfActivitiesModel.indexOf(source.getId())],"<",
                    modelActivityPositions[listOfActivitiesModel.indexOf(target.getId())]).post();
        }

        //todas las actividades del modelo deben estar presentes
        for(int i = 0; i < listOfActivitiesModel.size(); i++){
            model.post(new BelongsToConstraint(model.intVar(i+1),modelActivities));
        }

        //no se pueden repetir, salvo ceros

        model.allDifferentExcept0(modelActivities).post();
    }

    private void createLogConstraints() {

        model.post(new Ascending1Except0Constraint(instanceActivities));

        //todas las actividades de log deben estar presentes pero en terminos de restricciones es suficiente la ultima y en cascada vendran las demas
        model.post(new BelongsToConstraint(model.intVar(Collections.max(instanceTranslationRepeatedInts.keySet())),instanceActivities));
        //no se pueden repetir salvo ceros
        model.allDifferentExcept0(instanceActivities).post();
    }

    private void setupObjectiveFunction(){

        //model.arithm(modelActivities[0],"=", 1).post();

        solutionLength = model.intVar("Solution Length", 0, tamFinal * 2);
        ReverseVarAlignment = model.intVar("Reversed Conformance", 0, tamFinal * 2);

        for(int i = 0; i < tamFinal; i++){
            //only if elements are named the same they can be put together

            /*model.ifThen(model.and(model.arithm(modelActivities[i],"!=",0), model.arithm(instanceActivities[i],"!=",0)),
                    new EqualAfterMappingConstraint(modelActivities[i], instanceActivities[i], modelTranslationRepeatedInts, instanceTranslationRepeatedInts));*/
            model.post(new EqualAfterMappingConstraint(modelActivities[i], instanceActivities[i], modelTranslationRepeatedInts, instanceTranslationRepeatedInts));

            //there must be activities carrying out at any given point in time
            //also calculates length

            Constraint preAnd = model.trueConstraint();
            for(int j = i+1; j < tamFinal; j++){
                preAnd = model.and(preAnd, model.and(model.arithm(modelActivities[j],"=", 0),model.arithm(instanceActivities[j],"=", 0)));
            }


            model.ifThen(model.and(model.arithm(modelActivities[i],"=",0),model.arithm(instanceActivities[i],"=",0)),
                    model.and(preAnd, model.arithm(solutionLength, "<=", i+1)));

        }

        //Calculating varalignment
        /*List<IntVar> mixedList = new ArrayList<>();
        Collections.addAll(mixedList, modelActivities);
        Collections.addAll(mixedList, instanceActivities);
        IntVar[] mixedArray = mixedList.toArray(new IntVar[mixedList.size()]);*/


        for (int i = 0; i < modelActivities.length; i++) {

            //Se ha tenido que descomprimir el ifthenelse que había interior en el createcsp original en sus partes
            //Esto calcula el VarDiff como 0, 1 ó 2 dependiendo de si concuerda o no
            Constraint modelzero = model.arithm(modelActivities[i],"=", 0);
            Constraint instancezero = model.arithm(instanceActivities[i], "=", 0);
            model.ifOnlyIf(model.or(model.and(modelzero,model.not(instancezero)),model.and(instancezero,model.not(modelzero))),model.arithm(VarCalculation[i],"=",1));

            model.ifOnlyIf(model.and(model.not(modelzero),model.not(instancezero)),model.arithm(ReverseVarCalculation[i],"=",1));
        }

        model.arithm(model.intVar(listOfActivitiesInstance.size()+listOfActivitiesInstance.size()), "-", model.intScaleView(ReverseVarAlignment,2), "=", solutionLength).post();

        model.sum(VarCalculation, "=", VarAligment).post();

        model.setObjective(Model.MINIMIZE, VarAligment);
    }


    public int runSearch(){
        if(!initialized){
            return -1;
        }

        /*model.arithm(modelActivities[0],"=",1).post();
        model.arithm(modelActivities[1],"=",3).post();
        model.arithm(modelActivities[3],"=",2).post();
        model.arithm(modelActivities[4],"=",4).post();
        model.arithm(modelActivities[6],"=",5).post();
        model.arithm(instanceActivities[0],"=",1).post();
        model.arithm(instanceActivities[1],"=",2).post();
        model.arithm(instanceActivities[2],"=",3).post();
        model.arithm(instanceActivities[3],"=",4).post();
        model.arithm(instanceActivities[4],"=",5).post();
        model.arithm(instanceActivities[5],"=",6).post();*/

        Solver solver = model.getSolver();

        if(ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS > 0){
            solver.limitTime(ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS);
        }

        if(ConfigParameters.SOL){
            solver.plugMonitor(new IMonitorSolution() {
                @Override
                public void onSolution() {
                    System.out.println(LocalDateTime.now().toLocalTime().toString() + " Solution found with " + VarAligment + " on " +
                            lpoName + " - " + trace.getAttributes().get("concept:name").toString());
                }
            });
        }

        /*solver.plugMonitor(new IMonitorContradiction() {
            @Override
            public void onContradiction(ContradictionException e) {
                System.out.println("contradiction");
            }
        });*/

        List<IntVar> varList = new ArrayList<>();
        for(int i = 0; i < modelActivities.length; i++){
            varList.add(modelActivities[i]);
            varList.add(instanceActivities[i]);
        }

        for(EventLpo e: lpoFile.getEventLpo()){
            if(e.getIs_star()){
                for(Integer i : modelTranslationNode.keySet()){
                    if(modelTranslationNode.get(i).equals(e)){
                        startEventIndex = i;
                        break;
                    }
                }
                break;
            }
        }

        /*solver.setSearch(intVarSearch(
                // selects the variable of smallest domain size
                new FirstFail(model),
                // selects the smallest domain value (lower bound)
                new IntDomainMax(),
                varList.toArray(new IntVar[varList.size()])
        ));*/


        solver.setSearch(new ReversedSearchStrategy(varList.toArray(new IntVar[varList.size()])));


        int rest = -1;

        Solution solution = new Solution(model);
        while (solver.solve()) {
            rest = VarAligment.getValue();
            solution.record();
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


    /*private int[] calculateModelMinimums(){

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
    }*/

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

    public static int minMissAlignment(PetriNetLpo lpoFile, List<String> logActivities){

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

        return sumDiffMap(repeatedModel,repeatedLog);
    }

    public String debugData(){
        String data = "";
        if(ConfigParameters.SOL){
            for (int i = 0; i < modelActivities.length; i++) {
                data += modelActivities[i] + " <--> " + (instanceActivities[i]) + "\n";
            }
        }
        if(ConfigParameters.PRINTMODEL){
            data += "\n" + model.toString();
        }
        return data;
    }

    public IntVar[] subArray(IntVar[] array, int start, int end){
        IntVar[] newArray = new IntVar[end - start];
        System.arraycopy(array,start,newArray,0,end-start);
        return newArray;
    }



    private class ReversedSearchStrategy extends AbstractStrategy<IntVar> {
        PoolManager<IntDecision> pool = new PoolManager<>();

        List<Integer> nextModelVars;
        List<Integer> executedModelActivities;
        IStateInt nextInstanceVar;
        IStateInt lastVarIndex;

        ReversedSearchStrategy(IntVar[] vars){
            super(vars);

            nextModelVars = new ArrayList<>();
            executedModelActivities = new ArrayList<>();

            nextInstanceVar = model.getEnvironment().makeInt(1);
            lastVarIndex = model.getEnvironment().makeInt(0);
        }


        @Override
        public Decision<IntVar> getDecision() {
            IntDecision d = pool.getE();

            if(nextModelVars.isEmpty() && executedModelActivities.isEmpty()) {
                //nextModelVars.set(startEventIndex-1);
                nextModelVars.add(startEventIndex);
            }

            if (d == null) d = new IntDecision(pool);
            IntVar next = null;
            int value = -1;

            int nextInstanceValue = nextInstanceVar.get();

            do {
                IntVar modelVar = vars[lastVarIndex.get()];
                IntVar instanceVar = vars[lastVarIndex.get() + 1];

                //comprobaciones de que el estado es correcto iniciales
                    //si ya se ejecuto el modelo actualizamos

                if(checkNewInstantiations()){
                    //updateModelVars(modelVar.getValue());

                    //reconstruimos el estado pero no desde cero, las partes que ya hay son buenas porque lo que pasa es que el estado esta atrasado
                    reconstructState();
                } else if(checkBacktracking()){
                    //el estado esta adelantado, no podemos ir en direccion trasera porque es un grafo asi que tenemos que empezar de cero
                    rebuildState();
                }


                if(instanceVar.isInstantiated()){
                    //si ya se ejecuto la instancia actualizamos
                    if(nextInstanceValue <= instanceVar.getValue()){
                        //no se actualiza instancevar porque por cada ejecucion de getdecision solo puede actualizarse una vez
                        nextInstanceValue++;
                    }
                }

                if (!modelVar.isInstantiated()) {
                    if (instanceVar.isInstantiated()) {
                        //intento poner en la del modelo la del log o si no 0, pero hay multiples posibilidades

                        int instanceValue = instanceVar.getValue();
                        next = modelVar;

                        if(instanceVar.getValue() == 0){
                            //si resulta que instance es 0, pues podemos tomar la var de modelo que queramos
                            value = nextModelVars.get(0);
                            updateModelVars(value);

                        } else {
                            int instanceValueConverted = instanceTranslationRepeatedInts.get(instanceValue);

                            //buscamos una variable de modelo que encaje con la que tenemos de instancia
                            boolean found = false;

                            for(Integer i : nextModelVars){
                                if (modelTranslationRepeatedInts.get(i) == instanceValueConverted) {
                                    value = i;
                                    updateModelVars(value);
                                    found = true;
                                    break;
                                }
                            }

                            if(!found){
                                value = 0;
                            }
                        }



                    } else {
                        //busqueda

                        int instanceValue = nextInstanceValue;
                        int instanceValueConverted = instanceTranslationRepeatedInts.get(instanceValue);

                        //fase 1: comprobar si se puede hacer un matching directo modelo - log

                        for(Integer i : nextModelVars){
                            int modelConverted = modelTranslationRepeatedInts.get(i);
                            if (modelConverted == instanceValueConverted) {
                                next = modelVar;
                                value = i;
                                updateModelVars(value);
                                break;
                            }
                        }

                        if(next == null){
                            //fase 2: si eso falla, entonces pasamos al predictivo, empezando primero por el log (es más sencillo)
                            //no podemos hacerlo si es el ultimo elemento, claro
                            if(instanceValue < listOfActivitiesInstance.size()-1){
                                int afterNextInstance = instanceValue+1;
                                int afterNextInstanceConverted = instanceTranslationRepeatedInts.get(afterNextInstance);

                                for(Integer i : nextModelVars){
                                    int modelConverted = modelTranslationRepeatedInts.get(i);
                                    if (modelConverted == afterNextInstanceConverted) {
                                        next = instanceVar;
                                        value = instanceValue;
                                        updateInstanceVar(value);
                                        break;
                                    }
                                }
                            }


                            //ahora modelo
                            if(next == null){

                                modelinit:
                                for(Integer i: nextModelVars){
                                    EventLpo event = modelTranslationNode.get(i);

                                    for(es.idea.pnml.lpo.Pnml.Lpo.LpoArc arc : event.getOutputArcs()) {
                                        int targetActivity = modelTranslationIDs.get(arc.getTarget());
                                        int targetActivityConverted = modelTranslationRepeatedInts.get(targetActivity);
                                        if (targetActivityConverted == instanceValueConverted) {
                                            next = modelVar;
                                            value = i;
                                            updateModelVars(value);
                                            break modelinit;
                                        }
                                    }
                                }
                            }


                            if(next == null){
                                //fase 3: si tod lo demas falla, pues optamos por coger de log o modelo a quien le queden menos variables
                                //(en el modelo la primera disponible), pero no es lo optimo
                                if(listOfActivitiesModel.size() - executedModelActivities.size() > listOfActivitiesInstance.size() - instanceValue + 1){
                                    next = modelVar;
                                    value = nextModelVars.get(0);
                                    updateModelVars(value);
                                } else {
                                    next = instanceVar;
                                    value = instanceValue;
                                    updateInstanceVar(value);
                                }
                            }
                        }
                    }
                } else {
                    if (!instanceVar.isInstantiated()) {
                        //intento poner la del modelo en el log

                        next = instanceVar;

                        int instanceValue = nextInstanceValue;

                        if(modelVar.getValue() == 0 || instanceTranslationRepeatedInts.get(instanceValue).equals(modelTranslationRepeatedInts.get(modelVar.getValue()))){
                            //ponemos el valor disponible de instancia si encaja con el del modelo, o el del modelo es 0
                            value = instanceValue;
                            updateInstanceVar(value);
                        } else {
                            value = 0;
                        }
                    } else {
                        //si entra aqui es que los dos estan instanciados... parece que las restricciones han trabajado cosas
                        //simplemente avanzamos, los otros estados ya estaran actualizados
                        lastVarIndex.add(2);
                    }

                }
            } while (next == null && lastVarIndex.get() < vars.length);

            if(nextInstanceVar.get() != nextInstanceValue){
                updateInstanceVar(nextInstanceValue-1);
            }

            if(next == null){
                return null;
            } else {
                if(next.contains(value)){
                    d.set(next, value, DecisionOperatorFactory.makeIntEq());
                } else {
                    int nextValue = next.nextValue(0);
                    if(nextValue <= next.getUB()){
                        d.set(next, nextValue, DecisionOperatorFactory.makeIntEq());
                    } else {
                        d.set(next, 0, DecisionOperatorFactory.makeIntEq());
                    }

                }

                return d;
            }
        }

        boolean checkNewInstantiations(){
            //sacamos la ultima actividad que fue colocada
            IntVar lastExecuted = null;

            for(int i = lastVarIndex.get(); i >= 0; i-=2){
                if(vars[i].isInstantiated() && vars[i].getValue() != 0){
                    lastExecuted = vars[i];
                }
            }

            if(lastExecuted == null)
                return false;

            return !executedModelActivities.contains(lastExecuted.getValue());
        }

        boolean checkBacktracking(){
            if(executedModelActivities.isEmpty()){
                return false;
            }

            Integer lastExecutedState = executedModelActivities.get(executedModelActivities.size()-1);

            for(int i = lastVarIndex.get(); i >= 0; i-=2){
                if(vars[i].isInstantiatedTo(lastExecutedState)){
                    return false;
                }
            }
            return true;
        }

        void updateInstanceVar(int valTaken){
            if(valTaken != 0){
                nextInstanceVar.set(valTaken+1);
            }

        }

        void updateModelVars(Integer valTaken){
            if(valTaken != 0){
                List<Integer> nexts = new LinkedList<>();
                for(es.idea.pnml.lpo.Pnml.Lpo.LpoArc arc :  modelTranslationNode.get(valTaken).getOutputArcs()) {
                    nexts.add(modelTranslationIDs.get(arc.getTarget()));
                }

                executedModelActivities.add(valTaken);
                nextModelVars.remove(valTaken);

                for(Integer i : nexts){
                    if(!nextModelVars.contains(i)){
                        //hay que comprobar si todos los ya ejecutados estan, por lo de que pueden haber muchas aristas que entraban
                        boolean okay = true;

                        for(String s : modelTranslationNode.get(i).getInputs()){
                            if(!executedModelActivities.contains(modelTranslationIDs.get(s))){
                                okay = false;
                                break;
                            }
                        }
                        if(okay){
                            nextModelVars.add(i);
                        }

                    }
                }

                /*executedModelActivities.set(valTaken-1);
                nextModelVars.clear(valTaken-1);
                for(Integer i : nexts){
                    nextModelVars.set(i-1);
                }*/
            }
        }

        //repairs state from zero
        void rebuildState(){

            nextModelVars = new ArrayList<>();
            executedModelActivities = new ArrayList<>();
            nextModelVars.add(startEventIndex);

            for(int i = 0; i <= vars.length && vars[i].isInstantiated(); i+=2){
                if(vars[i].getValue() != 0){
                    updateModelVars(vars[i].getValue());
                }

            }
        }

        //repairs state, assuming the parts of state filled up are correct and finishes it off
        void reconstructState(){
            //checks up where we left

            int start = -1;

            for(int i = 0; i < vars.length && vars[i].isInstantiated(); i += 2){
                if(vars[i].getValue() == nextModelVars.get(nextModelVars.size())){
                    start = i;
                    break;
                }
            }

            if(start == -1){
                return;
            }

            for(int i = start; i <= vars.length && vars[i].isInstantiated(); i+=2){
                if(vars[i].getValue() != 0){
                    updateModelVars(vars[i].getValue());
                }
            }

        }

    }
}
