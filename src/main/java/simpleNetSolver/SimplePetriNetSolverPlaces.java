package simpleNetSolver;
import java.io.FileWriter;
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
import test.EscribeFichero;

public class SimplePetriNetSolverPlaces {
	
    protected IlcSolver solver;
    protected IlcIntVar[] VarModel ;
    protected IlcIntVar[] VarLog;
    protected IlcIntVar[] VarDifference;
    protected IlcIntVar VarAlignment;
    
    protected List<TransitionPetriNet> model;
    protected List<PlacePetriNet> places;
    protected List<String> logs;
    protected List<String> duplicatedInLog;  
    protected List<List<String>> duplicatedListList;  

    protected List<String> activityInlogNotInModel;
    protected Integer modelSizeWithoutNotInActivities;
    
    protected Integer numberOfXorMultiplesOuts;
    
    private int n;
    //int n = 40;
    private int error;
    private int jump;
    
    public SimplePetriNetSolverPlaces(List<TransitionPetriNet> modelExt, List<PlacePetriNet> placesExt, List<String> logsExt) throws IloException{
    	solver=new IlcSolver();
        this.places=placesExt;
    	this.model=modelExt;  
    	modelSizeWithoutNotInActivities=modelExt.size();
    	
    	numberOfXorMultiplesOuts=0;
    	
        //Change Duplicated Logs
        logs=new ArrayList<String>();
        duplicatedInLog=new ArrayList<String>();
        activityInlogNotInModel=new ArrayList<String>();
        duplicatedListList= new ArrayList<List<String>>();
        for (int i = 0; i < logsExt.size(); i++) {
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
    			if(!logs.contains(logExt)){
    				logs.add(logExt);
    			}
				activityInlogNotInModel.add(logExt);
				model.add(new TransitionPetriNet(logExt));
			}
		}

        
        VarModel = new IlcIntVar[model.size()];
        VarLog = new IlcIntVar[model.size()];
        VarDifference = new IlcIntVar[model.size()];
        
        //the worst case is: the longest path plus the log size
        n = model.size() + logs.size();
        //int n = 40;
        error = n; //logs.size();
        jump = logs.size();
        System.out.println("-------------" + n);
        VarAlignment = solver.intVar(0, error, " Alingment");
        for(int i=0;i<model.size();i++){
        	VarModel[i] = solver.intVar(0, n, model.get(i).getName()+" Model");
        	VarLog[i] = solver.intVar(0, n, model.get(i).getName()+" Log");
        	VarDifference[i] = solver.intVar(0, 1, model.get(i).getName()+" Difference..");
        }
        

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
//    	System.out.println(activityId);
//    	System.out.println(logs.contains(activityId));

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
   
    public List<Integer> indexOfActivitiesInmodelNotInLog(List<String> model){
    	List<Integer> res=new ArrayList<Integer>();
    	for (String mod : model) {
    		if(!logs.contains(mod)){
				res.add(indexTransitionInVarModel(mod));
			}
    	}
    	return res;
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
				//solver.add(solver.imply(solver.neq(VarModel[j], 0),solver.and(solver.lt(VarModel[j], solver.sum(VarModel[i], jump)), solver.gt(VarModel[j], VarModel[i]))));
				solver.add(solver.imply(solver.neq(VarModel[j], 0),solver.gt(VarModel[j], VarModel[i])));
			}
		}
    	//only one nput can be true
    	
    	IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
    	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
    	
    	IlcConstraint[] reifConstOut = new IlcConstraint[indexsOut.size()];
    	IlcIntVar[] reifVarModelOut = new IlcIntVar[indexsOut.size()];
    	
    	if(indexsIn.size()==1 && indexsOut.size()==1){
    		//solver.add(solver.ifThenElse(solver.neq(VarModel[indexsIn.get(0)],0), solver.and(solver.lt(VarModel[indexsOut.get(0)], solver.sum(VarModel[indexsIn.get(0)], jump)),solver.gt(VarModel[indexsOut.get(0)],VarModel[indexsIn.get(0)])), solver.eq(VarModel[indexsOut.get(0)],0)));
    		solver.add(solver.ifThenElse(solver.neq(VarModel[indexsIn.get(0)],0), solver.gt(VarModel[indexsOut.get(0)],VarModel[indexsIn.get(0)]), solver.eq(VarModel[indexsOut.get(0)],0)));
    	}else{
    	
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

	
	public void varLogConstrains() throws IloException{
		/*
		 * Var-Log[a_1] > 0
		 * Var-Log[a_2] > Var-Log[a_1]
		 * : : :
		 * Var-Log[a_q] > Var-Log[a_q-1]
		 */
//		System.out.println("LOG======");
		/*
		IlcConstraint[][] reifConstAssignedValue = new IlcConstraint[logs.size()][model.size()];
		IlcIntVar[][] reifVarModelIn = new IlcIntVar[logs.size()][model.size()];
		
		for(int i=0;i<logs.size();i++){
			for(int j=0;j<model.size();j++){
				reifVarModelIn[i][j] = solver.intVar(0, 1, "reifModelIn"+i+j);
	    	}
    		
    	}
		*/
		List<Integer> indexs = indexOfActivitiesListInVarModel(logs);
		for (int i = 0; i < logs.size(); i++) {
			
				
			if(i==0){
				//solver.add(solver.ifThenElse(solver.gt(solver.sum(reifConstAssignedValue[i]), 0) , solver.eq(VarLog[indexs.get(i)], 1),solver.gt(VarLog[indexs.get(i)], 0)));
				/*
				for(int j=0; j<model.size();j++){
					reifConstAssignedValue[i][j] = solver.eq(1, VarModel[j]);					
					solver.add(solver.eq(reifConstAssignedValue[i][j],  reifVarModelIn[i][j]));
				}
				*/
				solver.add(solver.gt(VarLog[indexs.get(i)], 0));
				//solver.add(solver.ifThenElse(solver.gt(solver.sum(reifConstAssignedValue[i]), 0), solver.eq(VarLog[indexs.get(i)], 1), solver.gt(VarLog[indexs.get(i)], 0)));
//				System.out.println("Varlog["+i+"]>0");

			}
			else{
				/*
				for(int j=0; j<model.size();j++){
					reifConstAssignedValue[i][j] = solver.eq(solver.sum(VarLog[indexs.get(i-1)],1), VarModel[j]);					
					solver.add(solver.eq(reifConstAssignedValue[i][j],  reifVarModelIn[i][j]));
				}
				solver.add(solver.ifThenElse(
						solver.gt(solver.sum(reifConstAssignedValue[i]), 0) , 
						solver.eq(VarLog[indexs.get(i)], solver.sum(VarLog[indexs.get(i-1)],1)),
						solver.gt(VarLog[indexs.get(i)], VarLog[indexs.get(i-1)])));
						*/
				solver.add(solver.gt(VarLog[indexs.get(i)], VarLog[indexs.get(i-1)]));
//				System.out.println("Varlog["+i+"]>Varlog["+(i-1)+"]");

			}
			//solver.add(solver.le(VarLog[indexs.get(i)], n-i));	
			
			
		}
		
		
//		System.out.println("============");

	}

	//each activity ai appearing in the model but not in the log, the
	//following constraint is included
	public void activityInModelNotInLog(){
		
		/*
		 * Var-Log[a_i]=0
		 */
//		System.out.println("NOT_IN_LOG======");
		for (int i = 0; i < model.size(); i++) {
			if(!activityInLog(model.get(i).getId())){
				solver.add(solver.eq(VarLog[i], 0));
//				System.out.println("nVarlog["+i+"]=0");

			}
		}
//		System.out.println("================");

	}
	
	
	////////////
	//REAPASAR//
	////////////
	public void activitiesAppearsMoreThanOnceInLog() throws IloException{
		///DuplicatedListList
		///{|a  |,null,|c |}
		/// |a' |      |c'|
		/// |a''|       
//		System.out.println("DUPLICATED======");

		
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
					

//		System.out.println("================");

	}
	
	
	
	///////////////////
	/// CSV Methods ///
	///////////////////
	
	 public IlcSolver createConstrains()  throws IloException{
		 Integer contadorDePlacesXor=0;
//		 //log constrains
		 varLogConstrains();
//		 
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
				if(place.getOutputs().size()>1 || place.getInputs().size()>1){
					contadorDePlacesXor++;
				}
				intermediatePlace(place);
			}			
		}
		 
		numberOfXorMultiplesOuts = contadorDePlacesXor;
		return solver;
	 }
	
	
	
	 //Var alignment Constranints and minimize
	 public IlcSolver DifferenceAligmentAndMinimize() throws IloException{
			
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
		 for(int i=0; i<VarModel.length; i++){
			 for(int j=0; j<VarModel.length; j++){
				 if(i!=j){
					 
					 solver.add(solver.imply(solver.neq(VarModel[i],0),solver.neq(VarModel[i], VarModel[j]) ));
					 solver.add(solver.imply(solver.neq(VarLog[i],0),solver.neq(VarLog[i], VarLog[j]) ));
					 
					
				 }
			 }
			 solver.add(solver.or(solver.eq(VarLog[i],VarModel[i]),solver.or(solver.eq(0,VarLog[i]), solver.eq(0,VarModel[i])) ));
		 }
		 
		 for (int i = 0; i < model.size(); i++) {
			 solver.add(solver.ifThenElse(solver.eq(VarLog[i], VarModel[i]), 
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
		 
		 //evitando que se creen huecos
		 /*
		 IlcConstraint[][] reifConstAssignedValue = new IlcConstraint[n][model.size()];
			IlcIntVar[][] reifVarModelIn = new IlcIntVar[n][model.size()];
			IlcIntVar[] VarListTime = new IlcIntVar[n];
			
			for(int i=0;i<n;i++){
				for(int j=0;j<model.size();j++){
					reifVarModelIn[i][j] = solver.intVar(0, 1, "reifModelIn"+i+j);
		    	}
				VarListTime[i] =  solver.intVar(0, 2, "reifModelIn"+i);
	    	}
		 for(int i=1;i<=n;i++){
			 for(int j=0;j<model.size();j++){
				 reifConstAssignedValue[i-1][j] = solver.or(solver.eq(i, VarModel[j]), solver.eq(i, VarLog[j]));
				 solver.add(solver.eq(reifConstAssignedValue[i-1][j], reifVarModelIn[i-1][j]));
				 
			 }
			 solver.add(solver.eq(VarListTime[i-1], solver.sum(reifVarModelIn[i-1])));
		 }
		 for(int i=0;i<n-1;i++){
			 solver.add(solver.not(solver.and(solver.eq(VarListTime[i],0), solver.gt(VarListTime[i+1],0 ))));
		 }
		 
		 */
	     solver.add(solver.eq(solver.sum(VarDifference),VarAlignment));

	 	/* #3
	 	 * minimize(Var-Alignment)
	 	 */	
	     solver.add(solver.minimize(VarAlignment));

	     return solver;
	 }
	 
	 

	 
	 
	
	public void search( FileWriter fichero) throws IloException{
        System.out.println("------SEARCH INFO-------");

        
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        
        
        
        
		solver.newSearch();
        
        System.out.print("Model[");
        for(TransitionPetriNet t: model){
        	System.out.print(t.getId()+",");
        }System.out.println("]");
        System.out.println("Logs"+logs);
        System.out.println("ActivitiesNotInModel"+activityInlogNotInModel);
        System.out.println("DuplicatedListList "+duplicatedListList);


        System.out.println("VarModelLogDifference Length: "+VarModel.length);
        System.out.println("Log length: "+ logs.size());
        //System.out.println("VarDifference length: "+VarDifference.length);
        System.out.println("VarAlignment: "+VarAlignment);
  
        System.out.println("------SEARCH-------");
        Integer iter=1;
        String ultimaIteracion="";
        while(solver.next()){
	      System.out.println("=====NEXT=====");
	     ultimaIteracion="\n";
/*	        for(int i=0;i<VarModel.length;i++){
	        	
	        	System.out.println(((IlcIntVar)VarModel[i]).getName()+ " " + VarModel[i]+ " :" + VarLog[i]);	
	        	System.out.println("valor del total " + ((IlcIntVar)VarDifference[i]).getName() +" :" + VarDifference[i]);	

	       }
	       */
//	
//	        System.out.println("Total " + ((IlcIntVar)VarAlignment).getName() +" :" + VarAlignment);
//        
	        System.out.println("iter : " + iter +  " VarAligment:" +  VarAlignment);
	        
	        
	        
	    for(int i=0;i<VarModel.length;i++){
        	ultimaIteracion=ultimaIteracion+"\n"+((IlcIntVar)VarModel[i]).getName()+ " " + VarModel[i]+ " :" + VarLog[i];	
        	ultimaIteracion=ultimaIteracion+"\n"+"valor del total " + ((IlcIntVar)VarDifference[i]).getName() +" :" + VarDifference[i];	

        }

	    ultimaIteracion=ultimaIteracion+"\n"+"Total " + ((IlcIntVar)VarAlignment).getName() +" :" + VarAlignment;
    
	    ultimaIteracion=ultimaIteracion+"\n"+"iter : " + iter;

	        iter++;
        }
        System.out.println("================");
        
        solver.printInformation();
       // solver.printModel();
//       
        solver.endSearch();
        time_end = System.currentTimeMillis();
        
        
        
        ultimaIteracion=ultimaIteracion+"\n"+"iterations : " + iter;
        ultimaIteracion=ultimaIteracion+"\n"+"Transitions num: "+this.model.size();
        ultimaIteracion=ultimaIteracion+"\n"+"Places num: "+places.size();
        ultimaIteracion=ultimaIteracion+"\n"+"Places XOR:  "+numberOfXorMultiplesOuts;
        ultimaIteracion=ultimaIteracion+"\n"+"Log :  "+logs.size();
        ultimaIteracion=ultimaIteracion+"\n"+"In log, not in Model activities size :  "+activityInlogNotInModel.size();
        ultimaIteracion=ultimaIteracion+"\n"+"Activities duplicated in log :  "+duplicatedInLog.size();
        ultimaIteracion=ultimaIteracion+"\n"+"Tiempo de ejecuci�n :  "+((double)time_end-(double)time_start)/1000;
        
        //imprimir ultimaiteracion
        EscribeFichero.execute(ultimaIteracion,fichero);
	}

	 


 
	
	
	
}
