package es.us.idea.pnml;
import java.util.*;

import ilog.concert.IloException;
import ilog.solver.*;

public class UnfoldingRuns1 {
	public static void main(String[] args) {
		try {
    	
        IlcSolver solver = new IlcSolver();
        
        //el gasto para cada uno de los meses
        List<String> l = new ArrayList();
        l.add("a");//0
        l.add("b");//1
        l.add("c");//2
        l.add("d1");//3
        l.add("d2");//4
        l.add("d3");//5
        l.add("k");//6
        l.add("x1");//7
        l.add("x2");//8
        l.add("x3");//9
        l.add("x4");//10
        
        IlcIntVar[] VarModel = new IlcIntVar[l.size()];
        IlcIntVar[] VarLog = new IlcIntVar[l.size()];
        IlcIntVar[] Darray = new IlcIntVar[3];
        IlcIntVar[] Xarray = new IlcIntVar[4];
        
        IlcIntVar[] VarDiff = new IlcIntVar[l.size()];
        IlcIntVar VarAligment = solver.intVar(0, l.size()*2, "SumTotal");
        for(int i=0;i<l.size();i++){
        	VarModel[i] = solver.intVar(0, l.size()-1, l.get(i)+"Model");//se puede mejorar el dominio
        	VarLog[i] = solver.intVar(0, l.size()-1, l.get(i)+"VarLog");//se puede mejorar el dominio
        	VarDiff[i] = solver.intVar(0, l.size()-1, l.get(i)+"VarDiff");//se puede mejorar el dominio
        }
        for(int i=0; i<Darray.length;i++){
        	Darray[i] = solver.intVar(0, l.size()-1, l.get(i)+"Darray");//se puede mejorar el dominio
        }
        for(int i=0; i<Xarray.length;i++){
        	Xarray[i] = solver.intVar(0, l.size()-1, l.get(i)+"Xarray");//se puede mejorar el dominio
        }
            
        
        
      //create the model according to the Petri Net model
        //a or k must be executed
        
        solver.add(solver.xor(solver.eq(VarModel[0],0),solver.eq(VarModel[6],0)));
        //solver.add(solver.xor(solver.eq(VarModel[0],1),solver.eq(VarModel[6],1)));
        /*
        //x1 or x3 must be exebuted
        solver.add(solver.xor(solver.eq(VarModel[7],0),solver.eq(VarModel[9],0)));
        
        //x2 or x4 must be exebuted
        solver.add(solver.xor(solver.eq(VarModel[8],0),solver.eq(VarModel[10],0)));
        */
        //k->x1
        solver.add(solver.ifThenElse(solver.gt(VarModel[6], 0), solver.gt(VarModel[7], VarModel[6]), solver.eq(VarModel[7], 0)));
        
        //x1->x2
        solver.add(solver.ifThenElse(solver.gt(VarModel[7], 0), solver.gt(VarModel[8], VarModel[7]), solver.eq(VarModel[8], 0)));
        
        //a->b
        solver.add(solver.ifThenElse(solver.gt(VarModel[0], 0), solver.gt(VarModel[1], VarModel[0]), solver.eq(VarModel[1], 0)));
        
        //b->c
        solver.add(solver.ifThenElse(solver.gt(VarModel[1], 0), solver.gt(VarModel[2], VarModel[1]), solver.eq(VarModel[2], 0)));
        
        //c->(d1 xor d2 xor d3)
        solver.add(solver.ifThenElse(solver.gt(VarModel[2], 0), solver.xor(solver.gt(VarModel[3],VarModel[2]),solver.xor(solver.gt(VarModel[4],VarModel[2]), solver.gt(VarModel[5],VarModel[2]))), solver.and(solver.eq(VarModel[3],0),solver.and(solver.eq(VarModel[4],0), solver.eq(VarModel[5],0)))));
        solver.add(solver.xor(solver.eq(VarModel[3], 0), solver.gt(VarModel[3], VarModel[2])));
        solver.add(solver.xor(solver.eq(VarModel[4], 0), solver.gt(VarModel[4], VarModel[2])));
        solver.add(solver.xor(solver.eq(VarModel[5], 0), solver.gt(VarModel[5], VarModel[2])));
        
        //a->(x3,d1)
        solver.add(solver.ifThenElse(solver.gt(VarModel[0], 0), solver.xor(solver.eq(VarModel[9],0),solver.eq(VarModel[3],0)), solver.and(solver.eq(VarModel[3], 0), solver.eq(VarModel[9], 0))));
        solver.add(solver.xor(solver.eq(VarModel[3], 0), solver.gt(VarModel[3], VarModel[0])));
        solver.add(solver.xor(solver.eq(VarModel[9], 0), solver.gt(VarModel[9], VarModel[0])));
        
      //x3->(x4,d2)
        solver.add(solver.ifThenElse(solver.gt(VarModel[9], 0), solver.xor(solver.eq(VarModel[10],0),solver.eq(VarModel[4],0)), solver.and(solver.eq(VarModel[10], 0), solver.eq(VarModel[4], 0))));
        solver.add(solver.xor(solver.eq(VarModel[10], 0), solver.gt(VarModel[10], VarModel[9])));
        solver.add(solver.xor(solver.eq(VarModel[4], 0), solver.gt(VarModel[4], VarModel[9])));
        
      //x4->d3
        solver.add(solver.ifThenElse(solver.gt(VarModel[10], 0), solver.gt(VarModel[5], VarModel[10]), solver.eq(VarModel[5], 0)));
        
        
        
        /****Introduce de log****/
        
        //A B X X C D ... or  A B X K X C D ... or  A B X X C X D
        //A B X X C D 
        /*
        solver.add(solver.lt(0,VarLog[0]));
        solver.add(solver.lt(VarLog[0], VarLog[1]));
        solver.add(solver.lt(VarLog[1], Xarray[0]));
        solver.add(solver.lt(Xarray[0], Xarray[1]));
        solver.add(solver.lt(Xarray[1], VarLog[2]));
        solver.add(solver.lt(VarLog[2], Darray[0]));
        
        solver.add(solver.eq(VarLog[6], 0));
        
        solver.add(solver.eq(Xarray[2], 0));
        solver.add(solver.eq(Xarray[3], 0));
        solver.add(solver.eq(Darray[1], 0));
        solver.add(solver.eq(Darray[2], 0));
        
        */
        //A B X K X C D 
        /*
        solver.add(solver.lt(0,VarLog[0]));
        solver.add(solver.lt(VarLog[0], VarLog[1]));
        solver.add(solver.lt(VarLog[1], Xarray[0]));
        solver.add(solver.lt(Xarray[0], VarLog[6]));
        solver.add(solver.lt(VarLog[6], Xarray[1]));
        solver.add(solver.lt(Xarray[1], VarLog[2]));
        solver.add(solver.lt(VarLog[2], Darray[0]));
        
        
      //For no executed	
        
        
        solver.add(solver.eq(Xarray[2], 0));
        solver.add(solver.eq(Xarray[3], 0));
        solver.add(solver.eq(Darray[1], 0));
        solver.add(solver.eq(Darray[2], 0));
        
        */
      //A B X X C X D 
        
        solver.add(solver.lt(0,VarLog[0]));
        solver.add(solver.lt(VarLog[0], VarLog[1]));
        solver.add(solver.lt(VarLog[1], Xarray[0]));
        solver.add(solver.lt(Xarray[0], Xarray[1]));
        solver.add(solver.lt(Xarray[1], VarLog[2]));
        solver.add(solver.lt(VarLog[2], Xarray[2]));
        solver.add(solver.lt(Xarray[2], Darray[0]));
        
        
        //los que no se ejecutan
        solver.add(solver.eq(VarLog[6], 0));
        
        
        solver.add(solver.eq(Xarray[3], 0));
        solver.add(solver.eq(Darray[1], 0));
        solver.add(solver.eq(Darray[2], 0));
        
        
        //for the reapeted labels
        ///for X
        solver.add(solver.or(solver.eq(VarLog[7],Xarray[0]), 
        		solver.or(solver.eq(VarLog[7],Xarray[1]), 
        				solver.or(solver.eq(VarLog[7],Xarray[2]), solver.eq(VarLog[7],Xarray[3])))));
        solver.add(solver.or(solver.eq(VarLog[8],Xarray[0]), 
        		solver.or(solver.eq(VarLog[8],Xarray[1]), 
        				solver.or(solver.eq(VarLog[8],Xarray[2]), solver.eq(VarLog[8],Xarray[3])))));
        solver.add(solver.or(solver.eq(VarLog[9],Xarray[0]), 
        		solver.or(solver.eq(VarLog[9],Xarray[1]), 
        				solver.or(solver.eq(VarLog[9],Xarray[2]), solver.eq(VarLog[9],Xarray[3])))));
        solver.add(solver.or(solver.eq(VarLog[10],Xarray[0]), 
        		solver.or(solver.eq(VarLog[10],Xarray[1]), 
        				solver.or(solver.eq(VarLog[10],Xarray[2]), solver.eq(VarLog[10],Xarray[3])))));
        
        solver.add(solver.or(solver.eq(Xarray[0], VarLog[7]), 
        		solver.or(solver.eq(Xarray[0], VarLog[8]), 
        				solver.or(solver.eq(Xarray[0], VarLog[9]), solver.eq(Xarray[0], VarLog[10])))));
        solver.add(solver.or(solver.eq(Xarray[1], VarLog[7]), 
        		solver.or(solver.eq(Xarray[1], VarLog[8]), 
        				solver.or(solver.eq(Xarray[1], VarLog[9]), solver.eq(Xarray[1], VarLog[10])))));
        solver.add(solver.or(solver.eq(Xarray[2], VarLog[7]), 
        		solver.or(solver.eq(Xarray[2], VarLog[8]), 
        				solver.or(solver.eq(Xarray[2], VarLog[9]), solver.eq(Xarray[2], VarLog[10])))));
        solver.add(solver.or(solver.eq(Xarray[3], VarLog[7]), 
        		solver.or(solver.eq(Xarray[3], VarLog[8]), 
        				solver.or(solver.eq(Xarray[3], VarLog[9]), solver.eq(Xarray[3], VarLog[10])))));
       
        ///FOR D
        solver.add(solver.or(solver.eq(VarLog[3],Darray[0]), 
        		solver.or(solver.eq(VarLog[3],Darray[1]),solver.eq(VarLog[3],Darray[2]))));
        solver.add(solver.or(solver.eq(VarLog[4],Darray[0]), 
        		solver.or(solver.eq(VarLog[4],Darray[1]),solver.eq(VarLog[4],Darray[2]))));
        solver.add(solver.or(solver.eq(VarLog[5],Darray[0]), 
        		solver.or(solver.eq(VarLog[5],Darray[1]),solver.eq(VarLog[5],Darray[2]))));
        
        
        solver.add(solver.or(solver.eq(Darray[0], VarLog[3]), 
        		solver.or(solver.eq(Darray[0], VarLog[4]),solver.eq(Darray[0], VarLog[5]))));
        solver.add(solver.or(solver.eq(Darray[1], VarLog[3]), 
        		solver.or(solver.eq(Darray[1], VarLog[4]),solver.eq(Darray[1], VarLog[5]))));
        solver.add(solver.or(solver.eq(Darray[2], VarLog[3]), 
        		solver.or(solver.eq(Darray[2], VarLog[4]),solver.eq(Darray[2], VarLog[5]))));
        

        
        
        
        
        /******minimize difference*****/
        
        for(int i=0;i<l.size();i++){
        	solver.add(solver.ifThenElse(solver.eq(VarModel[i], VarLog[i]), solver.eq(VarDiff[i],0), solver.ifThenElse(solver.or(solver.eq(VarModel[i], 0), solver.eq(VarLog[i], 0)), solver.eq(VarDiff[i],1), solver.eq(VarDiff[i],2))));
        }
        solver.add(solver.eq(solver.sum(VarDiff),VarAligment));
        //minimize the difference between the log and the model
        solver.add(solver.minimize(VarAligment));
        
        
        /***to avoid create equivalent results**/
        
        for (int i=0;i<l.size()-1;i++){
        	for (int j=i+1;j<l.size();j++){
            	solver.add(solver.or(solver.neq(VarModel[i], VarModel[j]), solver.or(solver.eq(VarModel[i], 0), solver.eq(VarModel[j], 0))));
            	solver.add(solver.or(solver.neq(VarLog[i], VarLog[j]), solver.or(solver.eq(VarLog[i], 0), solver.eq(VarLog[j], 0))));
            }
        }
        
        IlcConstraint[] valuesAsigned = new IlcConstraint[l.size()];
        IlcIntVar[] reifVarModel = new IlcIntVar[l.size()];
    	
    	for(int i=0;i<l.size();i++){
    		reifVarModel[i] = solver.intVar(0, 1, i+"reifModel");
    	}
        for (int i=1;i<l.size();i++){
         valuesAsigned[i] = solver.or(solver.eq(VarModel[0], i), solver.eq(VarLog[0], i));
         for(int j=1;j<l.size();j++){
        	 valuesAsigned[i] = solver.or(valuesAsigned[i], solver.or(solver.eq(VarModel[j], i), solver.eq(VarLog[j], i)));
         }
         solver.add(solver.eq(valuesAsigned[i], reifVarModel[i]));
        }
        for (int i=2;i<l.size();i++){
        	solver.add(solver.imply(solver.eq(1, reifVarModel[i]), solver.eq(1, reifVarModel[i-1])));
        }
      
        solver.newSearch();
        while(solver.next()){
/*        		
							//System.out.println(((IlcIntVar)u).getDomainValue());			
        }
        
        solver.restartSearch();
        solver.next();	
  */    
        	
        for(int i=0;i<VarModel.length;i++){
        	System.out.println(((IlcIntVar)VarModel[i]).getName()+ " " + VarModel[i]);	
        	System.out.println(((IlcIntVar)VarModel[i]).getName()+ " " + VarLog[i]);
        }
        System.out.println(((IlcIntVar)VarAligment).getName()+ " " + VarAligment);
        System.out.println(((IlcIntVar)Xarray[0]).getName()+ " " + Xarray[0] + " " + ((IlcIntVar)Xarray[1]).getName()+ " " + Xarray[1] + " " + ((IlcIntVar)Xarray[2]).getName()+ " " + Xarray[2] + " " + ((IlcIntVar)Xarray[3]).getName()+ " " + Xarray[3]);        
        System.out.println(((IlcIntVar)Darray[0]).getName()+ " " + Darray[0] + " " + ((IlcIntVar)Darray[1]).getName()+ " " + Darray[1] + " " + ((IlcIntVar)Darray[2]).getName()+ " " + Darray[2]);
        /*
        for(int i=0;i<reifVarModel.length;i++){
        	System.out.println(((IlcIntVar)reifVarModel[i]).getName()+ " " + reifVarModel[i]);	
        }*/
        System.out.println("================");
        }
        System.out.println("================");
        
        solver.printInformation();
        
        solver.endSearch();

    } catch (IloException error) {
        System.out.println("An error occurred : "+error);
    }               
}
}
