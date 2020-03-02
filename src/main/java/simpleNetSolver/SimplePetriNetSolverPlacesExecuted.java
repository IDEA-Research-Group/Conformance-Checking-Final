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
/********************************/
/*para conocer el m�ximo y m�nimo n�mero de transacciones que se puedne ejecutar*/
/************************************/
public class SimplePetriNetSolverPlacesExecuted {
	
    protected IlcSolver solver;
    //protected IlcIntVar[] VarModel ;
    protected IlcIntVar[] VarModelBool ;
    //protected IlcIntVar[] VarLog;
    //protected IlcIntVar[] VarDifference;
    //protected IlcIntVar[] CountDifferenceNum;
    //protected IlcIntVar sumCount;
    protected IlcIntVar VarAlignment;
    
    protected List<TransitionPetriNet> model;
    protected List<PlacePetriNet> places;
    protected List<String> logs;
    protected List<String> duplicatedInLog;  
    protected List<List<String>> duplicatedListList;  

    protected List<String> activityInlogNotInModel;
    protected Integer modelSizeWithoutNotInActivities;
    private int jump;
    private int n;
    protected Integer numberOfXorMultiplesOuts;
    
    //Floid
    int[][] floyd;
    int[][] mandatory;
    int startTransition;
    int endTransition;
    
    public SimplePetriNetSolverPlacesExecuted(List<TransitionPetriNet> modelExt, List<PlacePetriNet> placesExt, List<String> logsExt) throws IloException{
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

        
        
        
       
    }
    
    public void createVariables() throws IloException{
    	//VarModel = new IlcIntVar[model.size()];
    	VarModelBool = new IlcIntVar[model.size()];
        //-VarLog = new IlcIntVar[model.size()];
        //-VarDifference = new IlcIntVar[model.size()];
        //CountDifferenceNum = new IlcIntVar[model.size()];
        
        //the worst case is: the longest path plus the log size
        n = model.size() + logs.size()/5;//model.size();// + logs.size();
        jump = logs.size();
        int error = model.size();//n;//logs.size();
        
        System.out.println("-------------N:" + n);
        VarAlignment = solver.intVar(0, n, " Alingment");
        //sumCount = solver.intVar(0, n, " Alingment");
        for(int i=0;i<model.size();i++){
        	//VarModel[i] = solver.intVar(0, n-floyd[i][endTransition], model.get(i).getName()+" Model");
        	//VarModel[i] = solver.intVar(0, n-floyd[i][endTransition], model.get(i).getName()+" Model");
        	VarModelBool[i] = solver.intVar(0, 1, model.get(i).getName()+" Model");
        	//-VarLog[i] = solver.intVar(0, n, model.get(i).getName()+" Log");
        	//-VarDifference[i] = solver.intVar(0, 2, model.get(i).getName()+" Difference..");
        	
        	//-solver.add(solver.or(solver.eq(VarModel[i], 0), solver.ge(VarModel[i], floyd[startTransition][i])));
        }
        //for mandatory
        
        for(int i=0;i<model.size();i++){
        	for(int j=0;j<model.size();j++){
        		if (i==startTransition){
		        	if(mandatory[i][j]==1){
		        		//solver.add(solver.ge(VarModel[j],floyd[i][j]));		        				        	        		
		        		solver.add(solver.eq(VarModelBool[j],1));
		        		//System.out.println("solver.add(solver.eq("+VarModelBool[j]+",1));");
		        	}
        		}	
		        else{
		        	/*
		        	if(mandatory[i][j]==1 && floyd[i][j]>0 && i!=j){
		        		solver.add(solver.imply(solver.eq(VarModelBool[i], 1), solver.eq(VarModelBool[j], 1)));	
	        			solver.add(solver.imply(solver.eq(VarModelBool[i], 1), solver.le(solver.sum(floyd[i][j],VarModel[i]), VarModel[j])));
		        		//System.out.println("solver.add(solver.imply(solver.eq("+VarModelBool[i]+", 1), solver.eq("+VarModelBool[j]+", 1)));");
		        	}*/
		        	if(floyd[i][j]>0){
		        		if(mandatory[i][j]==1){
		        			solver.add(solver.imply(solver.eq(VarModelBool[i], 1), solver.eq(VarModelBool[j], 1)));
		        		}
		        		//solver.add(solver.imply(solver.eq(VarModelBool[j], 1), solver.le(solver.sum(floyd[i][j],VarModel[i]), VarModel[j])));
		        		//solver.add(solver.or(solver.eq(VarModel[j], 0), solver.le(VarModel[i], VarModel[j])));
		        	}
	        		
		        }
        		
        	}
        	//-solver.add(solver.le(solver.sum(floyd[i][endTransition],VarModel[i]), VarModel[endTransition]));
        }
        
        //solver.add(solver.lt(VarModel[endTransition]));
    }
    public void obtainFloydMatrix(){
    	floyd = new int[model.size()][model.size()];
    	mandatory = new int [model.size()][model.size()];
    	for (int i=0;i<model.size();i++){
    		for (int j=0;j<model.size();j++){
        		if(i==j){
        			floyd[i][j]=0;
        			mandatory[i][j]=1;
        		}
        		else{
        			floyd[i][j]=-1;
        			mandatory[i][j]=0;
        		}
        	}
    	}
    	//for every intermediate place, put '1' in the floid matrix, and -1 otherwise
    	for (PlacePetriNet place : places) {
			if (place.getIs_star()){
				List<Integer> indexs=indexOfActivitiesListInVarModel(place.getOutputs());
		    	//for floyd
		    	startTransition = indexs.get(0);
		    	
		    	
			}else if(place.getIs_end()){
				List<Integer> indexs=indexOfActivitiesListInVarModel(place.getInputs());
		    	//for floyd
		    	endTransition = indexs.get(0);
			}else{
				List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
		    	List<Integer> indexsOut=indexOfActivitiesListInVarModel(place.getOutputs());

		    	for (Integer in : indexsIn) {
					for (Integer jo : indexsOut) {
						floyd[in][jo]=1;
						
					}
				}
		    	if(indexsIn.size()==1 && indexsOut.size()==1){
		    		mandatory[indexsIn.get(0)][indexsOut.get(0)]=1;
		    	}else if(indexsIn.size()==1 && indexsOut.size()>1){
		    		//one input several outputs
		    		for(int i=0;i<indexsOut.size();i++){
		    			mandatory[indexsIn.get(0)][indexsOut.get(i)]=0;
		    		}
		    	}else if(indexsIn.size()>1 && indexsOut.size()==1){
		    		for(int i=0;i<indexsIn.size();i++){
		    			mandatory[indexsIn.get(i)][indexsOut.get(0)]=1;
		    		}
		    	}else{
		    		for(int i=0;i<indexsIn.size();i++){
		    			for(int j=0;j<indexsOut.size();j++){
		    				mandatory[indexsIn.get(i)][indexsOut.get(j)]=0;
		    			}
		    		}
		    	}

			}
			
						
		}
    	//execute the algorithm
    	
    	for (int k=0;k<model.size();k++){

	    	for (int i=0;i<model.size();i++){
	    		for (int j=0;j<model.size();j++){
	
	    			if ((floyd[i][k]!=-1)&&(floyd[k][j]!=-1)){
	
	    				floyd[i][j]=funcionfloyd(floyd[i][j],floyd[i][k]+floyd[k][j]);
	    			}
	    			
	    				if(mandatory[i][k]+mandatory[k][j]==2){
	    					mandatory[i][j]=1;
	    			
	    			}
    			}

	    	}

    	}
/*
    	System.out.println("Matriz de adyacencia correspondiente: ");

    	for (int i=0;i<model.size();i++){

	    	for (int j=0;j<model.size();j++){
	
	    		System.out.print(" � "+mandatory[i][j]);
	    	}
	    	System.out.println();
    	
    	}
  */  	
    	System.out.println("Start - Matriz de adyacencia correspondiente: ");
    	for (int i=0;i<model.size();i++)
    		System.out.print(" � "+floyd[startTransition][i]);
    	
    	System.out.println("End - Matriz de adyacencia correspondiente: ");
    	for (int i=0;i<model.size();i++){
    		System.out.print(" � "+floyd[i][endTransition]);
    	}
    	System.out.println("Star - Matriz de madatroy correspondiente: ");
    	for (int i=0;i<model.size();i++){
    		System.out.print(" � "+mandatory[startTransition][i]);
    	}
    /*	
    	System.out.println("Start - Mandatory Matrix ");
    	for (int i=0;i<model.size();i++){
    		for (int j=0;j<model.size();j++){
    		System.out.print(" � "+mandatory[i][j]);
    		}
    		System.out.println();
    	}
    	*/
    }
    private static int funcionfloyd(int A, int B){

			if ((A==-1)&&(B==-1))
			
			return -1;
			
			else if (A==-1)
			
			return B;
			
			else if (B==-1)
			
			return A;
			
			else if (A>B)
			
			return B;
			
			else return A;

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
    	
    	if(indexs.size()==1){
    		//solver.add(solver.eq(1, VarModel[indexs.get(0)]));
    		
    		solver.add(solver.eq(1, VarModelBool[indexs.get(0)]));
    		//System.out.println("solver.add(solver.eq(1, "+VarModelBool[indexs.get(0)]+"));");
    	}else{
	    	IlcConstraint[] reifConst = new IlcConstraint[indexs.size()];
	    	IlcIntVar[] reifVarModel = new IlcIntVar[indexs.size()];
	    	
	    	for(int i=0;i<indexs.size();i++){
	    		reifVarModel[i] = solver.intVar(0, 1, indexs.get(i)+"reifModel");
	    	}
	    	
	    	
	    	
	    	
	    	for(int i=0; i< indexs.size();i++){
	    		//-reifConst[i] = solver.neq(VarModel[indexs.get(i)], 0);
	    		reifConst[i] = solver.eq(VarModelBool[indexs.get(i)], 1);
	        	
	        	solver.add(solver.eq(reifConst[i], reifVarModel[i]));
	    	}
	    	solver.add(solver.eq(1, solver.sum(reifVarModel)));
    	}
    }
	/*
	 * (Var-Model[Output1]!=0 + : : : + Var-Model[Outputm]!=0) = 1
	 */
	
    
    
    //place with inputs and outputs
    public void intermediatePlace(PlacePetriNet place) throws IloException{
    	List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
    	List<Integer> indexsOut=indexOfActivitiesListInVarModel(place.getOutputs());

    	for (Integer i : indexsIn) {
			for (Integer j : indexsOut) {
				//solver.add(solver.imply(solver.neq(VarModel[j], 0),solver.and(solver.lt(VarModel[j], solver.sum(VarModel[i], jump)), solver.gt(VarModel[j], VarModel[i]))));
				//-solver.add(solver.imply(solver.neq(VarModel[j], 0),solver.gt(VarModel[j], VarModel[i])));
				//solver.add(solver.imply(solver.eq(VarModelBool[j], 1),solver.and(solver.lt(VarModel[j], solver.sum(VarModel[i], jump)), solver.gt(VarModel[j], VarModel[i]))));
				
			}
		}
    	//only one nput can be true
    	
    	
    	if(indexsIn.size()==1 && indexsOut.size()==1){
    		
    		
    		solver.add(solver.ifThenElse(solver.eq(
    				VarModelBool[indexsIn.get(0)],1), 
    				solver.eq(VarModelBool[indexsOut.get(0)],1), 
    				solver.eq(VarModelBool[indexsOut.get(0)],0)));
    		//solver.add(solver.ifThenElse(solver.eq(VarModelBool[indexsIn.get(0)],1), solver.gt(VarModel[indexsOut.get(0)],VarModel[indexsIn.get(0)]), solver.eq(VarModel[indexsOut.get(0)],0)));
    		//System.out.println("solver.add(solver.ifThenElse(solver.eq("+VarModelBool[indexsIn.get(0)]+",1), solver.eq("+VarModelBool[indexsOut.get(0)]+",1), solver.eq("+VarModelBool[indexsOut.get(0)]+",0)));");
    	}else{
    		
    		IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
        	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
        	
        	IlcConstraint[] reifConstOut = new IlcConstraint[indexsOut.size()];
        	IlcIntVar[] reifVarModelOut = new IlcIntVar[indexsOut.size()];
        	
    		if(indexsIn.size()==1){
    			if(indexsOut.size()==2){
    				//-solver.add(solver.ifThenElse(solver.neq(VarModel[indexsIn.get(0)],0), solver.xor(solver.gt(VarModel[indexsOut.get(0)],VarModel[indexsIn.get(0)]), solver.gt(VarModel[indexsOut.get(1)],indexsIn.get(0))), solver.and(solver.eq(VarModel[indexsOut.get(1)], 0),solver.eq(VarModel[indexsOut.get(0)], 0) )));
    				solver.add(solver.ifThenElse(solver.eq(VarModelBool[indexsIn.get(0)],1), solver.xor(solver.eq(VarModelBool[indexsOut.get(0)],1), solver.eq(VarModelBool[indexsOut.get(1)],1)), solver.and(solver.eq(VarModelBool[indexsOut.get(1)], 0),solver.eq(VarModelBool[indexsOut.get(0)], 0) )));
    				//System.out.println("solver.add(solver.ifThenElse(solver.eq("+VarModelBool[indexsIn.get(0)]+",1), solver.xor(solver.eq("+VarModelBool[indexsOut.get(0)]+",1), solver.eq("+VarModelBool[indexsOut.get(1)]+",1)), solver.and(solver.eq("+VarModelBool[indexsOut.get(1)]+", 0),solver.eq("+VarModelBool[indexsOut.get(0)]+", 0) )));");
    			}else{
	    			for(int i=0;i<indexsOut.size();i++){
			    		reifVarModelOut[i] = solver.intVar(0, 1, indexsOut.get(i)+"reifModelOut");
			    	}
	    			for(int i=0; i< indexsOut.size();i++){
	    				//-reifConstOut[i] = solver.neq(VarModel[indexsOut.get(i)], 0);
			    		reifConstOut[i] = solver.eq(VarModelBool[indexsOut.get(i)], 1);
			        	
			        	solver.add(solver.eq(reifConstOut[i], reifVarModelOut[i]));
			    	}
			    	solver.add(solver.le(solver.sum(reifVarModelOut),1));
			    	//-solver.add(solver.ifThenElse(solver.neq(VarModel[indexsIn.get(0)],0), solver.eq(solver.sum(reifVarModelOut),1), solver.eq(solver.sum(reifVarModelOut),0)));
			    	solver.add(solver.ifThenElse(solver.eq(VarModelBool[indexsIn.get(0)],1), solver.eq(solver.sum(reifVarModelOut),1), solver.eq(solver.sum(reifVarModelOut),0)));
			    	//-solver.add(solver.ifThenElse(solver.eq(solver.sum(reifVarModelOut),1), solver.neq(VarModel[indexsIn.get(0)],0), solver.neq(VarModel[indexsIn.get(0)],1)));
			    	solver.add(solver.ifThenElse(solver.eq(solver.sum(reifVarModelOut),1), solver.eq(VarModelBool[indexsIn.get(0)],1), solver.eq(VarModelBool[indexsIn.get(0)],0)));
    			}
    			
    		}else if(indexsOut.size()==1){
    			if(indexsIn.size()==2){
    				
    				//solver.add(solver.ifThenElse(solver.eq(VarModel[indexsOut.get(0)],0), solver.and(solver.eq(VarModel[indexsIn.get(1)], 0),solver.eq(VarModel[indexsIn.get(0)], 0)),solver.xor(solver.eq(VarModel[indexsIn.get(0)],0), solver.eq(VarModel[indexsIn.get(1)],0)) ));
    				solver.add(solver.ifThenElse(solver.eq(VarModelBool[indexsOut.get(0)],0), solver.and(solver.eq(VarModelBool[indexsIn.get(1)], 0),solver.eq(VarModelBool[indexsIn.get(0)], 0)),solver.xor(solver.eq(VarModelBool[indexsIn.get(0)],1), solver.eq(VarModelBool[indexsIn.get(1)],1)) ));
    				//System.out.println("solver.add(solver.ifThenElse(solver.eq("+VarModelBool[indexsOut.get(0)]+",0), solver.and(solver.eq("+VarModelBool[indexsIn.get(1)]+", 0),solver.eq("+VarModelBool[indexsIn.get(0)]+", 0)),solver.xor(solver.eq("+VarModelBool[indexsIn.get(0)]+",1), solver.eq("+VarModelBool[indexsIn.get(1)]+",1)) ));");
    			}else{
	    			for(int i=0;i<indexsIn.size();i++){
			    		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIn");
			    	}
	    			for(int i=0; i< indexsIn.size();i++){
			    		//-reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
	    				reifConstIn[i] = solver.eq(VarModelBool[indexsIn.get(i)], 1);
			        	
			        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
			    	}
			    	solver.add(solver.le(solver.sum(reifVarModelIn),1));
			    	//-solver.add(solver.ifThenElse(solver.eq(solver.sum(reifVarModelIn),1), solver.gt(VarModel[indexsOut.get(0)],0), solver.eq(VarModel[indexsOut.get(0)],0)));
			    	solver.add(solver.ifThenElse(solver.eq(solver.sum(reifVarModelIn),1), solver.eq(VarModelBool[indexsOut.get(0)],1), solver.eq(VarModelBool[indexsOut.get(0)],0)));
			    	//-solver.add(solver.ifThenElse(solver.gt(VarModel[indexsOut.get(0)],0), solver.eq(solver.sum(reifVarModelIn),1), solver.eq(solver.sum(reifVarModelIn),0)));
			    	solver.add(solver.ifThenElse(solver.eq(VarModelBool[indexsOut.get(0)],1), solver.eq(solver.sum(reifVarModelIn),1), solver.eq(solver.sum(reifVarModelIn),0)));
    			}
    		}else{
		    	for(int i=0;i<indexsIn.size();i++){
		    		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIn");
		    	}
		    	
		    	for(int i=0;i<indexsOut.size();i++){
		    		reifVarModelOut[i] = solver.intVar(0, 1, indexsOut.get(i)+"reifModelOut");
		    	}
		    	
		    	for(int i=0; i< indexsIn.size();i++){
		    		//-reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
		    		reifConstIn[i] = solver.eq(VarModelBool[indexsIn.get(i)], 1);
		        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
		    	}
		    	solver.add(solver.le(solver.sum(reifVarModelIn),1));
		    	
	 	    	
		    	for(int i=0; i< indexsOut.size();i++){
		    		//-reifConstOut[i] = solver.neq(VarModel[indexsOut.get(i)], 0);
		    		reifConstOut[i] = solver.eq(VarModelBool[indexsOut.get(i)], 1);
		        	
		        	solver.add(solver.eq(reifConstOut[i], reifVarModelOut[i]));
		    	}
		    	solver.add(solver.le(solver.sum(reifVarModelOut),1));
		    		    	
		    	solver.add(solver.eq(solver.sum(reifVarModelOut),solver.sum(reifVarModelIn)));
	    		}
    	}
    	
    }
    
    
    
    //place with no output arcs)
    public void endPlace(PlacePetriNet place)  throws IloException{
    	List<Integer> indexsIn=indexOfActivitiesListInVarModel(place.getInputs());
    	//for floyd
    	endTransition = indexsIn.get(0);
    	
    	if(indexsIn.size()==1){
    		//-solver.add(solver.neq(0, VarModel[indexsIn.get(0)]));
    		solver.add(solver.eq(1, VarModelBool[indexsIn.get(0)]));
    		//System.out.println("solver.add(solver.eq(1, "+VarModelBool[indexsIn.get(0)]+"));");
    	}else{
	    	IlcConstraint[] reifConstIn = new IlcConstraint[indexsIn.size()];
	    	IlcIntVar[] reifVarModelIn = new IlcIntVar[indexsIn.size()];
	    	
	    	
	    	for(int i=0;i<indexsIn.size();i++){
	    		reifVarModelIn[i] = solver.intVar(0, 1, indexsIn.get(i)+"reifModelIn");
	    	}
	    	for(int i=0; i< indexsIn.size();i++){
	    		//-reifConstIn[i] = solver.neq(VarModel[indexsIn.get(i)], 0);
	    		reifConstIn[i] = solver.eq(VarModelBool[indexsIn.get(i)], 1);
	        	
	        	solver.add(solver.eq(reifConstIn[i], reifVarModelIn[i]));
	    	}
	    	
	    	
	    	solver.add(solver.eq(solver.sum(reifVarModelIn), 1));
    	}
    }
    
    
    //Every activity ai appearing in the case (from the event log) to check, but
    //not in the model, is included in Var-Model, with the constraint:
    public void activityInLogNotInModel(){
    	List<Integer> indexs=indexOfActivitiesListInVarModel(activityInlogNotInModel);
    	
    	/*
    	 * Var-Model[i]=0
    	 */
    	for (Integer i : indexs) {
			//-solver.add(solver.eq(VarModel[i], 0));
    		solver.add(solver.eq(VarModelBool[i], 0));
		}
    }
    
    
    
    
	
	

    ////////////////
    /// EventLog ///
    ////////////////

	
	public void varLogConstrains() throws IloException{
		/*-
		List<Integer> indexs = indexOfActivitiesListInVarModel(logs);
		for (int i = 0; i < logs.size(); i++) {
			
				
			if(i==0){
				
				solver.add(solver.gt(VarLog[indexs.get(i)], 0));
				

			}
			else{
				solver.add(solver.gt(VarLog[indexs.get(i)], VarLog[indexs.get(i-1)]));


			}
			solver.add(solver.le(VarLog[indexs.get(i)], n-logs.size()+i));
		}
*/
	}

	public void activityInModelNotInLog(){
		
		/*-
		for (int i = 0; i < model.size(); i++) {
			
			if(!activityInLog(model.get(i).getId())){
				
				//System.out.println("--------------------------------------------->"+  count+ " " + model.get(i).getId() + " i " + i);
				solver.add(solver.eq(VarLog[i], 0));

			}
		}
*/
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

	/*-	
		for (List<String> list : duplicatedListList) {
			if(!list.isEmpty()){
				//System.out.println("--------------------------------------------->No debe de mostrar esto  111");
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
					//-solver.add(solver.eq(VarModel[indexOfList.get(i)], 0));
					solver.add(solver.eq(VarModelBool[indexOfList.get(i)], 0));
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
					
*/
//		System.out.println("================");

	}
	
	
	
	///////////////////
	/// CSV Methods ///
	///////////////////
	
	 public IlcSolver createConstrains()  throws IloException{
		 Integer contadorDePlacesXor=0;
		varLogConstrains();
		activityInLogNotInModel();
		activityInModelNotInLog();
		activitiesAppearsMoreThanOnceInLog();
		
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
		/*	
		for(int i=0; i<model.size(); i++){
			 for(int j=0; j<model.size(); j++){
				 if(i!=j){
					 
					solver.add(solver.or(solver.eq(VarModel[i],0),solver.neq(VarModel[i], VarModel[j]) ));
					 					
					//-solver.add(solver.imply(solver.neq(VarLog[i],0),solver.neq(VarLog[i], VarLog[j]) ));
					 
					//two different activities cannot be executed at the same time
					 
					 //-solver.add(solver.imply(solver.neq(VarModel[i],0),solver.neq(VarModel[i], VarLog[j]) ));
				 }
			 }
			 //solver.add(solver.or(solver.eq(VarLog[i],VarModel[i]),solver.or(solver.eq(0,VarLog[i]), solver.eq(0,VarModel[i])) ));
		 }
		*/
		 //-for (int i = 0; i < model.size(); i++) {
		//-	solver.add(solver.ifThenElse(solver.eq(VarLog[i], VarModel[i]), solver.eq(VarDifference[i], 0), solver.ifThenElse(solver.and(solver.neq(0, VarLog[i]),solver.neq(0, VarModel[i])), solver.eq(VarDifference[i], 2), solver.eq(VarDifference[i], 1))));
			 
			//solver.add(solver.imply(solver.eq(VarLog[i], VarModel[i]), solver.eq(VarDifference[i], 0)));
		/*	
			//iguales y distintos de 0
			solver.add(solver.imply(solver.and(solver.eq(VarLog[i], VarModel[i]), solver.neq(VarLog[i], 0)), solver.eq(CountDifferenceNum[i], 1)));
			//iguales e iguales a 0
			solver.add(solver.imply(solver.and(solver.eq(VarLog[i], VarModel[i]), solver.eq(VarLog[i], 0)), solver.eq(CountDifferenceNum[i], 0)));
			//distintos y los dos distintos de 0
			solver.add(solver.imply(solver.and(solver.neq(VarLog[i], VarModel[i]), solver.and(solver.neq(VarLog[i], 0), solver.neq(VarModel[i], 0))), solver.eq(CountDifferenceNum[i], 2)));
			//distintos y s�lo 1 distinto de 0
			solver.add(solver.imply(solver.and(solver.neq(VarLog[i], VarModel[i]), solver.xor(solver.neq(VarLog[i], 0), solver.neq(VarModel[i], 0))), solver.eq(CountDifferenceNum[i], 1)));
			
			
			solver.add(solver.le(VarModel[i], sumCount));
			 solver.add(solver.le(VarLog[i], sumCount));
			*/
		 //-}
		 
		 //solver.add(solver.eq(solver.sum(CountDifferenceNum),sumCount)); 
		 //-solver.add(solver.eq(solver.sum(VarDifference),VarAlignment));
		 solver.add(solver.eq(solver.sum(VarModelBool),VarAlignment));
		 
		 
		 	
	     solver.add(solver.maximize(VarAlignment));

	     return solver;
	 }
	 
	 

	 
	 
	
	public void search( FileWriter fichero) throws IloException{
        System.out.println("------SEARCH INFO-------");

        
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        
        //IlcGoal g = solver.generate(VarAlignment);
        
        //solver.newSearch(g);
        
		solver.newSearch();
        
        System.out.print("Model[");
        for(TransitionPetriNet t: model){
        	System.out.print(t.getId()+",");
        }System.out.println("]");
        System.out.println("Logs"+logs);
        System.out.println("ActivitiesNotInModel"+activityInlogNotInModel);
        System.out.println("DuplicatedListList "+duplicatedListList);


        //-System.out.println("VarModelLogDifference Length: "+VarModel.length);
        System.out.println("VarModelLogDifference Length: "+VarModelBool.length);
        System.out.println("Log length: "+ logs.size());
        //System.out.println("VarDifference length: "+VarDifference.length);
        System.out.println("VarAlignment: "+VarAlignment);
        
        
        System.out.println("------SEARCH-------");
        Integer iter=1;
        String ultimaIteracion="";
        while(solver.next()){
	      System.out.println("=====NEXT=====");
	     ultimaIteracion="\n";
	        for(int i=0;i<model.size();i++){
	        	
	//        	System.out.println(((IlcIntVar)VarModelBool[i]).getName()+ " " + VarModelBool[i]+ " :" + VarModel[i]);	
	        	//-System.out.println("valor del total " + ((IlcIntVar)VarDifference[i]).getName() +" :" + VarDifference[i]);	

	       }
	       
//	
//	        System.out.println("Total " + ((IlcIntVar)VarAlignment).getName() +" :" + VarAlignment);
//        
	        System.out.println("iter : " + iter +  " N�mero de transacciones ejecutadas:" +  VarAlignment);
	        
	        
	        
	    for(int i=0;i<model.size();i++){
        	//-ultimaIteraci�n=ultimaIteraci�n+"\n"+((IlcIntVar)VarModelBool[i]).getName()+ " " + VarModelBool[i]+ " :" + VarLog[i];	
        	//-ultimaIteraci�n=ultimaIteraci�n+"\n"+"valor del total " + ((IlcIntVar)VarDifference[i]).getName() +" :" + VarDifference[i];	

        }

	    ultimaIteracion=ultimaIteracion+"\n"+"Total " + ((IlcIntVar)VarAlignment).getName() +" :" + VarAlignment;
    
	    ultimaIteracion=ultimaIteracion+"\n"+"iter : " + iter;

	        iter++;
	        
	        
	        time_end = System.currentTimeMillis();
	        ultimaIteracion=ultimaIteracion+"\n"+"Tiempo de ejecuci�n Durante la b�squeda:  "+((double)time_end-(double)time_start)/1000;
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