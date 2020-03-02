package es.us.idea.runs;

public final class ConfigParameters {

	// In distributed mode, the program cannot be executed in local. Files will be read in HDFS (routes described below).
	// It will be necessary to package the program and submit a JAR to the driver that will manage execution.
	// If this mode is not enabled, the program will be launched normally with the local file directories.
	public final static boolean DISTRIBUTED_MODE = false;
	// If true, some config parameters will be taken from a remote database
	public final static boolean REMOTE_CONFIGURATION = false;
	// MongoDB collection URI with the configuration
	public final static String MONGO_REMOTE_CONFIGURATION = "URI_TO_MONGO";
	//public final static String MONGO_REMOTE_CONFIGURATION = "mongodb://localhost:27017";
	public final static String MONGO_REMOTE_CONFIGURATION_DB = "CC_config";
	public final static String MONGO_REMOTE_CONFIGURATION_COLLECTION = "configurations";

	// Cluster config params
	public static String EXECUTORMEMORY = "8g";
	public static String DRIVERMEMORY = "4g";
	public static String EXECUTORCORES = null;
	public static String DRIVERCORES = null;

	public static final String TEST_CASE = "M1";
	public static final String TEST_TO_RUN = "M1";

	public static final int MIN_PARTITIONS = 50;

	public static String HDFSLPOS = "URI_TO_LPOS_IN_HDFS";
	// Other LPO paths: /runs/robin/ /runs/newlpos/ /runs/M1/
	public static String HDFSXES = "URI_TO_XES_IN_HDFS";
	// Other XES (ver carpeta, src/main/resources/xes): /xes/runtests/logRobinv2.xes /xes/runtests/M1.xes /xes/runtests/M1v2.xes
	//public static final String DISTRIBUTEDOUTPUT = "hdfs://10.141.10.111:8020/user/snape/conformance_checking/salida";
	public static String DISTRIBUTEDOUTPUT = "URI_TO_MONGO";
	public static String DISTRIBUTEDOUTPUTDB = "confcheck";
	public static String DISTRIBUTEDOUTPUTCOLLECTION = "CCX_" + TEST_CASE + "_" + TEST_TO_RUN/* + "_RES"*/;

	// Local partial model path
	public static final String LOCALLPOS = "/partialModels/M1/";
	// Local XES path
	public static final String LOCALXES = "/xes/M1.xes";

	// Output directory
	public static final String outputFileName = "results/output";

	// Show (true) or not (false) the model in output
	public final static boolean PRINTMODEL = false;
	// Show (true) or not (false) the trace in search
	public final static boolean TRACE = false;
	// Show (true) or not (false) the examples being processed
	public final static boolean SOL = false;
	// Force (true) or not (false) instantiation of positions
	public final static boolean GENERATE = true;

    // Enables result checking with CSV files from ProM
    // Even if this parameter is enabled, it will not work if there are LPO cuts or the execution is parallelized
    // It's meant for testing purposes
    public final static boolean CHECK_SOLUTIONS = false;
    public final static String DEBUG_LOCATION = "results/debug/";
    public final static String CHECK_SOLUTIONS_FILE = "CSVResultsM1.csv";

	// Timeout: It will be disabled if non-positive
	public static Integer MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS = 5000;

	//These parameters represent the number of pieces to divide the LPO and Trace lists in, when paralellizing.
	//The total number of partitions will be LPOCUTS * TRACECUTS
	//It is advisable to make the number of partitions eq.ual or higher than the number of executors that will launch the program
	//To use up all the cores and not leave any idle.
	public static int TRACECUTS = 500;
	public static int LPOCUTS = 1;
	//public final static int LPOCUTS = 100;
	//public final static int TRACECUTS = 1;
	// Don't modify this parameter
	public final static boolean SKIP_IF_IMPROVED = false;


	// LPOs and trace slice. Lower than 0 will take all
	public final static int LPOTAKE = -1;
	public final static int TRACETAKE = -1;

	// *****DEPRECATED*****

	// Parameters for CSP and search:
	// Number of iterations to determine average of search
	public final static int NITERATIONS = 0;

	// If one example takes longer that this amount of time, specific details about it will be logged. A negative value
	// makes all examples to be logged. Disable with 0.
	public final static float MINUTESTOLOGHARDSOLUTIONS = 0f;
	// Makes a table of solutions in CSV format to log the time or hard examples, but execution needs to end for them.
	public final static boolean BUILD_TIME_TABLE_HARD_SOLUTIONS = false;

	public final static int MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS_2 = 1000000;


	// SOLVER
	// Select jsolver (0) CPLEX (1) ChocoSolver (2)
	public final static int SOLVER = 2;

	public final static int NUMBER_REPETITIONS_HARD = 5;
	public final static float NUMBER_REPETITIONS_MULTIPLIER = 1f;
	public final static float PERCENTAGE_BIG_TRACES = 0.05f;
	public final static float BIG_TRACE_MULTIPLIER = 1f;
	//public final static int BIG_HOLE_MULTIPLIER = 2;
}
