package es.us.idea.runs;

import PetriNet.LPO.PetriNetLpo;
import es.idea.pnml.Pnml;
import es.idea.xes.XesUtils;
import org.deckfour.xes.model.XTrace;

import java.io.*;
import java.util.*;


// This class creates tables counting repetitions. Just for local
public class RunCreateTables {
    public static void main(String[] args) throws Exception {


        PrintStream pr = new PrintStream(new File(ConfigParameters.outputFileName + "tables.csv"));
        System.setOut(pr);

        //********
        //M1 - M1
        //********
        System.out.println();
        System.out.println("**M1**");
        System.out.println();
        List<RunProblem.LPOContainer> lpoFiles = new ArrayList<>();
        String runPath = RunCreateTables.class.getResource("/partialModels/M1/").getPath(); //just finding the variable

        for (File f1 : new File(runPath).listFiles()) {
            lpoFiles.add(new RunProblem.LPOContainer(new PetriNetLpo("/partialModels/M1/" + f1.getName()), f1.getName()));
        }

        //We initialize the problem, load the logfile into a list
        List<XTrace> tracesI = XesUtils.getXLog("/xes/M1.xes");

        //LPOS
        System.out.println("LPO,Length,Nb repeated activities,Total repetitions");
        for(RunProblem.LPOContainer lpo : lpoFiles){
            String print = lpo.lpoFileName+","+lpo.lpo.getEventLpo().size();
            //reps
            Map<String, Integer> reps = populateModelVariables(lpo.lpo);
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        System.out.println();

        //Traces
        System.out.println("Trace,Length,Nb repeated activities,Total repetitions");
        for(XTrace t : tracesI){
            String print = t.getAttributes().get("concept:name").toString()+","+t.size();
            Map<String, Integer> reps = populateLogVariables(XesUtils.getRawLog(t));
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        //********
        //M2 - M2
        //********

        System.out.println();
        System.out.println("**M2**");
        System.out.println();
        lpoFiles = new ArrayList<>();
        runPath = RunCreateTables.class.getResource("/partialModels/M2/").getPath(); //just finding the variable

        for (File f1 : new File(runPath).listFiles()) {
            lpoFiles.add(new RunProblem.LPOContainer(new PetriNetLpo("/partialModels/M2/" + f1.getName()), f1.getName()));
        }

        //We initialize the problem, load the logfile into a list
        tracesI = XesUtils.getXLog("/xes/M2.xes");

        //LPOS
        System.out.println("LPO,Length,Nb repeated activities,Total repetitions");
        for(RunProblem.LPOContainer lpo : lpoFiles){
            String print = lpo.lpoFileName+","+lpo.lpo.getEventLpo().size();
            //reps
            Map<String, Integer> reps = populateModelVariables(lpo.lpo);
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        System.out.println();

        //Traces
        System.out.println("Trace,Length,Nb repeated activities,Total repetitions");
        for(XTrace t : tracesI){
            String print = t.getAttributes().get("concept:name").toString()+","+t.size();
            Map<String, Integer> reps = populateLogVariables(XesUtils.getRawLog(t));
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        //********
        //M5 - M5
        //********
        System.out.println();
        System.out.println("**M5**");
        System.out.println();
        lpoFiles = new ArrayList<>();
        runPath = RunCreateTables.class.getResource("/partialModels/M5/").getPath(); //just finding the variable

        for (File f1 : new File(runPath).listFiles()) {
            lpoFiles.add(new RunProblem.LPOContainer(new PetriNetLpo("/partialModels/M5/" + f1.getName()), f1.getName()));
        }

        //We initialize the problem, load the logfile into a list
        tracesI = XesUtils.getXLog("/xes/runtests/M5.xes");

        //LPOS
        System.out.println("LPO,Length,Nb repeated activities,Total repetitions");
        for(RunProblem.LPOContainer lpo : lpoFiles){
            String print = lpo.lpoFileName+","+lpo.lpo.getEventLpo().size();
            //reps
            Map<String, Integer> reps = populateModelVariables(lpo.lpo);
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        System.out.println();

        //Traces
        System.out.println("Trace,Length,Nb repeated activities,Total repetitions");
        for(XTrace t : tracesI){
            String print = t.getAttributes().get("concept:name").toString()+","+t.size();
            Map<String, Integer> reps = populateLogVariables(XesUtils.getRawLog(t));
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        //********
        //M8 - M8
        //********
        System.out.println();
        System.out.println("**M8**");
        System.out.println();
        lpoFiles = new ArrayList<>();
        runPath = RunCreateTables.class.getResource("/partialModels/M8/").getPath(); //just finding the variable

        for (File f1 : new File(runPath).listFiles()) {
            lpoFiles.add(new RunProblem.LPOContainer(new PetriNetLpo("/partialModels/M8/" + f1.getName()), f1.getName()));
        }

        //We initialize the problem, load the logfile into a list
        tracesI = XesUtils.getXLog("/xes/runtests/M8.xes");

        //LPOS
        System.out.println("LPO,Length,Nb repeated activities,Total repetitions");
        for(RunProblem.LPOContainer lpo : lpoFiles){
            String print = lpo.lpoFileName+","+lpo.lpo.getEventLpo().size();
            //reps
            Map<String, Integer> reps = populateModelVariables(lpo.lpo);
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }

        System.out.println();

        //Traces
        System.out.println("Trace,Length,Nb repeated activities,Total repetitions");
        for(XTrace t : tracesI){
            String print = t.getAttributes().get("concept:name").toString()+","+t.size();
            Map<String, Integer> reps = populateLogVariables(XesUtils.getRawLog(t));
            int nRepeated = 0;
            int nRepetitions = 0;
            for(String s : reps.keySet()){
                if(reps.get(s) > 1){
                    nRepeated++;
                    nRepetitions+= reps.get(s);
                }
            }

            print += "," + nRepeated + "," + nRepetitions;
            System.out.println(print);
        }


        pr.close();
    }


    private static Map<String, Integer> populateLogVariables(List<String> acts) {
        Map<String, Integer> reps = new HashMap<>();
        for (String s : acts) {
            if (reps.keySet().contains(s)) {
                reps.put(s, reps.get(s) + 1);
            } else {
                reps.put(s, 1);
            }
        }
        return reps;
    }

    private static Map<String, Integer> populateModelVariables(PetriNetLpo lpoFile) {
        Map<String, Integer> reps = new HashMap<>();
        for (Pnml.Net.Transition t : lpoFile.getTransitions()) {
            if (reps.keySet().contains(t.getName().getText())) {
                reps.put(t.getName().getText(), reps.get(t.getName().getText()) + 1);
            } else {
                reps.put(t.getName().getText(), 1);
            }
        }
        return reps;
    }

    private static Map<String, Integer> processCombination(Map<String, Integer> repeatedActivityModel, Map<String, Integer> repeatedActivityInstance) {
        Set<String> activitiesModel = repeatedActivityModel.keySet();
        Set<String> activitiesLog = repeatedActivityInstance.keySet();

        Map<String, Integer> repeatedFinal = new HashMap<>();
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
        return repeatedFinal;
    }


}