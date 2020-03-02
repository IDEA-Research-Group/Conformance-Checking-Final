# Conformance-Checking-Final

This project is intended to solve conformance checking problems in Big Data environments. It has been designed to be run on a Apache Spark cluster. The conformance checking resolutor is implemented by constraint optimization problems.

## Requirements

The following libraries must be imported so that the conformance checking resolutor can work:

* [Spex](http://code.deckfour.org/Spex/)
* [OpenXES](http://code.deckfour.org/xes/)
* Jsolver
* [ILOG CPLEX](https://www.ibm.com/products/ilog-cplex-optimization-studio)

## Configuration

The cnofiguration file is in `es.us.idea.runs.ConfigParameters`.

### Local mode

If it is going to be run in local mode, the following configuration must be set:

* `DISTRIBUTED_MODE` must be set to `false`. 
* The path to the directory in which the partial models are located must be specified in `LOCALLPOS`. It must be a path to a local folder.
* The path to the directory in which the XES file is located must be specified in `LOCALXES`. It must be a path to a local folder.

Note that there exists two directories inside the folder `resources`, containning the necessary files for running `M1` and `M2` in local mode.


### Cluster mode

If it is going to be deployed on a Apache Spark cluster, then the configuration must be set as follows:

* `DISTRIBUTED_MODE` must be set to `true`
* The path to the directory in which the partial models are located must be specified in `HDFSLPOS`. *It must be stored in Hadoop HDFS *
* The path to the directory in which the XES file is located must be specified in `HDFSXES`. *It must be stored in Hadoop HDFS *
* The output will be stored in a MongoDB database. This database is specified in `DISTRIBUTEDOUTPUT` (database URI), `DISTRIBUTEDOUTPUTDB` (database name), and `DISTRIBUTEDOUTPUTCOLLECTION` (collection inside the database).

### Configuration parameters

* Partitions for the set of traces: `TRACECUTS`
* Partitions for the set of partial models: `LPOCUTS`
* Timeout: `MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS`

### Getting configuration from remote

There exists the possibility of getting the configuration from a remote MongoDB database. There must exist a document in a database located at `DISTRIBUTED_MODE`, named as specified in `MONGO_REMOTE_CONFIGURATION_DB`, and in the collection `MONGO_REMOTE_CONFIGURATION_COLLECTION`:

```
{
    "_id" : ObjectId("5e3ac2b69fd567b7f25a801e"),
    "EXECUTORMEMORY" : "8g",
    "DRIVERMEMORY" : "4g",
    "EXECUTORCORES" : null,
    "DRIVERCORES" : null,
    "MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS" : 15000000,
    "TRACECUTS" : 500,
    "LPOCUTS" : 16,
    "HDFSLPOS" : "PATH_TO_LPOS",
    "HDFSXES" : "PATH_TO_TRACES",
    "DISTRIBUTEDOUTPUT" : "PATH_TO_OUTPUT_DB",
    "DISTRIBUTEDOUTPUTDB" : "confcheck",
    "DISTRIBUTEDOUTPUTCOLLECTION" : "CC_M8"
}
```

## Running the algorithm

Run the class `es.us.idea.runs.RunProblem`

## Generating jar file

In order to run it on a Apache Spark cluster, a jar file must be generated. It can be done by executing the following maven command:

`mvn clean package`

