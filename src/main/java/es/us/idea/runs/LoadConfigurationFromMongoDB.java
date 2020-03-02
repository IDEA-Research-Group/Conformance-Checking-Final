package es.us.idea.runs;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.Document;

public class LoadConfigurationFromMongoDB {
    public static void load() {
        MongoClient mc = new MongoClient(new MongoClientURI(ConfigParameters.MONGO_REMOTE_CONFIGURATION));
        Document doc = mc.getDatabase(ConfigParameters.MONGO_REMOTE_CONFIGURATION_DB).getCollection(ConfigParameters.MONGO_REMOTE_CONFIGURATION_COLLECTION).find().sort(new BasicDBObject("_id", -1)).first();
        mc.close();
        try {
            ConfigParameters.EXECUTORMEMORY = doc.getString("EXECUTORMEMORY");
            ConfigParameters.DRIVERMEMORY = doc.getString("DRIVERMEMORY");
            ConfigParameters.EXECUTORCORES = doc.getString("EXECUTORCORES");
            ConfigParameters.DRIVERCORES = doc.getString("DRIVERCORES");
            ConfigParameters.MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS = doc.getInteger("MAX_SOLVING_TIME_EACH_PROBLEM_MILLIS");
            ConfigParameters.TRACECUTS = doc.getInteger("TRACECUTS");
            ConfigParameters.LPOCUTS = doc.getInteger("LPOCUTS");
            ConfigParameters.HDFSLPOS = doc.getString("HDFSLPOS");
            ConfigParameters.HDFSXES = doc.getString("HDFSXES");
            ConfigParameters.DISTRIBUTEDOUTPUT = doc.getString("DISTRIBUTEDOUTPUT");
            ConfigParameters.DISTRIBUTEDOUTPUTDB = doc.getString("DISTRIBUTEDOUTPUTDB");
            ConfigParameters.DISTRIBUTEDOUTPUTCOLLECTION = doc.getString("DISTRIBUTEDOUTPUTCOLLECTION");
        } catch(Exception e) {
            throw new IllegalArgumentException("Missing configuration parameters in Mongo config database.");
        }
    }
}
