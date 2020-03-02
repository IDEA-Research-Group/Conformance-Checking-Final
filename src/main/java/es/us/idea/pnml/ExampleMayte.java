package es.us.idea.pnml;
import java.util.*;

import ilog.concert.IloException;
import ilog.solver.*;

public class ExampleMayte {
	public static void main(String[] args) {
		try {
    	
        IlcSolver solver = new IlcSolver();
        
        //el gasto para cada uno de los meses
        List<String> l = new ArrayList();
        l.add("a");
        l.add("b");
        l.add("c");
        l.add("d");
        l.add("e");
        l.add("g");
        l.add("h");
        l.add("m");
        
        
        IlcIntVar[] VarModel = new IlcIntVar[l.size()];
        IlcIntVar[] VarLog = new IlcIntVar[l.size()];
        
        IlcIntVar[] Total = new IlcIntVar[l.size()];
        IlcIntVar SumTotal = solver.intVar(0, l.size()*2, "SumTotal");
        
        for(int i=0;i<l.size();i++){
        	VarModel[i] = solver.intVar(0, l.size(), l.get(i)+"Model");//se puede mejorar el dominio
        	VarLog[i] = solver.intVar(0, l.size(), l.get(i)+"Log");//se puede mejorar el dominio
        	Total[i] = solver.intVar(0, l.size(), l.get(i)+"Total");//se puede mejorar el dominio
        }
            
        
        
      //create the model according to the Petri Net model
        //a<d
        
        solver.add(solver.lt(VarModel[0],VarModel[3]));
        //a<>0
        solver.add(solver.neq(0, VarModel[0]));
        //b and c are 0 or greater than a
        solver.add(solver.xor(solver.lt(VarModel[0], VarModel[1]), solver.eq(VarModel[1], 0)));
        solver.add(solver.xor(solver.lt(VarModel[0], VarModel[2]), solver.eq(VarModel[2], 0)));
        //b parallel to c
        solver.add(solver.xor(solver.eq(0, VarModel[1]), solver.eq(0, VarModel[2])));
        //d<e
        solver.add(solver.lt(VarModel[3], VarModel[4]));
        //b,c<e
        solver.add(solver.lt(VarModel[1], VarModel[4]));
        solver.add(solver.lt(VarModel[2], VarModel[4]));
        //g and h are 0 or greater than e
        solver.add(solver.xor(solver.lt(VarModel[4], VarModel[5]), solver.eq(VarModel[5], 0)));
        solver.add(solver.xor(solver.lt(VarModel[4], VarModel[6]), solver.eq(VarModel[6], 0)));
        //g parallel to h
        solver.add(solver.xor(solver.eq(0, VarModel[5]), solver.eq(0, VarModel[6])));
        //m does not exist
        solver.add(solver.eq(VarModel[7], 0));
        /*1. log Model - correct trace*/
        /*
        //a d b e h
        //the first event
        solver.add(solver.lt(0,VarLog[0]));
        //the activities that do not appear
        //solver.add(solver.eq(VarLog[0], 0));
        // the events that appear
        solver.add(solver.lt(VarLog[0], VarLog[3]));
        solver.add(solver.lt(VarLog[3], VarLog[1]));
        solver.add(solver.lt(VarLog[1], VarLog[4]));
        solver.add(solver.lt(VarLog[4], VarLog[6]));
        */
        /*2. log Model - Incorrect trace-->there is an activity in the model that does not appear in the log*/
        /*
        //a d b - h
        //the first event
        solver.add(solver.lt(0,VarLog[0]));
        // the events that appear
        solver.add(solver.lt(VarLog[0], VarLog[3]));
        solver.add(solver.lt(VarLog[3], VarLog[1]));
        solver.add(solver.lt(VarLog[1], VarLog[6]));
      //the activities that do not appear
        solver.add(solver.eq(VarLog[2], 0));
        solver.add(solver.eq(VarLog[4], 0));
        solver.add(solver.eq(VarLog[5], 0));
        */
        /*3. log Model - Incorrect trace-->there is an activity in the log that does not appear in a correct trace of the model although it is in the model*/
        /*
        //a d c b e h
        //the first event
        solver.add(solver.lt(0,VarLog[0]));
        // the events that appear
        solver.add(solver.lt(VarLog[0], VarLog[3]));
        solver.add(solver.lt(VarLog[3], VarLog[2]));
        solver.add(solver.lt(VarLog[2], VarLog[1]));
        solver.add(solver.lt(VarLog[1], VarLog[4]));
        solver.add(solver.lt(VarLog[4], VarLog[6]));
        */
        /*4. log Model - Incorrect trace-->there is an activity in the log that does not appear in the model*/
        /*
        //a d c e 'm' h
        //hay que pone las variables tantos en el modelo como en el log, en el log se pone con la relación <, y en el modelo se le pone = 0??
        //the first event
        solver.add(solver.lt(0,VarLog[0]));
        // the events that appear
        solver.add(solver.lt(VarLog[0], VarLog[3]));
        solver.add(solver.lt(VarLog[3], VarLog[2]));
        solver.add(solver.lt(VarLog[2], VarLog[4]));
        solver.add(solver.lt(VarLog[4], VarLog[7]));
        solver.add(solver.lt(VarLog[7], VarLog[6]));
        */
        /*5. log Model - Incorrect trace-->there is an activity in the log that does not appear in the model*/
        
        //a d c e 'a' h
        //hay que pone las variables tantos en el modelo como en el log, en el log se pone con la relación <, y en el modelo se le pone = 0??
        //the first event
        solver.add(solver.lt(0,VarLog[0]));
        // the events that appear
        solver.add(solver.lt(VarLog[0], VarLog[3]));
        solver.add(solver.lt(VarLog[3], VarLog[2]));
        solver.add(solver.lt(VarLog[2], VarLog[4]));
        solver.add(solver.lt(VarLog[4], VarLog[7]));
        solver.add(solver.lt(VarLog[7], VarLog[6]));
        
        
        for(int i=0;i<l.size();i++){
        	solver.add(solver.ifThenElse(solver.eq(VarModel[i], VarLog[i]), solver.eq(Total[i],0), solver.ifThenElse(solver.or(solver.eq(VarModel[i], 0), solver.eq(VarLog[i], 0)), solver.eq(Total[i],1), solver.eq(Total[i],2))));
        }
        solver.add(solver.eq(solver.sum(Total),SumTotal));
        //minimize the difference between the log and the model
        solver.add(solver.minimize(SumTotal));
        
        
        
        solver.newSearch();
        while(solver.next()){
/*        		
							//System.out.println(((IlcIntVar)u).getDomainValue());			
        }
        
        solver.restartSearch();
        solver.next();	
  */    
        	
        for(int i=0;i<VarModel.length;i++){
        	System.out.println(((IlcIntVar)VarModel[i]).getName()+ " " + VarModel[i]+ " :" + VarLog[i]);	
        }
        /*
        for(int i=0;i<VarModel.length;i++){
        	System.out.println("valor del Log" + ((IlcIntVar)VarLog[i]).getName() +" :" + VarLog[i]);	
        }*/
        for(int i=0;i<VarModel.length;i++){
        	System.out.println("valor del total" + ((IlcIntVar)Total[i]).getName() +" :" + Total[i]);	
        }
        System.out.println("Total " + ((IlcIntVar)SumTotal).getName() +" :" + SumTotal);
        
        
        }
        System.out.println("================");
        
        solver.printInformation();
        
        solver.endSearch();

    } catch (IloException error) {
        System.out.println("An error occurred : "+error);
    }               
}
}
