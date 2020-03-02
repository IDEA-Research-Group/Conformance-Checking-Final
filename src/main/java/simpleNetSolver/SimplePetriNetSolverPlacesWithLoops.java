package simpleNetSolver;
import java.util.*;

import PetriNet.PlacePetriNet;
import PetriNet.TransitionPetriNet;
import ilog.concert.IloCopyManager;
import ilog.concert.IloCopyable;
import ilog.concert.IloException;
import ilog.concert.IloRuntimeException;
import ilog.concert.IloCopyManager.Check;
import ilog.concert.model.IlcCollectionDomain;
import ilog.solver.*;

public class SimplePetriNetSolverPlacesWithLoops {
	
	protected Integer nada;
	
    protected IlcSolver solver;
    protected IlcSolver solverTimes;

    protected IlcIntVar[] VarModel ;
    protected IlcIntVar[] VarLog;
    protected IlcIntVar[] VarDifference;
    protected IlcIntVar VarAlignment;
    
    protected List<TransitionPetriNet> model;
    protected List<TransitionPetriNet> modelConLoop;

    protected List<PlacePetriNet> places;
    protected List<String> logs;
    protected List<String> duplicatedInLog;  
    protected List<List<String>> duplicatedListList;  

    protected List<String> activityInlogNotInModel;
    protected Integer modelSizeWithoutNotInActivities;
    
    protected List<List<Boolean>> matrixForLoop;
    protected List<List<Integer>> indexInVarMatrixLoops;

    protected List<String> logSinModificar;
    
    protected IlcIntExpr[] numExecutions;
    protected List<Integer> numberOfAiLoops;
    protected List<Integer> transitionInLoop;
    
    
    
    public SimplePetriNetSolverPlacesWithLoops(List<TransitionPetriNet> modelExt, List<PlacePetriNet> placesExt, List<String> logsExt) throws IloException{
    	solver=new IlcSolver();
    	solverTimes=new IlcSolver();
        this.places=placesExt;
    	this.model=modelExt; 
    	this.modelConLoop=new ArrayList<TransitionPetriNet>();
    	
    	numberOfAiLoops=new ArrayList<Integer>();
    	transitionInLoop=new ArrayList<Integer>();

    	modelSizeWithoutNotInActivities=modelExt.size();
    	indexInVarMatrixLoops= new ArrayList<List<Integer>>();


    	
    	
    	
        //Change Duplicated Logs
        logs=new ArrayList<String>();
        duplicatedInLog=new ArrayList<String>();
        activityInlogNotInModel=new ArrayList<String>();
        duplicatedListList= new ArrayList<List<String>>();
        for (int i = 0; i < modelExt.size(); i++) {
			duplicatedListList.add(new ArrayList<String>());
		}
        
        
        for (String logExt : logsExt) {
        	//if the transition of the log is in the model
        	if(activityInModel(logExt)){
    			if(!logs.contains(logExt)){
    				logs.add(logExt);
    			}
    			else{
    				//inicializamos la lista de lista si no existe
    				Integer indexAux= indexTransitionInVarModel(logExt);
    		        System.out.println("SIZE"+duplicatedListList.size());
    		        System.out.println("indexAux"+indexAux);
    		        System.out.println("elemento "+logExt);
    		        System.out.println("model "+model);

    				if(duplicatedListList.get(indexAux).isEmpty())
    					duplicatedListList.get(indexAux).add(logExt);
    				
    				//concat + for each duplicated transaction in log
    				String logAux=logExt;
    				logExt+="'";
    				String namePrime="'";
    				while(logs.contains(logExt)){
    					logExt+="'";
    					namePrime+="'";
    				}
    				logs.add(logExt);
    		        duplicatedInLog.add(logExt);
    		        TransitionPetriNet t=new TransitionPetriNet(model.get(indexTransitionInVarModel(logAux)),logExt,namePrime);
    				model.add(t);
    				activityInlogNotInModel.add(logExt);
    				
    				//adding to the list of lists the renamed activity
					duplicatedListList.get(indexAux).add(logExt);
    			}
        	}
	
        	//if the transition of the log is NOT in the model
        	//add this transaction to de model without in/out-puts
			else{
				activityInlogNotInModel.add(logExt);
				model.add(new TransitionPetriNet(logExt));
			}
		}

        

        
        
        //MATRIX LOOP
    	matrixForLoop= new ArrayList<List<Boolean>>();
        for (int i = 0; i < model.size(); i++) {
        	List<Boolean> r= new ArrayList<Boolean>();
        	for(int j = 0; j < model.size(); j++){
        		if(j==i)
        			r.add(true);
        		else
        			r.add(false);
        	}
        	matrixForLoop.add(r);
		}       
        
        logSinModificar=logsExt;
    }
    
    
    
    ////////////////////////
    /// Auxiliar Methods ///
    ////////////////////////
    
    
    //return false if the log Transition isn't be in the model
    public boolean activityInModel(String log){
    	boolean res=false;
    	for (TransitionPetriNet transition : model) {
    		if(transition.getId().equals(log)){
    			res=true;
    			break;
    		}
		}
    	return res;
    }
    
    public boolean activityInLog(String activityId){
    	return logs.contains(activityId);
    }
    
    
    public Integer indexOfTransitionInModel(TransitionPetriNet trans){
    	return model.indexOf(trans);
    }
    
    
    //index of transaction in VarModel for a transaction Id. For duplicated transactions too
    public Integer indexTransitionInVarModel(String log){
    	Integer res=null;
    	for (int i = 0; i < model.size(); i++) {
			if(model.get(i).getId().equals(log)){
				res=i;
			}
		}
    	return res;
    }
    
    
    //index of LIST of transaction in VarModel for a transaction Id. For duplicated transactions too
    public List<Integer> indexOfActivitiesListInVarModel(List<String> logs){
    	List<Integer> res=new ArrayList<Integer>();
    	for (String log : logs) {
	    	res.add(indexTransitionInVarModel(log));
    	}
    	return res;
    }
   
    public List<Integer> indexOfActivitiesListInVarModel2(List<String> logs){
    	List<Integer> res=new ArrayList<Integer>();
    	for (String log : logs) {
	    	res.add(indexTransitionInVarModel2(log));
    	}
    	return res;
    }
    public Integer indexTransitionInVarModel2(String log){
    	Integer res=null;
    	for (int i = 0; i < modelConLoop.size(); i++) {
			if(modelConLoop.get(i).getId().equals(log)){
				res=i;
			}

		}
		if(res==null){
    	System.out.println("error en "+ log);
		}

    	return res;
    }
    
    
    public PlacePetriNet placeForString(String placeId){
    	PlacePetriNet res=null;
    	for (PlacePetriNet aux : places) {
			if(aux.getId().equals(placeId)){
				res=aux;
			}
		}
    	return res;
    }
    
    
    
    ///////////////////
    ////   MATRIX  ////
    ///////////////////
    
    public void makeMatrix(){
    	for (PlacePetriNet place : places) {

			for (String inp : place.getInputs()) {
				Integer indexIn=indexTransitionInVarModel(inp);

				for (String outp : place.getOutputs()) {
					Integer indexOut=indexTransitionInVarModel(outp);

					matrixForLoop.get(indexIn).set(indexOut, true);
				}
				
			}
		}   	
    	
    	for (int i = 0; i < matrixForLoop.size(); i++) {
        	for (int j = 0; j < matrixForLoop.size(); j++) {
        		if(j!=i){
        			if(matrixForLoop.get(i).get(j)){
        				List<Boolean> aux=matrixForLoop.get(j);
        				for (int z = 0; z < matrixForLoop.size(); z++) {
							if(aux.get(z)){
								matrixForLoop.get(i).set(z,true);
							}
						}
        			}
        		}	
        	}
		}

    	for (int i = 0; i < matrixForLoop.size(); i++) {
    		List<Integer> aux=new ArrayList<Integer>();
    		aux.add(i);
        	for (int j = 0; j < matrixForLoop.size(); j++) {
        		if(j>i){ //!= anteri9ormente, probando >
        			if(matrixForLoop.get(i).equals(matrixForLoop.get(j))){
        				aux.add(j);
        			}
        		}
        	}
        	if(aux.size()>1){
//        		boolean containsAux=false;
//            	for (int z = 0; z < indexInVarMatrixLoops.size(); z++) {
//            		containsAux=containsAux||indexInVarMatrixLoops.get(z).containsAll(aux);
//            		if (containsAux){
//            			break;
//            		}
//            	}
//            	if(!containsAux){
            		indexInVarMatrixLoops.add(aux);
//            	}
            		for(Integer indexForBucleTransaction : aux){
            			if(!transitionInLoop.contains(indexForBucleTransaction)){
            				transitionInLoop.add(indexForBucleTransaction);
            			}
            		}
            		
        	}
    	}

    	System.out.println("Loops: "+indexInVarMatrixLoops);
    	System.out.println("Transition in loops: "+transitionInLoop);
    	//System.out.println(matrixForLoop);
    	
    	
    }
    
    //CASE ANALYSIS
    public void caseAnalysis() throws IloException{
    	int sizeWithoutDuplicated = model.size()-activityInlogNotInModel.size();
		// calcular numero de veces en el log que se repite
		List<Integer> done=new ArrayList<Integer>();
		for (int i = 0; i < model.size(); i++) {
			done.add(0);
		}
		
		System.out.println("log sin modificar "+logSinModificar);
		for (String ai : logSinModificar) {
			Integer indexOfAi=indexTransitionInVarModel(ai);
			System.out.println(done+" "+indexOfAi+"  ai "+ai);
			done.set(indexOfAi, done.get(indexOfAi)+1);
		}
		System.out.println(done);

		
		//numberIfAi	
    	/*For every activity ai:
NumberOfai > count of ai in the case
Minimize(sum(Number of every activity))*/
    	numExecutions = new IlcIntVar[sizeWithoutDuplicated];

    	for(int i=0;i<sizeWithoutDuplicated;i++){
    		numExecutions[i] = solverTimes.intVar(0, logs.size(), model.get(i).getName()+"-numExecutions");
    	}
    	for(int i=0;i<sizeWithoutDuplicated;i++){
    		solverTimes.add(solverTimes.ge(numExecutions[i], done.get(i)));
    	}

    	
    	
//		 //model
		for (PlacePetriNet place : places) {
		    List<Integer> indexsOut=indexOfActivitiesListInVarModel(place.getOutputs());
	    	List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());

			if (place.getOutputs().size()==1 && place.getInputs().size()==1){
				//a=b
				solverTimes.add(solverTimes.eq(numExecutions[indexsIn.get(0)],numExecutions[indexsOut.get(0)]));
			}
			else if(place.getOutputs().size()==0 || place.getInputs().size()==0){
				//nothing first and last places
			}
			else{
				//a+b=c+d
				//in


				IlcIntExpr suma=solver.sum(numExecutions[indexsIn.get(0)],0);
		    	for(int i=1;i<indexsIn.size();i++){
		    		suma=solverTimes.sum(suma,numExecutions[indexsIn.get(i)]);
				}
				
				IlcIntExpr suma2=solver.sum(0,numExecutions[indexsOut.get(0)]);
				for(int i=1;i<indexsOut.size();i++){
				    suma2=solverTimes.sum(suma2,numExecutions[indexsOut.get(i)]);
				}
				
				solverTimes.add(solverTimes.eq(suma, suma2));

			}
		}
		
    	for(int i=0;i<sizeWithoutDuplicated;i++){
    		solverTimes.add(solverTimes.minimize(numExecutions[i]));
    	}

    	System.out.println("SEARCH TIMES--");	

    	solverTimes.newSearch();

        while(solverTimes.next()){

	        for(int i=0;i<sizeWithoutDuplicated;i++){
	        	System.out.println((numExecutions[i]));	
	        	numberOfAiLoops.add(numExecutions[i].getDomainMin());
	        }	        
        
        }
    	System.out.println("-------");	

        
//        solverTimes.printModel();
        
        solverTimes.endSearch();
    	System.out.println("NumberOfX");

    	System.out.println(numberOfAiLoops);
    }
    
    
    
    
    
    //LOOP UNROLLING
    public void loopUnrolling() throws IloException{
    	//transitions que no estan en loop

    	for (int i = 0; i < model.size()-duplicatedInLog.size(); i++) {
			//no es parte de un loop
    		if(!transitionInLoop.contains(i)){
				modelConLoop.add(model.get(i));
			}
		}

    	//transitions not in model
//    	for (String notInModel : activityInlogNotInModel) {
//			System.out.println(notInModel);
//
//			modelConLoop.add(new TransitionPetriNet(notInModel));
//		}
    	

    	//transitions in loops
    	for(List<Integer> aux : indexInVarMatrixLoops){
    		Integer max=0;
    		for(Integer auxIndex:aux){
    			Integer ultimo=numberOfAiLoops.get(auxIndex);
    			if(ultimo>max){
    				max=ultimo;
    			}
    		}
    		
    		String prime="";
    		for (int i = 0; i < max; i++) {
				
        		for(Integer auxIndex:aux){
        			if(i==0){

        				modelConLoop.add(model.get(auxIndex));
        			}
        			else{

        				TransitionPetriNet tAux=model.get(auxIndex);
        				
        				
        				modelConLoop.add(new TransitionPetriNet(tAux,tAux.getId()+prime,prime));
        			}
        			
        		}

    			prime=prime+"'";
			}

    	}
    	
    	System.out.println("Nuevo modelo");
    	for(TransitionPetriNet tp:modelConLoop){
        	System.out.print(tp.getId()+",");

    	}
    	System.out.println();

        VarModel = new IlcIntVar[modelConLoop.size()];
        VarLog = new IlcIntVar[modelConLoop.size()];
        VarDifference = new IlcIntVar[modelConLoop.size()];
        
        int n = 5;
    	   	
        VarAlignment = solver.intVar(0, modelConLoop.size()+n, " Alingment");

        for(int i=0;i<modelConLoop.size();i++){
        	VarModel[i] = solver.intVar(0, modelConLoop.size()+n, modelConLoop.get(i).getName()+" Model");
        	VarLog[i] = solver.intVar(0, modelConLoop.size()+n, modelConLoop.get(i).getName()+" Log");
        	VarDifference[i] = solver.intVar(0, 1, modelConLoop.get(i).getName()+" Difference..");
        }
    }
    
    
  
    
    ///COP PARA LOOPS
    public void case1(PlacePetriNet place, int N) throws IloException{
//		System.out.println("CASE1");
//		System.out.println(place);
    	
    	String prime="";
    	for (int e = 0; e < N; e++) {
    		
    		List<Integer> indexsIn=new ArrayList<Integer>();
    		List<Integer> indexsOut=new ArrayList<Integer>();

    		
    		
    		if(e==0){
    			indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
        		indexsOut=indexOfActivitiesListInVarModel(place.getOutputs());
        		System.out.println("e==0");
        		System.out.println(indexsIn);
        		System.out.println(indexsOut);

    		}
    		
    		else{
    			List<String> auxIn=new ArrayList<String>();
    			List<String> auxOut=new ArrayList<String>();
    			for(String t: place.getInputs()){
    				auxIn.add(t+prime);
    			}
    			for(String t: place.getOutputs()){
    				auxOut.add(t+prime);
    			}
    			indexsIn=indexOfActivitiesListInVarModel(auxIn);
        		indexsOut=indexOfActivitiesListInVarModel(auxOut);
        		
        		System.out.println("e == " +e);
        		System.out.println(indexsIn);
        		System.out.println(indexsOut);
    		}

    		
        	for(int i=0;i<indexsIn.size();i++){
            	for(int j=0;j<indexsOut.size();j++){
            		solver.add(solver.imply(solver.neq(VarModel[indexsOut.get(j)],0),
            								solver.ge(VarModel[indexsOut.get(j)], VarModel[indexsOut.get(i)]) ));
            	}
        	}
        	
        	
        	
        	
    	//(Var-Model[Input1:e]!=0 + : : : + Var-Model[Inputn:e]!=0) <= 1 AND
    	//(Var-Model[Output1:e]!=0 + : : : + Var-Model[Outputm:e]!=0) <= 1
        	IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
        	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
        	
        	IlcConstraint[] reifConstOut = new IlcConstraint[indexsOut.size()];
        	IlcIntVar[] reifVarModelOut = new IlcIntVar[indexsOut.size()];
        	
        	
        	
        	for(int i=0;i<indexsIn.size();i++){
        		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIncase1");
        	}
        	
        	for(int i=0;i<indexsOut.size();i++){
        		reifVarModelOut[i] = solver.intVar(0, 1, indexsOut.get(i)+"reifModelOutcase1");
        	}
        	
        	for(int i=0; i< indexsIn.size();i++){
        		reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
            	
            	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
        	}
        	solver.add(solver.le(solver.sum(reifVarModelIn),1));
        	
        	
        	for(int i=0; i< indexsOut.size();i++){
        		reifConstOut[i] = solver.neq(VarModel[indexsOut.get(i)], 0);
            	
            	solver.add(solver.eq(reifConstOut[i], reifVarModelOut[i]));
        	}
        	solver.add(solver.le(solver.sum(reifVarModelOut),1));
        	
        	
        	
        	
        //(Var-Model[Input1:e]!=0 + : : : + Var-Model[Inputn:e]!=0) ==(Var-Model[Output1:e]!=0 + : : : + Var-Model[Outputm:e]!=0)
	
        	solver.add(solver.eq(solver.sum(reifVarModelOut),solver.sum(reifVarModelIn)));
        	
        		
        	prime=prime+"'"; // END FOR
		}
    }
    
    
    
    
    public void case2(PlacePetriNet place, int N) throws IloException{
//		System.out.println("CASE2");
//		System.out.println(place);
//		System.out.println(transitionInLoop+" transition in loop");

		List<Integer> indexsIn=indexOfActivitiesListInVarModel2(place.getInputs());
		List<Integer> indexsOut=indexOfActivitiesListInVarModel2(place.getOutputs());
		
		//elementos de 1..p y 1..q
		List<Integer> indexInputsNotInLoop= new ArrayList<>();
		List<Integer> indexOutputsNotInLoop=new ArrayList<>();
		
		//elementos de p..N y q..N
		List<Integer> indexInputsInLoop=new ArrayList<>();
		List<Integer> indexOutputsInLoop=new ArrayList<>();
		
		//aux es el indice en el modelo nuevo, aux2 en el modelo viejo, con el que se hace la matriz
		for (Integer aux : indexsIn) {
			Integer aux2=indexTransitionInVarModel2(model.get(aux).getId());
			if(transitionInLoop.contains(aux2)){
				indexInputsInLoop.add(aux);
			}
			else{
				indexInputsNotInLoop.add(aux);
			}
		}
		for (Integer aux : indexsOut) {
			Integer aux2=indexTransitionInVarModel2(model.get(aux).getId());

			if(transitionInLoop.contains(aux2)){
				indexOutputsInLoop.add(aux);
			}
			else{
				indexOutputsNotInLoop.add(aux);
			}
		}
		System.out.println(indexInputsInLoop+" InpIn");
		System.out.println(indexInputsNotInLoop+" InpNOT");
		System.out.println(indexOutputsNotInLoop+" OutNOT");
		System.out.println(indexOutputsInLoop+" OutIn");

		
		//apartado A
		//parte no en loop
    	//only one nput can be true
    	
    	IlcConstraint[] reifConstIn = new IlcConstraint[indexInputsNotInLoop.size()];
    	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexInputsNotInLoop.size()];
    	
    	IlcConstraint[] reifConstOut = new IlcConstraint[indexOutputsNotInLoop.size()];
    	IlcIntVar[] reifVarModelOut = new IlcIntVar[indexOutputsNotInLoop.size()];
    	
    	//LOOP VARIABLES e,q,p
    	IlcConstraint[] reifConstInLoop = new IlcConstraint[indexInputsInLoop.size()];
    	IlcIntVar[] reifVarModelInLoop = new IlcIntVar[indexInputsInLoop.size()];
    	
    	IlcConstraint[] reifConstOutLoop = new IlcConstraint[indexOutputsInLoop.size()];
    	IlcIntVar[] reifVarModelOutLoop = new IlcIntVar[indexOutputsInLoop.size()];
    	
    	//Variables
    	
    	
    	for(int i=0;i<indexInputsNotInLoop.size();i++){
    		reifVarModelIn[i] = solver.intVar(0, 1, indexInputsNotInLoop.get(i)+"reifModelInNILoop");
    	}
    	
    	for(int i=0;i<indexOutputsNotInLoop.size();i++){
    		reifVarModelOut[i] = solver.intVar(0, 1, indexOutputsNotInLoop.get(i)+"reifModelOutNILopp");
    	}
    	
    	for(int i=0; i< indexInputsNotInLoop.size();i++){
    		reifConstIn[i] = solver.neq(VarModel[indexInputsNotInLoop.get(i)], 0);
        	
        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
    	}
    	solver.add(solver.le(solver.sum(reifVarModelIn),1));
    	
    	
    	for(int i=0; i< indexOutputsNotInLoop.size();i++){
    		reifConstOut[i] = solver.neq(VarModel[indexOutputsNotInLoop.get(i)], 0);
        	
        	solver.add(solver.eq(reifConstOut[i], reifVarModelOut[i]));
    	}
    	solver.add(solver.le(solver.sum(reifVarModelOut),1));
    			
		
		//Parte de los loops
    	String prime="";
    	for (int z = 0; z < N; z++) {
			if(z!=0){
			//Actulizamos los index a la de las tansiciones prima	
		    	for(int i=0;i<indexInputsInLoop.size();i++){
		    		TransitionPetriNet t1=modelConLoop.get(indexInputsInLoop.get(i));
		    		Integer primeIndex=indexTransitionInVarModel2(t1.getId()+prime);
		    		indexInputsInLoop.set(i, primeIndex);
		    	}
		    	for(int i=0;i<indexOutputsInLoop.size();i++){
		    		TransitionPetriNet t2=modelConLoop.get(indexOutputsInLoop.get(i));
		    		Integer primeIndex=indexTransitionInVarModel2(t2.getId()+prime);
		    		indexOutputsInLoop.set(i, primeIndex);
		    	}
				
			}
    		System.out.println("segunda parte para los loops, in y out prime");

    		System.out.println(indexInputsInLoop);
    		System.out.println(indexOutputsInLoop);	
			
	    	
	    	for(int i=0;i<indexInputsInLoop.size();i++){
	    		reifVarModelInLoop[i] = solver.intVar(0, 1, indexInputsInLoop.get(i)+"reifModelInLoop");
	    	}
	    	
	    	for(int i=0;i<indexOutputsInLoop.size();i++){
	    		reifVarModelOutLoop[i] = solver.intVar(0, 1, indexOutputsInLoop.get(i)+"reifModelOutLoop");
	    	}
	    	
	    	for(int i=0; i< indexInputsInLoop.size();i++){
	    		reifConstInLoop[i] = solver.neq(VarModel[indexInputsInLoop.get(i)], 0);
	        	
	        	solver.add(solver.eq(reifConstInLoop[i], reifVarModelInLoop[i]));
	    	}
	    	solver.add(solver.le(solver.sum(reifVarModelInLoop),1));
	    	
	    	
	    	for(int i=0; i< indexOutputsInLoop.size();i++){
	    		reifConstOutLoop[i] = solver.neq(VarModel[indexOutputsInLoop.get(i)], 0);
	        	
	        	solver.add(solver.eq(reifConstOutLoop[i], reifVarModelOutLoop[i]));
	    	}
	    	solver.add(solver.le(solver.sum(reifVarModelOutLoop),1));
	    	
	    	
	    	
	    	solver.add(solver.eq(solver.sum(reifVarModelOutLoop),solver.sum(reifVarModelInLoop)));
		


    		
    		prime=prime+"'";
		}
    	
    	
    	
    	
    	//Apartado B
	    	for(int i=0; i< indexInputsInLoop.size();i++){
		    	solver.add(solver.imply(solver.neq(VarModel[indexInputsInLoop.get(i)], 0),
		    			solver.gt(VarModel[indexInputsInLoop.get(i)],VarModel[indexInputsInLoop.get(i)-1])));
	    	}
	    	
	    	for(int i=0; i< indexOutputsInLoop.size();i++){
		    	solver.add(solver.imply(solver.neq(VarModel[indexOutputsInLoop.get(i)], 0),
		    			solver.gt(VarModel[indexOutputsInLoop.get(i)],VarModel[indexOutputsInLoop.get(i)-1])));
	    	}

    	
    	
    	//Apartado C

    	
    	IlcConstraint[][] reifConstThen = new IlcConstraint[indexInputsNotInLoop.size()][indexOutputsNotInLoop.size()];
    	
    	for(int i=0; i< indexInputsNotInLoop.size();i++){
    		reifConstInLoop[i] = solver.neq(VarModel[indexInputsNotInLoop.get(i)], 0);
        	
    	}
    	
    	
    	for(int i=0; i< indexOutputsNotInLoop.size();i++){
    		reifConstOutLoop[i] = solver.neq(VarModel[indexOutputsNotInLoop.get(i)], 0);
        	
    	}
    	
    	
    //Parte de la e in 1..N
    		for(int i=0; i< indexInputsInLoop.size();i++){
    			for(int j=0; j< indexOutputsInLoop.size();j++){
    				IlcConstraint then =solver.imply(solver.neq(VarModel[indexOutputsInLoop.get(j)],0),
    												solver.gt(VarModel[indexOutputsInLoop.get(j)],VarModel[indexInputsInLoop.get(i)]));
    				
    				solver.imply(solver.and(solver.eq(solver.sum(reifVarModelIn),0),solver.eq(solver.sum(reifVarModelOut),0)),//IF
    	    					then);
    			}
    		}	  
    	
    	
    	//
    	solver.add(solver.eq(solver.sum(reifVarModelOutLoop),solver.sum(reifVarModelInLoop)));

    	
    	
    //APARTADO D
		for(int i=0; i< indexInputsInLoop.size();i++){
			for(int j=0; j< indexOutputsInLoop.size();j++){
    	
				for(int y=0; y< indexInputsInLoop.size();y++){
					for(int x=0; x< indexOutputsNotInLoop.size();x++){
						
						IlcConstraint ifthen2=solver.imply(solver.neq(VarModel[indexOutputsNotInLoop.get(x)],0),
															solver.gt(VarModel[indexOutputsNotInLoop.get(x)], VarModel[indexInputsInLoop.get(y)]));
						
						IlcConstraint ifthen=solver.imply(solver.eq(solver.sum(reifVarModelOutLoop),0), ifthen2);
						for(int jj=0; jj< indexOutputsInLoop.size();jj++){

							IlcConstraint elseC= solver.imply(solver.neq(VarModel[indexOutputsInLoop.get(jj)],0),
																solver.gt(VarModel[indexOutputsInLoop.get(jj)],VarModel[indexInputsInLoop.get(y)]));
							solver.add(solver.ifThenElse(solver.eq(solver.sum(reifVarModelIn),0),
									ifthen,
									elseC));
						}					
					}
				}

			}
		}
		
    }
    
    

    /////////////////////
    /// MODEL&PLACES ////
    /////////////////////

	
    //Start (transition without input/s)
    public void startPlace(PlacePetriNet place) throws IloException{
    	
    	List<Integer> indexs=indexOfActivitiesListInVarModel(place.getOutputs());
    	
    	IlcConstraint[] reifConst = new IlcConstraint[indexs.size()];
    	IlcIntVar[] reifVarModel = new IlcIntVar[indexs.size()];
    	
    	for(int i=0;i<indexs.size();i++){
    		reifVarModel[i] = solver.intVar(0, 1, indexs.get(i)+"reifModel");
    	}
    	
    	
    	
    	
    	for(int i=0; i< indexs.size();i++){
    		reifConst[i] = solver.neq(VarModel[indexs.get(i)], 0);
        	
        	solver.add(solver.eq(reifConst[i], reifVarModel[i]));
    	}
    	solver.add(solver.eq(1, solver.sum(reifVarModel)));
    	
    }
	/*
	 * (Var-Model[Output1]!=0 + : : : + Var-Model[Outputm]!=0) = 1
	 */
	
    
    
    //place with inputs and outputs
    public void intermediatePlace(PlacePetriNet place) throws IloException{
    	List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
    	List<Integer> indexsOut=indexOfActivitiesListInVarModel(place.getOutputs());

    	
    	/*
    	 * FOR EACH Input,i and Output,j
    	 * if (Var-Model[Output_j] !=0) then (Var-Model[Output_j] > Var-Model[Input_i])
    	 * END FOR
    	 */
    	for (Integer i : indexsIn) {
			for (Integer j : indexsOut) {
				solver.add(solver.imply(solver.neq(VarModel[j], 0),
											solver.gt(VarModel[j], VarModel[i])));
			}
		}
    	//only one nput can be true
    	
    	IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
    	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
    	
    	IlcConstraint[] reifConstOut = new IlcConstraint[indexsOut.size()];
    	IlcIntVar[] reifVarModelOut = new IlcIntVar[indexsOut.size()];
    	
    	for(int i=0;i<indexsIn.size();i++){
    		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIn");
    	}
    	
    	for(int i=0;i<indexsOut.size();i++){
    		reifVarModelOut[i] = solver.intVar(0, 1, indexsOut.get(i)+"reifModelOut");
    	}
    	
    	for(int i=0; i< indexsIn.size();i++){
    		reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
        	
        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
    	}
    	solver.add(solver.le(solver.sum(reifVarModelIn),1));
    	
    	
    	for(int i=0; i< indexsOut.size();i++){
    		reifConstOut[i] = solver.neq(VarModel[indexsOut.get(i)], 0);
        	
        	solver.add(solver.eq(reifConstOut[i], reifVarModelOut[i]));
    	}
    	solver.add(solver.le(solver.sum(reifVarModelOut),1));
    	
    	
    	
    	solver.add(solver.eq(solver.sum(reifVarModelOut),solver.sum(reifVarModelIn)));
    	
    	
    	
    }
    
    
    
    //place with no output arcs)
    public void endPlace(PlacePetriNet place)  throws IloException{
    	List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
    	
    	IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
    	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
    	
    	
    	for(int i=0;i<indexsIn.size();i++){
    		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIn");
    	}
    	for(int i=0; i< indexsIn.size();i++){
    		reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
        	
        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
    	}
    	
    	
    	solver.add(solver.eq(solver.sum(reifVarModelIn), 1));

    }
    
    
    //Every activity ai appearing in the case (from the event log) to check, but
    //not in the model, is included in Var-Model, with the constraint:
    public void activityInLogNotInModel(){
    	List<Integer> indexs=indexOfActivitiesListInVarModel(activityInlogNotInModel);
    	
    	/*
    	 * Var-Model[i]=0
    	 */
    	for (Integer i : indexs) {
			solver.add(solver.eq(VarModel[i], 0));
		}
    }
    
    
    
    
	
	

    ////////////////
    /// EventLog ///
    ////////////////

	
	public void varLogConstrains(){
		/*
		 * Var-Log[a_1] > 0
		 * Var-Log[a_2] > Var-Log[a_1]
		 * : : :
		 * Var-Log[a_q] > Var-Log[a_q-1]
		 */
		System.out.println("LOG======");
		
		List<Integer> indexs = indexOfActivitiesListInVarModel2(logs);
		for (int i = 0; i < logs.size(); i++) {
			if(i==0){
				solver.add(solver.gt(VarLog[indexs.get(i)], 0));
				System.out.println("Varlog["+i+"]>0");

			}
			else{
				solver.add(solver.gt(VarLog[indexs.get(i)], VarLog[indexs.get(i-1)]));
				System.out.println("Varlog["+i+"]>Varlog["+(i-1)+"]");

			}
				
			
		}
		
		System.out.println("============");

	}

	//each activity ai appearing in the model but not in the log, the
	//following constraint is included
	public void activityInModelNotInLog(){
		
		/*
		 * Var-Log[a_i]=0
		 */
		System.out.println("NOT_IN_LOG======");
		for (int i = 0; i < model.size(); i++) {
			if(!activityInLog(model.get(i).getId())){
				solver.add(solver.eq(VarLog[i], 0));
				System.out.println("nVarlog["+i+"]=0");

			}
		}
		System.out.println("================");

	}
	
	
	//////////////
	//Duplicates//
	//////////////	
	public void activitiesAppearsMoreThanOnceInLog() throws IloException{
		///DuplicatedListList
		///{|a  |,null,|c |}
		/// |a' |      |c'|
		/// |a''|       
		System.out.println("DUPLICATED======");

		
		for (List<String> list : duplicatedListList) {
			if(!list.isEmpty()){
				List<Integer> indexOfList= indexOfActivitiesListInVarModel(list);
		    	IlcIntVar[] duplicated  = new IlcIntVar[list.size()];
		    	
		    	
				//{ The Var-Log refers the variables called a1, : : :, ak in the constraints to rep-
				//resent the sequential order.
				solver.add(solver.gt(VarLog[indexOfList.get(0)],0 ));

				for (int i = 0; i < indexOfList.size()-1; i++) {
					solver.add(solver.gt(VarLog[indexOfList.get(i+1)], VarLog[indexOfList.get(i)]));
				}
				
				String lastDuplicated=list.get(list.size()-1);
				int indexNextInLog=logs.indexOf(lastDuplicated)+1;
				if(indexNextInLog<logs.size()){
					String nextInLog=logs.get(indexNextInLog);
							
					Integer indexNextInVarLog=indexTransitionInVarModel(nextInLog);

					solver.add(solver.gt(VarLog[indexNextInVarLog], VarLog[indexOfList.get(indexOfList.size()-1)]));
				}

				//Var-Model[a'] = 0 AND ...
				for (int i = 1; i < indexOfList.size(); i++) {
					solver.add(solver.eq(VarModel[indexOfList.get(i)], 0));
				}
				
				//XOR
				int n=5;
				for (int i = 0; i < list.size(); i++) {
		        	duplicated[i] = solver.intVar(0, model.size()+n, list.get(i)+"-Duplicated LOG.");
				}
				
				for (int i = 0; i < duplicated.length; i++) {
					
					IlcConstraint preXor=solver.eq(VarLog[indexOfList.get(0)], duplicated[i]);
					for (int j = 1; j < indexOfList.size(); j++) {
						preXor=solver.xor(preXor, solver.eq(VarLog[indexOfList.get(j)], duplicated[i]));
						
					}
					solver.add(preXor);
				}
				
				solver.add(solver.allDiff(duplicated));

			
			}
			
		}
					

		System.out.println("================");

	}
	
	
	
	///////////////////
	/// CSV Methods ///
	///////////////////
	
	 public IlcSolver createConstrains()  throws IloException{

		//caseanalisys
		makeMatrix();

		caseAnalysis();
		
		loopUnrolling();
		 
		 
//		 //log constrains
		 varLogConstrains();
	 
//		//others (not-in activities)
		activityInLogNotInModel();
		activityInModelNotInLog();
		activitiesAppearsMoreThanOnceInLog();
		

		
//		 
//		 
//		 //model
		 for (PlacePetriNet place : places) {
			if (place.getIs_star()){
				startPlace(place);
			}
			else if(place.getIs_end()){
				endPlace(place);
			}
			else{
				//LOOPS
				
				Integer haveInLoop=haveLoopInput(place);
				Integer haveOutLoop=haveLoopOutput(place);
				Integer N=haveInLoop;
				
				//para tener el numero mayor de numberofX entre todos sus outputs e inputs
				if(N<haveOutLoop){
					N=haveOutLoop;
				}
				
				
				if(haveInLoop!=0 || haveOutLoop!=0){
					if(placeWithCase1(place)){
						case1(place, N);
					}
					else{
						case2(place, N);
					}
				}
				else{
					intermediatePlace(place);
				}
			}
			
		}
		 

		
		return solver;
	 }
	
	public Integer haveLoopInput(PlacePetriNet place){
		Integer res=0;
		Integer aux=0;
		for (String input : place.getInputs()) {
			Integer index=indexTransitionInVarModel2(input);
			if(transitionInLoop.contains(index)){
				aux=numberOfAiLoops.get(index);
			}
			if(aux>res){
				res=aux;
			}
		}
		return res;
	}
	
	public Integer haveLoopOutput(PlacePetriNet place){
		Integer res=0;
		Integer aux=0;
		for (String out : place.getOutputs()) {
			Integer index=indexTransitionInVarModel2(out);
			if(transitionInLoop.contains(index)){
				aux=numberOfAiLoops.get(index);
			}
			if(aux>res){
				res=aux;
			}
		}
		return res;
	}

	public boolean placeWithCase1(PlacePetriNet place){
		Boolean res=true;
		for (String input : place.getInputs()) {
			Integer index=indexTransitionInVarModel2(input);
			if(!transitionInLoop.contains(index)){
				res=false;
			}
		}
		for (String out : place.getOutputs()) {
			Integer index2=indexTransitionInVarModel2(out);
			if(!transitionInLoop.contains(index2)){
				res=false;
			}
		}
		return res;
	}
	
	
	

	
	 //Var alignment Constranints and minimize
	 public IlcSolver DifferenceAligmentAndMinimize(){
			
		/* #1
		 * For every i in 1..Var-Model.size do:
		 * IF(Var-Log[i]==Var-Model[i])
		 * THEN (Var-Dierence[i]=0)
		 * ELSE IF(Var-Log[i]==0 or Var-Model[i]==0)
		 * 	THEN (Var-Dierence[i]=1)
		 * 	ELSE (Var-Dierence[i]=2)
		 * 	END
		 * END
		 */
		 for (int i = 0; i < model.size(); i++) {
			 solver.add(solver.ifThenElse(solver.and(solver.neq(VarLog[i], 0), solver.eq(VarLog[i], VarModel[i])), 
				 					  	  solver.eq(VarDifference[i], 0),
				 					  	  solver.eq(VarDifference[i], 1)));
		 }
		 
		 
		/* #2
		 * //following the theory of alignment
		 * for(int i=0; i<Var-Model.size(); i++)
			 * for(int j=0; j<Var-Model.size(); j++)
			 * (i!=j)
				 * if (VarModel[i]!=0) then VarModel[i]!=VarLog[j];
				 * END
			 * END
		 * END
		 * Var-Alignment=Var-Difference[1]+ : : : + Var-Difference[n]
		 */
		 for(int i=0; i<VarModel.length; i++){
			 for(int j=0; j<VarModel.length; j++){
				 if(i!=j){
					 
					 solver.add(solver.imply(solver.neq(VarModel[i],0),solver.neq(VarModel[i], VarLog[j]) ));
					 
					
				 }
			 }
		 }
		 
		 
	     solver.add(solver.eq(solver.sum(VarDifference),VarAlignment));

	 	/* #3
	 	 * minimize(Var-Alignment)
	 	 */	
	     solver.add(solver.minimize(VarAlignment));

	     return solver;
	 }
	 
	 

	 
	 
	
	public void search() throws IloException{
        System.out.println("------SEARCH INFO-------");

		solver.newSearch();
        
        System.out.println("Logs"+logs);
        System.out.println("ActivitiesNotInModel"+activityInlogNotInModel);
        System.out.println("DuplicatedListList "+duplicatedListList);


        System.out.println("VarModel Length: "+VarModel.length);
        System.out.println("VarLog length: "+ VarLog.length);
        System.out.println("VarDifference length: "+VarDifference.length);
        System.out.println("VarAlignment: "+VarAlignment);
        
        
        System.out.println("------SEARCH-------");
        int ite=0;
        while(solver.next()){
        	ite++;
	        for(int i=0;i<VarModel.length;i++){
	        	System.out.println(((IlcIntVar)VarModel[i]).getName()+ " " + VarModel[i]+ " :" + VarLog[i]);	
	        	System.out.println("valor del total " + ((IlcIntVar)VarDifference[i]).getName() +" :" + VarDifference[i]);	

	        }
	
	        System.out.println("Total " + ((IlcIntVar)VarAlignment).getName() +" :" + VarAlignment);
	        
	        System.out.println("---------NEXT-----------");
        }

        System.out.println("================");
        
        solver.printInformation();
//        solver.printModel();
        
        solver.endSearch();
        System.out.println("iteraciones: "+ite);
	}

	 


 
	
	
	
}
