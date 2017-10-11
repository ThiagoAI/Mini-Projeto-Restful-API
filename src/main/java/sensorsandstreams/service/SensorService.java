package sensorsandstreams.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sensorsandstreams.Sensor;
import sensorsandstreams.Unit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;

public class SensorService {
	
	//Registra um sensor no banco de dados
	/*Recebe label e description do sensor, retorna o sensor em formato json*/
	public JsonObject registerSensor(String label, String description, String owner) {
		Sensor sensor = new Sensor(label, description, owner);
		JsonParser parser = new JsonParser();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("sensors");
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos o sensor e registramos no banco de dados
			newobj.put("label",sensor.getLabel());
			newobj.put("description",sensor.getDescription());
			newobj.put("owner",sensor.getOwner());
			newobj.put("key",sensor.getKey());
			table.insert(newobj);
			
			//Formatamos a resposta, que será formated em formato json
			JsonObject formatted = new JsonObject();
			
			//Formatando resposta
			formatted.addProperty("oid",newobj.get("_id").toString());
			formatted.addProperty("key", (String)newobj.get("key"));
			formatted.addProperty("label", (String)newobj.get("label"));
			formatted.addProperty("description", (String)newobj.get("description"));
			
			return formatted;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}
	
	//Pega os sensores de um usuário especificado
	/*Recebe nome do usuário, retorna todos os sensores dele em formato Json*/
	public JsonArray getUserSensors(String user) {
		JsonParser parser = new JsonParser();
		Gson g = new Gson();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("sensors");
			
			//Percorremos todos os sensores em busca dos sensores do usuário
			BasicDBObject query = new BasicDBObject();
			query.put("owner",user);
			DBCursor cursor = table.find(query);
			
			//Se não tiver sensores, retorna um json vazio
			if(cursor.count() == 0) return new JsonArray();
			
			//Percorremos todos os sensores encontrados e adicionamos eles a sensors
			//List<BasicDBObject> sensors = new ArrayList<BasicDBObject>();
			JsonArray sensors = new JsonArray();
			
			//Precisamos da collection streams para inserir em formatted
			DBCollection streams = db.getCollection("streams");
			
			//While pegando os sensors
			while(cursor.hasNext()) {
				BasicDBObject tempSensor = (BasicDBObject) cursor.next();
				JsonObject formatted = new JsonObject();
			
				formatted.addProperty("oid", tempSensor.get("_id").toString());
				formatted.addProperty("key", (String)tempSensor.get("key"));
				formatted.addProperty("label", (String)tempSensor.get("label"));
				formatted.addProperty("description", (String)tempSensor.get("description"));
			
				//Agora pegamos as streams
				BasicDBList listBson = (BasicDBList) tempSensor.get("streams");
				if(listBson != null) {
					//Para inserir as streams temos de transformar em uma lista de ids
					List<ObjectId> list = new ArrayList<ObjectId>();
					for(Object id: listBson) list.add((ObjectId)id);
					
					//Chamamos a função auxiliar e transformamos em json
					List<BasicDBObject> formattedStreams = StreamService.getStreamNoData(list,mongo);
					JsonArray array = (JsonArray) parser.parse(g.toJson(formattedStreams));
					formatted.add("streams", array);
				}
				else {
					formatted.add("streams", new JsonArray());
				}
				
				//Colocamos o sensor na resposta
				sensors.add(formatted);
			}
			
			cursor.close();
			
			return sensors;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return (JsonArray) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}
	
	//Pega um sensor específico
	/*Recebe a key como string, retorna o sensor em formato json*/
	public JsonObject getSpecificSensor(String key) {
		JsonParser parser = new JsonParser();
		Gson g = new Gson();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("sensors");
			
			//Procuramos o sensor no banco
			BasicDBObject query = new BasicDBObject();
			query.put("key", key);
			DBCursor cursor = table.find(query);
			
			//Se sensor não existir, saimos com erro
			if(!cursor.hasNext()) {
				return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso. Sensor não registrado.\"}"));
			}
			
			//Pegamos o sensor e criamos a resposta
			BasicDBObject sensor = (BasicDBObject)cursor.next();
			JsonObject formatted = new JsonObject();
			
			//Já formatamos parte da resposta
			formatted.addProperty("oid",sensor.get("_id").toString());
			formatted.addProperty("key",(String)sensor.get("key"));
			formatted.addProperty("label",(String)sensor.get("label"));
			formatted.addProperty("unit",(String)sensor.get("description"));
			
			//Precisamos agora pegar todas as streams associadas para passar ao json
			BasicDBList listBson = (BasicDBList) sensor.get("streams");
			
			//Se a lista de ids não for nula...
			if(listBson != null) {
				List<ObjectId> list = new ArrayList<ObjectId>();
				for(Object id: listBson) list.add((ObjectId)id);
			
				List<BasicDBObject> formattedStreams = StreamService.getStream(list,mongo);
				
				formatted.add("streams", (JsonArray) parser.parse(g.toJson(formattedStreams)));
			}
			//Se não houverem streams, colocamos uma lista vazia
			else formatted.add("streams", new JsonArray());
					
			cursor.close();
	
			return formatted;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}
	
}
