package es.us.idea.maximumAlignment;

import PetriNet.LPO.EventLpo;
import PetriNet.LPO.PetriNetLpo;
import com.google.common.collect.Lists;
import es.idea.pnml.Pnml;
import es.us.idea.runs.ConfigParameters;
import ilog.concert.IloException;
import ilog.solver.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

public class CreateCSPAbnormality {
    //graph
    private Graph<LPOGraphActivity, DefaultEdge> g;
    private LPOGraphActivity startNode;
    private Map<String, LPOGraphActivity> graphActivities;

    //activities
    private LinkedHashMap<String, Integer> repeatedActivityModel;
    private LinkedHashMap<String, Integer> repeatedActivityInstance;
    private LinkedHashMap<String, Integer> repeatedFinal;

    private List<String> listOfActivitiesModel;
    private List<String> listOfActivitiesInstance;


    private Map<String, List<LPOGraphActivity>> availableInstancePositions;

    //output variables
    private List<String> solutionModel;
    private List<String> solutionInstance;
    private long lastSolutionTime = 0;
    private boolean finishedInTime = true;

    //for the >= optimization
    //branches that have more than one activity
    private List<List<LPOGraphActivity>> bigBranches;
	//ordered the same as the other list, it contains the good activities from this branch (i.e. don't appear outside the branch)
	private List<List<String>> isolatedActivitiesPerBranch;

    public int minAlignment;
    public int maxAlignment;

    private List<TraceComponent> traces;
    private List<IlcIntVar> hardVars;

    protected IlcSolver solver;

     public CreateCSPAbnormality(PetriNetLpo lpoFile, List<String> listOfActivitiesInstance, int minAlignment, int maxAlignment) throws IloException {

        // Model y Log initializing
        this.listOfActivitiesInstance = listOfActivitiesInstance;

        // Initializing variables
        solver = new IlcSolver();
        repeatedActivityModel = new LinkedHashMap<>();
        listOfActivitiesModel = new ArrayList<>();
        repeatedActivityInstance = new LinkedHashMap<>();
        repeatedFinal = new LinkedHashMap<>();
        bigBranches = new ArrayList<>();

        // Population and processing data
        populateModelVariables(lpoFile);
        populateLogVariables();
        processCombination();

        this.minAlignment = minAlignment;
        if(maxAlignment >= 0){
            this.maxAlignment = maxAlignment-1;
        } else {
            this.maxAlignment = repeatedFinal.values().stream().reduce((i1, i2) -> i1 + i2).get() * 2;
        }

        initializeGraph(lpoFile);
        pruneRepeatedActivities();

        matchModelInstance();
        initializeTraces();
        createMatchingConstraints();
        createPrecedenceConstraints();

    }


    //Calculates instance repetitions
    private void populateLogVariables() {
        for (String s : listOfActivitiesInstance) {
            if (repeatedActivityInstance.keySet().contains(s)) {
                repeatedActivityInstance.put(s, repeatedActivityInstance.get(s) + 1);
            } else {
                repeatedActivityInstance.put(s, 1);
            }
        }
    }

    //Calculates model repetitions and processes model activities
    private void populateModelVariables(PetriNetLpo lpoFile) {
        for (Pnml.Net.Transition t : lpoFile.getTransitions()) {
            if (repeatedActivityModel.keySet().contains(t.getName().getText())) {
                repeatedActivityModel.put(t.getName().getText(), repeatedActivityModel.get(t.getName().getText()) + 1);
            } else {
                repeatedActivityModel.put(t.getName().getText(), 1);
            }
            listOfActivitiesModel.add(t.getId());
        }
    }

    //Calculates common repetitions
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


    //Creates a graph with the LPO with all activities and edges, then calls the marking function
    private void initializeGraph(PetriNetLpo lpo){
        g = new DefaultDirectedGraph<>(DefaultEdge.class);
        graphActivities = new HashMap<>();

        for(EventLpo e: lpo.getEventLpo()){
            LPOGraphActivity node = new LPOGraphActivity(e.getId(), e.getName());
            g.addVertex(node);
            graphActivities.put(e.getId(),node);
            if(e.getIs_star()){
                startNode = node;
            }
        }

        for(es.idea.pnml.lpo.Pnml.Lpo.LpoArc a: lpo.getArcsLpo()){
            g.addEdge(graphActivities.get(a.getSource()),graphActivities.get(a.getTarget()));
        }

        fillGraphPositions();
    }

    //Adds all markings to the graph. These are used to discern what position is each node assigned to. Trace elements can take those positions.
    private void fillGraphPositions(){
        DepthFirstIterator it = new DepthFirstIterator(g, startNode);

        Map<LPOGraphActivity, Integer> parentCounter = new HashMap<>();

        ArrayList list = Lists.newArrayList(it);

        boolean reachedGraphEnd = false;

        List<LPOGraphActivity> currentBranch = new ArrayList<>();

        for(int i = 0 ; i < list.size(); i++){
            LPOGraphActivity currentActivity = (LPOGraphActivity) list.get(i);

            if(i != 0){

                LPOGraphActivity previousActivity = null;
                Set<DefaultEdge> incomingEdges = g.incomingEdgesOf(currentActivity);

                if(!reachedGraphEnd){
                    //since it's depth based we know here it's the previous on the list
                    previousActivity = (LPOGraphActivity) list.get(i-1);
                } else {
                    if(incomingEdges.size() == 1){
                        //if it's only one it's easy
                        previousActivity = g.getEdgeSource(incomingEdges.iterator().next());
                    } else {
                        //no choice but to check all edges to see the previous activity
                        for (DefaultEdge next : incomingEdges) {
                            LPOGraphActivity possiblePrevious = g.getEdgeSource(next);
                            if (graphActivities.containsKey(possiblePrevious.id) && !possiblePrevious.positions.isEmpty()) {
                                previousActivity = possiblePrevious;
                                break;
                            }

                        }
                    }
                }

                assert previousActivity != null;
                List<Integer> previousActivityPositions = previousActivity.positions;
                currentActivity.setPositionsClone(previousActivityPositions);

                //if we have multiple incoming edges then we are closing a branch, remove an AND
                if(incomingEdges.size() > 1){
                    currentActivity.getPositions().remove(currentActivity.getPositions().size()-1);
                    currentActivity.getPositions().remove(currentActivity.getPositions().size()-1);
                }

                //add 1 to last marking
                Integer j = currentActivity.getPositions().get(currentActivity.getPositions().size()-1);
                currentActivity.getPositions().set(currentActivity.getPositions().size()-1,j+1);

                //if the previous element ad multiple output edges we add an AND
                //note that it's possible to both remove an add an and but that's a rare case
                if(g.outgoingEdgesOf(previousActivity).size() > 1){
                    if(parentCounter.containsKey(previousActivity)){
                        Integer number = parentCounter.get(previousActivity);
                        currentActivity.getPositions().add(number);
                        parentCounter.put(previousActivity, number+1);
                    } else {
                        currentActivity.getPositions().add(0);
                        parentCounter.put(previousActivity,1);
                    }
                    currentActivity.getPositions().add(0);
                }

                //part of the optimization: detection of branches
                if(g.outgoingEdgesOf(previousActivity).size() > 1 || incomingEdges.size() > 1){
                    //in that case we are in a new branch
                    if(currentBranch.size() > 1){
                        bigBranches.add(currentBranch);
                    }
                    currentBranch = new ArrayList<>();
                    currentBranch.add(currentActivity);
                } else {
                    currentBranch.add(currentActivity);
                }
                //end of optimization

                if(g.outgoingEdgesOf(currentActivity).size() == 0){
                    reachedGraphEnd = true;
                }
            } else {
                //part of the optimization
                currentBranch.add(currentActivity);
                //end of optimization


                currentActivity.getPositions().add(0);
            }

        }

    }

    //This method is part of the branch optimization
    //Removes activities that are on other branches
    private void pruneRepeatedActivities(){
		isolatedActivitiesPerBranch = new ArrayList<>();
		for(int i = 0; i < bigBranches.size(); i++){
			isolatedActivitiesPerBranch.add(new ArrayList<>());
		}
		
		for(String s : repeatedActivityModel.keySet()){
			for(List<LPOGraphActivity> branch : bigBranches){
                long numberOf = branch.stream().filter((p) -> p.basicName.equals(s)).count();
				if(numberOf >= 1L){
					if(numberOf == repeatedActivityModel.get(s)){
						int index = bigBranches.indexOf(branch);
						isolatedActivitiesPerBranch.get(index).add(s);
					}
					break;
				}
			}
		}
    }

    private void matchModelInstance(){
        //There are 3 possibilities in terms of matching
        //a) 1 or more options per activity
        //b) 0 options, empty list, which means this trace activity is not present in model
        //c) we don't even detect an activity present in model, which means it's not on the trace, these are noted which model size
        availableInstancePositions = new HashMap<>();
        for(String s : listOfActivitiesInstance){
            availableInstancePositions.put(s, new ArrayList<>());
            for(LPOGraphActivity a : graphActivities.values()){
                if(a.basicName.equals(s)){
                    availableInstancePositions.get(s).add(a);
                }
            }
        }
    }

    private void initializeTraces() throws IloException{

        traces = new ArrayList<>();

        for(int i = 0; i < listOfActivitiesInstance.size(); i++){
            String s = listOfActivitiesInstance.get(i);
            List<LPOGraphActivity> availablePositions = availableInstancePositions.get(s);
            if(availablePositions.isEmpty()){
                //case b)
                //Just making a dummy tracecomp
                traces.add(new TraceComponent(s, s+i, solver, 1, 0, 0));
            } else {
                //case a)
                //taking all possible values

                List<List<Integer>> values = new ArrayList<>();
                boolean anyone = true;

                int position = 0;

                while (anyone){
                    List<Integer> onPosition = new ArrayList<>();
                    anyone = false;
                    for(LPOGraphActivity ga : availablePositions){
                        if(ga.getPositions().size() > position){
                            anyone = true;
                            onPosition.add(ga.getPositions().get(position));
                        }
                    }
                    if(anyone){
                        position++;
                        values.add(onPosition);
                    }

                }

                traces.add(new TraceComponent(s,s+i, solver, values));

            }
        }

    }

    private void createMatchingConstraints() {
        for(TraceComponent trace: traces){
            List<LPOGraphActivity> availablePositions = availableInstancePositions.get(trace.id);
            if(availablePositions.size() == 0){
                //case b) it's always abnormal
                solver.add(solver.eq(trace.AB, 1));
            } else if (availablePositions.size() == 1){
                //case a) but a simple one since only one possibility
                //just throw in a bunch of ands
                IlcConstraint c = solver.trueConstraint();
                List<Integer> positions = availablePositions.get(0).getPositions();
                for(int i = 0; i < positions.size(); i++){
                    c = solver.and(c, solver.eq(trace.pos[i], positions.get(i)));
                }
                solver.add(solver.imply(solver.eq(trace.AB, 0), c));

            } else {

                //we don't have an algorithm to minimize the number of expressions, this is improvable
                //but, we do detect elements that are always the same number 100% of the times
                int max = availablePositions.stream().max(Comparator.comparingInt(a -> a.getPositions().size())).get().positions.size();

                IlcConstraint finalC = solver.trueConstraint();

                List<Integer> completePositions = new ArrayList<>();
                for(int i = 0; i < max; i++){
                    boolean coincide = true;
                    int value = -1;
                    for(LPOGraphActivity a : availablePositions){
                        if(value == -1){
                            value = a.getPositions().get(i);
                        } else {
                            if(value != a.getPositions().get(i)){
                                coincide = false;
                                break;
                            }
                        }
                    }
                    if(coincide){
                        finalC = solver.and(finalC, solver.eq(trace.pos[i],value));
                        completePositions.add(i);
                    }
                }


                //for the rest we just slap all possibilities with OR
                IlcConstraint c = solver.falseConstraint();

                for(LPOGraphActivity a : availablePositions){
                    IlcConstraint c2 = solver.trueConstraint();
                    for(int i = 0; i < a.positions.size(); i++){
                        if(!completePositions.contains(i)){
                            c2 = solver.and(c2, solver.eq(trace.pos[i], a.getPositions().get(i)));
                        }
                    }
                    c = solver.xor(c, c2);
                }

                solver.add(solver.imply(solver.eq(trace.AB, 0), solver.and(finalC, c)));

            }
            //Limits on number of abnormals with 0
            for(String activityName : repeatedFinal.keySet()){
                int modelRepeats = repeatedActivityModel.containsKey(activityName) ? repeatedActivityModel.get(activityName) : 0;

                if(repeatedActivityInstance.containsKey(activityName) && (repeatedActivityInstance.get(activityName) > modelRepeats)){

                    //we just look all possibilities
                    List<IlcIntVar> vars = traces.stream().filter(p -> p.id.equals(activityName)).map(p -> p.AB).collect(Collectors.toList());

                    solver.add(solver.ge(solver.sum(vars.toArray(new IlcIntVar[vars.size()])), repeatedActivityInstance.get(activityName) - modelRepeats));
                }
            }
        }
    }

    private void createPrecedenceConstraints(){
        for(int i = 0; i < traces.size(); i++){
            for(int j = i+1; j < traces.size(); j++){
                solver.add(solver.imply(solver.and(solver.eq(traces.get(i).AB, 0), solver.eq(traces.get(j).AB, 0)), menor(solver, traces.get(i), traces.get(j))));
            }
        }

        //improvement, all with all activities with same name and in the same branch must have precedence with each other even if abnormal
        List<List<TraceComponent>> groups = new ArrayList<>();

        for(List<String> a : isolatedActivitiesPerBranch){
            for(String activity : a){
                List<TraceComponent> group = new ArrayList<>();
                for(TraceComponent trace : traces){
                    if(activity.equals(trace.id)){
                        group.add(trace);
                    }
                }
                groups.add(group);
            }
        }

        for(List<TraceComponent> group : groups){
            for(int i = 0; i < group.size(); i++){
                for(int j = i+1; j < group.size(); j++){
                    solver.add(menorIgual(solver, group.get(i), group.get(j)));
                }
            }
        }

    }

    //this starts off a search
    public int runSearch(int timeout) throws IloException{
        IlcIntVar[] ABList = new IlcIntVar[traces.size()];

        //this is our true search strategy
        //we priorize by position array size (always instantiate first the outside! fewer combinations)
        //if the array size is equal then instantiate first the ones with less repetitions, since it has fewer combinations as well
        //we try to prune as early as possible
        List<TraceComponent> orderedByReps = traces.stream().sorted((o1, o2) -> {
            int size = Integer.compare(o1.pos.length,o2.pos.length);
            if(size != 0)
                return size;

            return repeatedActivityInstance.getOrDefault(o1.id, Integer.MAX_VALUE).compareTo(repeatedActivityInstance.getOrDefault(o2.id, Integer.MAX_VALUE));
        }).collect(Collectors.toList());

        hardVars = new ArrayList<>();
        for(TraceComponent c : orderedByReps){
            hardVars.addAll(Arrays.asList(c.pos));
        }


        //calculating missalignment
        for(int i = 0; i< traces.size(); i++){
            ABList[i] = orderedByReps.get(i).AB;
        }
        IlcIntVar sumRefE = solver.intVar(0, ABList.length);//ABList.size() - 7
        solver.add(solver.eq(sumRefE, solver.sum(ABList)));
        //the 1 means do not search for all solutions, just one is enough as long as it's minimal
        solver.add(solver.minimize(sumRefE, 1)); // 1 0
        IlcIntVar missAlignment = solver.intVar(minAlignment, maxAlignment);

        //missalignment is the number of abnormals in instance + number of abnormals in model, but we know the number of abnormals in model is related to the sizes and the abnormals in instance as well
        solver.add(solver.eq(missAlignment, solver.sum(solver.prod(sumRefE,2),listOfActivitiesModel.size()-listOfActivitiesInstance.size())));

        //this makes the search strategy work
        class OrderHeuristic extends IlcIntChooseVariableHeuristic {
            public int evaluate(final IlcIntExpr exp) {
                return hardVars.indexOf(exp);
            }
        }

        if(timeout > 0){
            solver.setPropagationTimeLimit(timeout);
        }

        //force to instantiate all positions, it's cheap since the abnormals do most of the work anyway, and prevents reconstruction errors
        if(ConfigParameters.GENERATE){
            solver.newSearch(solver.and(solver.generate(ABList, solver.selectIntMinValue()), solver.generate(hardVars.toArray(new IlcIntVar[hardVars.size()]), new OrderHeuristic(), solver.selectIntMinValue())));

        } else {
            solver.newSearch(solver.generate(ABList, solver.selectIntMinValue()));
        }


        if(ConfigParameters.PRINTMODEL){
            solver.printModel();
        }

        int alignment = -1;

        //we store solutions for the reconstruction
        IlcSolution solution = solver.solution();
        for(TraceComponent t : traces){
            solution.add(t.AB);
            solution.add(t.pos);
        }

        long curr = System.currentTimeMillis();

        while (solver.next()) {
            alignment = missAlignment.getDomainValue();
            if(ConfigParameters.TRACE){
                System.out.println("********************************************");
                System.out.println("Desalineamiento: " + missAlignment.getDomainValue());
                printSolution();
                System.out.println("********************************************");
            }
            //stores the values on the solution, this is important because normally they get unbounded after the solver.next loop
            lastSolutionTime = System.currentTimeMillis() - curr;
            solution.store();
        }

        if(solver.getElapsedTime()*1000 >= timeout){
            finishedInTime = false;
        }

        solver.endSearch();

        if(alignment != -1){
            reconstructExecution(solution);
        }
        return alignment;
    }

    //This method fills up two lists with a text-based reconstruction.
    //From all the possible correct executions we print out one. We deem it enough.
    private void reconstructExecution(IlcSolution s){
        solutionInstance = new ArrayList<>();
        solutionModel = new ArrayList<>();

        List<LPOGraphActivity> nextModelActivities = new ArrayList<>();
        List<LPOGraphActivity> executedActivities = new ArrayList<>();
        int nextInstanceIndex = 0;


        nextModelActivities.add(startNode);


        while(nextInstanceIndex < traces.size()){
            TraceComponent nextInstance = traces.get(nextInstanceIndex);

            if(s.getValue(nextInstance.AB) == 0){
                List<Integer> modelPos = Arrays.stream(nextInstance.pos).map(s::getValue).collect(Collectors.toList());

                LPOGraphActivity modelActivityWithPosition = graphActivities.values().stream().filter((p)-> p.positions.equals(modelPos)).collect(Collectors.toList()).get(0);

                //if we can't execute the activity in model then there are activites that are executed only in model and we have to detect them with the graph
                if(!nextModelActivities.contains(modelActivityWithPosition)){
                    //we fill out a list of activities we keep detecting
                    List<LPOGraphActivity> activitiesToAddReverseOrdered = new ArrayList<>();
                    Set<DefaultEdge> previousActivities = g.incomingEdgesOf(modelActivityWithPosition);
                    while (!previousActivities.isEmpty()){
                        //and we keep going backwards until we already executed all activities
                        Set<DefaultEdge> newPrevious = new HashSet<>();
                        for(DefaultEdge e : previousActivities){
                            LPOGraphActivity a = g.getEdgeSource(e);
                            if(!executedActivities.contains(a)){
                                activitiesToAddReverseOrdered.add(a);
                                newPrevious.addAll(g.incomingEdgesOf(a));
                            }
                        }
                        previousActivities = newPrevious;
                    }
                    //we execute them in reverse order
                    for(int i = activitiesToAddReverseOrdered.size() - 1; i >=0; i--){
                        LPOGraphActivity a = activitiesToAddReverseOrdered.get(i);

                        solutionModel.add(a.basicName);
                        solutionInstance.add(" ");

                        //update arrays
                        executedActivities.add(a);
                        nextModelActivities.remove(a);
                        for(DefaultEdge e : g.outgoingEdgesOf(a)){
                            LPOGraphActivity target = g.getEdgeTarget(e);
                            if(g.incomingEdgesOf(target).size() == 1 || executedActivities.containsAll(g.incomingEdgesOf(target))){
                                nextModelActivities.add(target);
                            }
                        }
                    }

                }

                //now we are sure we can execute both
                solutionModel.add(modelActivityWithPosition.basicName);
                solutionInstance.add(nextInstance.id);

                //update arrays
                executedActivities.add(modelActivityWithPosition);
                nextModelActivities.remove(modelActivityWithPosition);
                for(DefaultEdge e : g.outgoingEdgesOf(modelActivityWithPosition)){
                    LPOGraphActivity target = g.getEdgeTarget(e);
                    if(g.incomingEdgesOf(target).size() == 1 || executedActivities.containsAll(g.incomingEdgesOf(target))){
                        nextModelActivities.add(target);
                    }
                }


            } else {
                //if abnormal just advance in log
                solutionModel.add(" ");
                solutionInstance.add(nextInstance.id);
            }
            nextInstanceIndex++;
        }
    }


    //Generates strict precedence constraints
    private IlcConstraint menor(IlcSolver solver, TraceComponent c1, TraceComponent c2){
        int c1Size = c1.pos.length;
        int c2Size = c2.pos.length;

        int size = Integer.min(c1Size, c2Size);

        IlcConstraint prec = solver.falseConstraint();

        for(int i = 0; i < size; i=i+2){
            IlcConstraint innerAnd = solver.trueConstraint();
            if(i == 0){
                innerAnd = solver.and(innerAnd, solver.lt(c1.pos[0], c2.pos[0]));
            } else {
                for(int pos = 0; pos < i; pos++){
                    if(pos == i-1){
                        innerAnd = solver.and(innerAnd, solver.or(solver.neq(c1.pos[pos], c2.pos[pos]),solver.lt(c1.pos[pos+1], c2.pos[pos+1])));
                    } else {
                        innerAnd = solver.and(innerAnd, solver.eq(c1.pos[pos], c2.pos[pos]));
                    }
                }
            }

            prec = solver.or(prec, innerAnd);
        }
        return prec;
    }

    //Generates precedence constrants, or equality in case of multiple positions
    private IlcConstraint menorIgual(IlcSolver solver, TraceComponent c1, TraceComponent c2){
        int c1Size = c1.pos.length;
        int c2Size = c2.pos.length;

        int size = Integer.min(c1Size, c2Size);

        IlcConstraint prec = solver.falseConstraint();

        for(int i = 0; i < size; i=i+2){
            IlcConstraint innerAnd = solver.trueConstraint();
            if(i == 0){
                innerAnd = solver.and(innerAnd, solver.lt(c1.pos[0], c2.pos[0]));
            } else {
                for(int pos = 0; pos < i; pos++){
                    if(pos == i-1){
                        innerAnd = solver.and(innerAnd, solver.or(solver.neq(c1.pos[pos], c2.pos[pos]),solver.le(c1.pos[pos+1], c2.pos[pos+1])));
                    } else {
                        innerAnd = solver.and(innerAnd, solver.eq(c1.pos[pos], c2.pos[pos]));
                    }
                }
            }

            prec = solver.or(prec, innerAnd);
        }
        return prec;
    }

    private void printSolution(){
        String s = "";
        for(TraceComponent t: traces){
            s+= t + "\n";
        }
        System.out.println(s);
    }

    public List<String> getModelSolution(){
         return solutionModel;
    }

    public List<String> getInstanceSolution(){
         return solutionInstance;
    }

    public long getLastSolutionTime() {
        return lastSolutionTime;
    }

    public boolean isFinishedInTime() {
        return finishedInTime;
    }

    private class LPOGraphActivity {
        String id;
        String basicName;
        List<Integer> positions;

        public LPOGraphActivity(String id, String basicName){
            this.id = id;
            this.basicName = basicName;
            positions = new ArrayList<>();
        }

        public List<Integer> getPositions(){
            return positions;
        }

        public void setPositionsClone(List<Integer> positions){
            this.positions.addAll(positions);
        }

        public String toString(){
            return id + " / " + basicName + ": " + positions.toString();
        }
    }

    //Utility method to calculate minimum missalignment quickly
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
}
