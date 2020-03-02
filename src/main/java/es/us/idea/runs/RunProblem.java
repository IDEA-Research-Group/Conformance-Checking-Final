package es.us.idea.runs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.spark.MongoConnector;
import com.mongodb.spark.MongoSpark;
import es.us.idea.maximumAlignment.CreateCSPAbnormality;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.HashPartitioner;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.catalog.Database;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StringType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.CollectionAccumulator;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.impl.*;
import org.deckfour.xes.model.*;
import PetriNet.LPO.PetriNetLpo;
import org.deckfour.xes.model.XTrace;
import es.idea.xes.XesUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.deckfour.xes.out.XesXmlSerializer;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;
import scala.Tuple4;
import sun.security.krb5.Config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class RunProblem {

    static PrintStream solverStream;

    //private static List<String> traces;
    private static List<XTrace> tracesI;
    private static List<Integer> tracesHashes;

    private static List<Integer> lpoHashes;

    private static List<LPOContainer> lpoFiles;

    private static List<String> checkLines;

    private static long globalTime;

    // Lista para volcar la informacion del debug en el fichero TODO
    private static List<String> debug;

    public static void main(String[] args) throws Exception {

        //Solver debug
        try{
            solverStream = new PrintStream(new File(ConfigParameters.outputFileName + "_1_searchStats.txt"));
        } catch(Exception e){
            System.out.println("Solver debug will be off");
        }

        // Check if remote configuration is enabled and if so, refresh the configuration
        if(ConfigParameters.REMOTE_CONFIGURATION)
            LoadConfigurationFromMongoDB.load();

        // No pasa si esta en modo distribuido
        if(ConfigParameters.CHECK_SOLUTIONS && ConfigParameters.LPOCUTS == 1 && !ConfigParameters.DISTRIBUTED_MODE){
            Path path = Paths.get("src/main/resources/"+ConfigParameters.CHECK_SOLUTIONS_FILE);
            if(Files.exists(path)) {
                try {
                    checkLines = Files.readAllLines(path);
                } catch(Exception e){
                    System.out.println("Checking will be off");
                }
            } else {
                System.out.println("Checking will be off");
            }
        }

	    //Starting Spark out
        SparkConf conf = new SparkConf()
                .setAppName("Conformance Checking");


        // añadir parametros relacionados con el modo distribuido al spark context

        if(!ConfigParameters.DISTRIBUTED_MODE){
            conf.setMaster("local[1]");
        } else {
            conf
                .set("spark.executor.memory", ConfigParameters.EXECUTORMEMORY)
                .set("spark.driver.memory", ConfigParameters.DRIVERMEMORY)
                .set("spark.mongodb.output.uri", ConfigParameters.DISTRIBUTEDOUTPUT + "/"+ConfigParameters.DISTRIBUTEDOUTPUTDB + "." + ConfigParameters.DISTRIBUTEDOUTPUTCOLLECTION);

            if(ConfigParameters.EXECUTORCORES != null)
                conf.set("spark.executor.cores", ConfigParameters.EXECUTORCORES);

            if(ConfigParameters.DRIVERCORES != null)
                conf.set("spark.driver.cores", ConfigParameters.DRIVERCORES);
        }
            JavaSparkContext sc = new JavaSparkContext(conf);

            Broadcast<Integer> timeoutBroadcast = sc.broadcast(ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS);

            //List<PetriNetLpo> lpoFiles;
            //List<XTrace> tracesI;
            JavaRDD<LPOContainer> sparkLpos;
            List<LPOContainer> sparkLposList;

            if(ConfigParameters.DISTRIBUTED_MODE){
                //Sustituye al codigo anterior comentado: elimina la coleccion de mongo antes de hacer nada
                MongoClient mc = new MongoClient(new MongoClientURI(ConfigParameters.DISTRIBUTEDOUTPUT));
                mc.getDatabase(ConfigParameters.DISTRIBUTEDOUTPUTDB).getCollection(ConfigParameters.DISTRIBUTEDOUTPUTCOLLECTION).drop();
                mc.close();

                sparkLpos = sc.wholeTextFiles(ConfigParameters.HDFSLPOS, ConfigParameters.MIN_PARTITIONS).map(pair ->
                        new LPOContainer(new PetriNetLpo(new ByteArrayInputStream(pair._2.getBytes())), pair._1)
                );

                long lpoPartitionSize = new Double(Math.ceil(new Double(sparkLpos.count()) / new Double(ConfigParameters.LPOCUTS))).longValue();

                JavaPairRDD<LPOContainer, Long> sparkLposWithIndex = sparkLpos.zipWithIndex();

                if(ConfigParameters.LPOTAKE > 0) {
                    sparkLposWithIndex = sparkLposWithIndex.filter(x -> x._2 > ConfigParameters.LPOTAKE);
                }

                String xesContent = sc.wholeTextFiles(ConfigParameters.HDFSXES, ConfigParameters.MIN_PARTITIONS).collect().get(0)._2;
                tracesI = XesUtils.getXLogFromStream(new ByteArrayInputStream(xesContent.getBytes()));
            } else {
                lpoFiles = new ArrayList<>();
                String runPath = RunProblem.class.getResource(ConfigParameters.LOCALLPOS).getPath(); //just finding the variable

                for (File f1 : new File(runPath).listFiles()){
                        lpoFiles.add(new LPOContainer(new PetriNetLpo(ConfigParameters.LOCALLPOS + f1.getName()), f1.getName()));
                }

                if(ConfigParameters.LPOTAKE > 0) {
                    lpoFiles = lpoFiles.subList(0, ConfigParameters.LPOTAKE);
                }

                sparkLpos = sc.parallelize(lpoFiles/*, ConfigParameters.LPOCUTS*/);

                tracesI = XesUtils.getXLog(ConfigParameters.LOCALXES);//.stream().filter(trace -> trace.getAttributes().get("concept:name").toString().equals("instance_91")).collect(Collectors.toList());

            }

        sparkLposList = sparkLpos.collect();

        // Traces take
        if(ConfigParameters.TRACETAKE > 0) {
            tracesI = tracesI.subList(0, ConfigParameters.TRACETAKE);
        }

        if(ConfigParameters.LPOTAKE > 0) {
            sparkLposList.subList(0, ConfigParameters.LPOCUTS);
        }


        //Since the xtrace is not serializable and cannot be made so since it's external
        //We make xlogs of just 1 trace, and serialize them in XML format to strings.
        Map<Integer, String> tracesToHashes = new HashMap<>();
        //traces = new ArrayList<>();
        tracesHashes = new ArrayList<>();

        // for(XTrace t : tracesI){
        //     String aux = RunProblem.serializeTrace(t);
        //     //Integer index = traces.size();
        //     //tracesToHashes.put(aux.hashCode(), aux);
        //     tracesToHashes.put(index, aux);
        //     //traces.add(aux);
        //     //tracesHashes.add(aux.hashCode());
        //     tracesHashes.add(index);
        // }

        for(int i =0; i<tracesI.size(); i++) {
            String aux = RunProblem.serializeTrace(tracesI.get(i));
            //Integer index = traces.size();
            //tracesToHashes.put(aux.hashCode(), aux);
            tracesToHashes.put(i, aux);
            //traces.add(aux);
            //tracesHashes.add(aux.hashCode());
            tracesHashes.add(i);
        }

        Map<Integer, LPOContainer> lposToHashes = new HashMap<>();
        lpoHashes = new ArrayList<>();

        for(int i =0; i<sparkLposList.size(); i++) {
            lposToHashes.put(i, sparkLposList.get(i));
            lpoHashes.add(i);
        }

        // System.out.println(tracesToHashes.keySet().size());

        // It's mandatory to declare the broadcast variable here. It cannot be a class property.
        Broadcast<Map<Integer, String>> tracesToHashesBroadCast = sc.broadcast(tracesToHashes);
        Broadcast<Map<Integer, LPOContainer>> lposToHashesBroadCast = sc.broadcast(lposToHashes);

        //Map<Integer,RunResults> solutions = new HashMap<>();

        JavaRDD<Integer> sparkTraces =  sc.parallelize(tracesHashes, ConfigParameters.TRACECUTS);
        JavaRDD<Integer> sparkLpoHashes = sc.parallelize(lpoHashes, ConfigParameters.LPOCUTS);

        //All possibilities between them to achieve all possible missalignments
        // Left: Trace, Right: LPO
        JavaPairRDD<Integer,Integer> cutProblem = sparkTraces.cartesian(sparkLpoHashes);
        // System.out.println(cutProblem.getNumPartitions());
        globalTime = System.currentTimeMillis();



        reduceJob(cutProblem, sc, tracesToHashesBroadCast, lposToHashesBroadCast, timeoutBroadcast);

        if(!ConfigParameters.DISTRIBUTED_MODE){
            globalTime = System.currentTimeMillis() - globalTime;
            solverStream.println("The examples took to process " + (float)globalTime/1000 +" seconds");
        }
    }

    private static void reduceJob(JavaPairRDD<Integer,Integer> cutProblem, JavaSparkContext sc, Broadcast<Map<Integer, String>> tracesToHashesBroadCast, Broadcast<Map<Integer, LPOContainer>> lposToHashesBroadCast, Broadcast<Integer> timeoutBroadcast) throws FileNotFoundException, UnsupportedEncodingException {
        //Logging
        LongAccumulator numSolvedProblems = sc.sc().longAccumulator("Number of Completed Problems");

        //And then we can work on multiple nodes. A map operation will make Maps of partial solutions.
        //Do not confuse the Map type with the map operation.
        JavaRDD<Tuple2<Integer, RunResults>> rdd = cutProblem.mapPartitions((iterator) -> {

            XesXmlParser parser = new XesXmlParser();
            List<Tuple4<Integer, LPOContainer, XTrace, List<String>>> estimations = new ArrayList<>();

            //To save valuable time
            Map<XTrace,Integer> savedSerializations = new HashMap<>();

            while (iterator.hasNext()){
                Tuple2<Integer,Integer> pair = iterator.next();
                String s = tracesToHashesBroadCast.value().get(pair._1);
                LPOContainer hashedLpo = lposToHashesBroadCast.getValue().get(pair._2);

                XTrace trace = parser.parse(new ByteArrayInputStream(s.getBytes())).get(0).get(0);

                if(!savedSerializations.containsKey(trace)){
                    savedSerializations.put(trace, pair._1); // pair._1 = s.hashCode()
                }

                PetriNetLpo net = hashedLpo.lpo;

                List<String> log = XesUtils.getLog(trace,net);

                int minMissAlignment = CreateCSPAbnormality.minMissAlignment(net,log);
                estimations.add(new Tuple4<>(minMissAlignment, hashedLpo,trace, log));

            }

            estimations.sort(Comparator.comparingInt(Tuple4::_1));

            //System.out.println("-----> -> ->Entra workgroup");


            Map<Integer,RunResults> partialSolution = new TreeMap<>(); // las claves siguen siendo la traza

            for (Tuple4<Integer, LPOContainer, XTrace, List<String>> e : estimations) {

                LPOContainer container = e._2();
                XTrace trace = e._3();

                String instanceName = trace.getAttributes().get("concept:name").toString();

                //System.out.println(instanceName);

                Integer previousMissAlignment;
                boolean newEntry = false;

                //If the trace exists there is already a missalignment around, we wanna use that
                //That was the whole point of doing this operation in partitions
                Boolean isTraceInPartialSolutions = partialSolution.keySet().contains(savedSerializations.get(trace));
                if(!isTraceInPartialSolutions){
                    newEntry = true;
                    previousMissAlignment = -1;
                } else {
                        previousMissAlignment = partialSolution.get(savedSerializations.get(trace)).bestMissAlignment;
                    //If we cannot improve for sure this example then just ignore it
                    if(e._1() >= previousMissAlignment && previousMissAlignment != -1){
                        if(ConfigParameters.SKIP_IF_IMPROVED)
                            break;
                        else
                            continue;
                    }
                }

                long currTime = System.nanoTime();
                Integer newMissAlignment;

                CreateCSPAbnormality csp = new CreateCSPAbnormality(container.lpo, e._4(), e._1(), previousMissAlignment);

                if(ConfigParameters.SOL){
                    System.out.println(LocalDateTime.now().toLocalTime() + " We are now processing problem: "  +container.lpoFileName + " - " + instanceName
                    + " with min = " + csp.minAlignment + " and max = " + csp.maxAlignment);
                }

                //Critical instruction here
                newMissAlignment = csp.runSearch(timeoutBroadcast.getValue());

                //Logging examples
                double totalTimeTaken = (double)(System.nanoTime() - currTime) / 1000000.0;

                if(ConfigParameters.SOL ){
                    System.out.println(LocalDateTime.now().toLocalTime() + " We just finished processing: " + container.lpoFileName + " - " + instanceName +
                            " and found a missalignment of " + newMissAlignment);
                }

                numSolvedProblems.add(1L);

                long time = 0;
                if(!csp.isFinishedInTime()){
                    time = csp.getLastSolutionTime();
                }

                if(newEntry || previousMissAlignment == -1 || (newMissAlignment < previousMissAlignment && newMissAlignment != -1)){
                    boolean isPartial = ! csp.isFinishedInTime(); //time != 0;
                    partialSolution.put(savedSerializations.get(trace),new RunResults(container, e._4(), instanceName, totalTimeTaken, newMissAlignment, csp.getModelSolution(), csp.getInstanceSolution(),time, isPartial));
                    //if(ConfigParameters.SKIP_IF_IMPROVED)
                    //    if(newMissAlignment == 0) break;
                }
            }


            List<Tuple2<Integer, RunResults>> res = partialSolution.entrySet().stream().map(x -> new Tuple2<>(x.getKey(), x.getValue())).collect(Collectors.toList());
            return res.iterator();
        });

        JavaRDD<RunResults> results = JavaPairRDD
                .fromJavaRDD(rdd)
                .reduceByKey((x, y) -> {

                    RunResults res;
                    boolean isPartial;

                    // se comprueba si el missalignment de y es menor que el de x, y si el de y es mayor que cero
                    if((x.bestMissAlignment > y.bestMissAlignment && y.bestMissAlignment >= 0) || x.bestMissAlignment < 0)
                        res = y;
                    else
                        res = x;

                    isPartial = x.isPartialSolution || y.isPartialSolution;

                    return new RunResults(
                            res.lpo,
                            res.instanceActivities,
                            res.instanceName,
                            x.operationTimeMs + y.operationTimeMs,
                            res.bestMissAlignment,
                            res.modelSolution,
                            res.instanceSolution,
                            res.solutionTime,
                            isPartial
                    );
                })
                .map(x -> {
                    return x._2;
                });

        if(ConfigParameters.DISTRIBUTED_MODE) {
            //results.saveAsTextFile(ConfigParameters.DISTRIBUTEDOUTPUT);
            MongoSpark.save(results.map(x ->{
                ObjectMapper mapper = new ObjectMapper();
                return Document.parse(mapper.writeValueAsString(x));
            } ));
        } else {
            List<String> resultsList = results
                    .map(x -> {

                        String output = "";
                        //output += "LPOs: " + lpoFilesBroadcast.value() + System.lineSeparator();
                        output += "Trace: " + x.instanceActivities + System.lineSeparator();
                        output += "Trace name: " + x.instanceName + System.lineSeparator();
                        output += "LPO with best score: " + x.lpo.lpoFileName + System.lineSeparator();
                        output += "Best score: " + x.bestMissAlignment + System.lineSeparator();
                        output += "Solution" + System.lineSeparator();
                        output += x.modelSolution + System.lineSeparator();
                        output += x.instanceSolution + System.lineSeparator();
                        output += "Operation Time (ms): " + x.operationTimeMs + System.lineSeparator();
                        if(x.solutionTime != 0){
                            output += "This is a partial solution that was reached in " + x.solutionTime + " milliseconds" + System.lineSeparator();
                        }
                        return output;
                    })
                    .collect();
            Integer index;
            for(index = 0; index<resultsList.size(); index++){
                PrintWriter writer = new PrintWriter(ConfigParameters.outputFileName + "_trace_" + index + ".txt","UTF-8");
                writer.write(resultsList.get(index));
                writer.close();
            }
        }
    }

    @Deprecated
    private static List<Tuple2<String, String>> printSolutions(Map<Integer,RunResults> finalSolutions) throws FileNotFoundException, UnsupportedEncodingException{

        List<Tuple2<String, String>> totalOutput = new ArrayList<>();

        for(Map.Entry<Integer, RunResults> entry : finalSolutions.entrySet()){
            //Integer traceIndex = tracesHashes.indexOf(entry.getKey());
            Integer traceIndex = entry.getKey();

            String output = "";

            output += "LPOs: " + lpoFiles + System.lineSeparator();
            output += "Trace: " + entry.getValue().instanceActivities + System.lineSeparator();
            output += "Trace name: " + entry.getValue().instanceName + System.lineSeparator();
            output += "LPO with best score: " + entry.getValue().lpo.lpoFileName + System.lineSeparator();
            output += "Best score: " + entry.getValue().bestMissAlignment + System.lineSeparator();
            output += "Solution" + System.lineSeparator();
            output += entry.getValue().modelSolution + System.lineSeparator();
            output += entry.getValue().instanceSolution + System.lineSeparator();
            output += "Operation Time (ms): " + entry.getValue().operationTimeMs + System.lineSeparator();
            if(entry.getValue().solutionTime != 0){
                output += "This is a partial solution that was reached in " + entry.getValue().solutionTime + " milliseconds" + System.lineSeparator();
            }

            //This will only execute if it's local and we process all LPOs at once
            if(checkLines != null){
                //csv has 3 useless lines at the beginning
                for(int i = 3; i <= checkLines.size(); i++){
                    String s = checkLines.get(i);

                    if(s.equals("")){
                        break;
                    }

                    String[] array = s.split(",");

                    String first = array[1];
                    String[] allnames = first.split("\\|");
                    boolean contained = false;

                    for(String option: allnames){
                        String cleanedOption = option.replace("\"","");
                        if(cleanedOption.equals(entry.getValue().instanceName)){
                            contained = true;
                            break;
                        }
                    }

                    if(contained){
                        String newString = array[4].replace("\"","");
                        if(Integer.parseInt(newString) == entry.getValue().bestMissAlignment){
                            output += "This score checks out with line " + i + " at the CSV file.";
                        } else {
                            //We print extensive info in case of an error
                            output += "==================ERROR==================";
                            output += "This score DOES NOT CHECK OUT with line " + i + " at the CSV file.";
                            output += "At the CSV file the score is " + newString;
                            output += "==================ERROR==================";

                        }
                        break;
                    }
                }
            }

            totalOutput.add(new Tuple2<>(traceIndex.toString(), output));

        }
        return totalOutput;
    }

    //Type that we use for printing results
    public static class RunResults implements Serializable {
        public LPOContainer lpo;
        private List<String> instanceActivities;
        private String instanceName;
        private double operationTimeMs; // TODO name changed from operationTimeSeconds to operationTimeMs
        private int bestMissAlignment;
        private List<String> modelSolution;
        private List<String> instanceSolution;
        private long solutionTime;
        private boolean isPartialSolution;

        RunResults(LPOContainer lpo, List<String> instanceActivities, String instanceName, double operationTimeMs, int bestMissAlignment, List<String> modelSolution, List<String> instanceSolution, long solutionTime, boolean isPartialSolution) throws IOException{
            this.lpo = lpo;
            this.instanceActivities = instanceActivities;
            this.instanceName = instanceName;
            this.operationTimeMs = operationTimeMs;
            this.bestMissAlignment = bestMissAlignment;
            this.modelSolution = modelSolution;
            this.instanceSolution = instanceSolution;
            this.solutionTime = solutionTime;
            this.isPartialSolution = isPartialSolution;
        }

        public LPOContainer getLpo() {
            return lpo;
        }

        public void setLpo(LPOContainer lpo) {
            this.lpo = lpo;
        }

        public List<String> getInstanceActivities() {
            return instanceActivities;
        }

        public void setInstanceActivities(List<String> instanceActivities) {
            this.instanceActivities = instanceActivities;
        }

        public String getInstanceName() {
            return instanceName;
        }

        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }

        public double getOperationTimeMs() {
            return operationTimeMs;
        }

        public void setOperationTimeMs(double operationTimeMs) {
            this.operationTimeMs = operationTimeMs;
        }

        public int getBestMissAlignment() {
            return bestMissAlignment;
        }

        public void setBestMissAlignment(int bestMissAlignment) {
            this.bestMissAlignment = bestMissAlignment;
        }

        public List<String> getModelSolution() {
            return modelSolution;
        }

        public void setModelSolution(List<String> modelSolution) {
            this.modelSolution = modelSolution;
        }

        public List<String> getInstanceSolution() {
            return instanceSolution;
        }

        public void setInstanceSolution(List<String> instanceSolution) {
            this.instanceSolution = instanceSolution;
        }

        public long getSolutionTime() {
            return solutionTime;
        }

        public void setSolutionTime(long solutionTime) {
            this.solutionTime = solutionTime;
        }

        public boolean isPartialSolution() {
            return isPartialSolution;
        }

        public void setPartialSolution(boolean partialSolution) {
            this.isPartialSolution = partialSolution;
        }

    }

    //Type just to include the LPO file name which is important.
    public static class LPOContainer implements Serializable {
        public PetriNetLpo lpo;
        public String lpoFileName;

        public LPOContainer(PetriNetLpo lpo, String fileName){
            this.lpo = lpo;
            lpoFileName = fileName;
        }

        @JsonValue
        public String toString(){
            return lpoFileName;
        }
    }

    private static String serializeTrace(XTrace trace) throws IOException{
        XesXmlSerializer serializer = new XesXmlSerializer();
        OutputStream baou = new ByteArrayOutputStream();
        XLog xlog = new XLogImpl(trace.getAttributes());
        xlog.add(trace);
        serializer.serialize(xlog, baou);
        return baou.toString();
    }

    private static XTrace deserializeTrace(String traceBytes) throws Exception{
        XesXmlParser parser = new XesXmlParser();
        return parser.parse(new ByteArrayInputStream(traceBytes.getBytes())).get(0).get(0);
    }

    public static void printDebugData(String fileName, String data) throws IOException{
        BufferedWriter writer = new BufferedWriter( new FileWriter( ConfigParameters.DEBUG_LOCATION + fileName+".log" , true ) );
        writer.write(data);
        writer.close();
    }

}
