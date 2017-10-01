import static spark.Spark.*;

import java.net.UnknownHostException;
import java.util.UUID;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import sensorsandstreams.User;
import sensorsandstreams.controller.DataController;
import sensorsandstreams.controller.SensorController;
import sensorsandstreams.controller.StreamController;
import sensorsandstreams.controller.UnitController;
import sensorsandstreams.service.DataService;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.StreamService;
import sensorsandstreams.service.UnitService;
import jsontransformer.JsonUtil;

public class Main {
	public static void main(String[] args) throws Throwable {
		//SparkConf conf = new SparkConf().setMaster("local").setAppName("Test app");
		//JavaSparkContext sc = new JavaSparkContext(conf);
		//Dataset<String> logData = spark.read.textFile(logFile).cache();
		port(8080);
		ipAddress("localhost");
		
		new SensorController(new SensorService());
		new StreamController(new StreamService());
		new UnitController(new UnitService());
		new DataController(new DataService());
	
	}
	
}
