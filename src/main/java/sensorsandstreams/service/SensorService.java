package sensorsandstreams.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sensorsandstreams.Sensor;
import sensorsandstreams.Unit;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;


import jsontransformer.JsonUtil;

public class SensorService {
	
	//Registra um sensor no banco de dados
	/*Recebe label e description do sensor, retorna o sensor em formato json*/
	public String registerSensor(String label, String description, String owner) {
		
		Sensor sensor = new Sensor(label, description, owner);
		
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
			BasicDBObject formatted = new BasicDBObject();

			//Formatando resposta
			formatted.put("oid",newobj.get("_id").toString());
			formatted.put("key", newobj.get("key"));
			formatted.put("label", newobj.get("label"));
			formatted.put("description", newobj.get("description"));
			
			return JsonUtil.toJson(formatted);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
	//Pega os sensores de um usuário especificado
	/*Recebe nome do usuário, retorna todos os sensores dele em formato Json*/
	public String getUserSensors(String user) {
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
			if(cursor.count() == 0) return new String("{}");
			
			//Percorremos todos os sensores encontrados e adicionamos eles a sensors
			List<BasicDBObject> sensors = new ArrayList<BasicDBObject>();
			
			//Precisamos da collection streams para inserir em formatted
			DBCollection streams = db.getCollection("streams");
			
			//While pegando os sensors
			while(cursor.hasNext()) {
				BasicDBObject tempSensor = (BasicDBObject) cursor.next();
				BasicDBObject formatted = new BasicDBObject();
			
				formatted.put("oid", tempSensor.get("_id").toString());
				formatted.put("key", tempSensor.get("key"));
				formatted.put("label", tempSensor.get("label"));
				formatted.put("description", tempSensor.get("description"));
			
				//Agora pegamos as streams
				BasicDBList listBson = (BasicDBList) tempSensor.get("streams");
				if(listBson != null) {
					//Para inserir as streams temos de transformar em uma lista de ids
					List<ObjectId> list = new ArrayList<ObjectId>();
					for(Object id: listBson) list.add((ObjectId)id);
					
					//Chamamos a função auxiliar
					List<BasicDBObject> formattedStreams = StreamService.getStreamNoData(list,mongo);
					formatted.put("streams", formattedStreams);
				}
				else {
					formatted.put("streams", new ArrayList<>());
				}
				
				//Colocamos o sensor na resposta
				sensors.add(formatted);
			}
			
			cursor.close();
			
			return JsonUtil.toJson(sensors);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
	//Pega um sensor específico
	/*Recebe a key como string, retorna o sensor em formato json*/
	public String getSpecificSensor(String key) {
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
			return new String("{\"status\":\"Operação não foi realizada com sucesso. Sensor não cadastrado.\"}");
		}
		
		//Pegamos o sensor e criamos a resposta
		BasicDBObject sensor = (BasicDBObject)cursor.next();
		BasicDBObject response = new BasicDBObject();
		
		//Já formatamos parte da resposta
		response.put("oid",sensor.get("_id").toString());
		response.put("key",sensor.get("key"));
		response.put("label",sensor.get("label"));
		response.put("unit",sensor.get("description"));
		
		//Precisamos agora pegar todas as streams associadas para passar ao json
		BasicDBList listBson = (BasicDBList) sensor.get("streams");
		
		//Se a lista de ids não for nula...
		if(listBson != null) {
			List<ObjectId> list = new ArrayList<ObjectId>();
			for(Object id: listBson) list.add((ObjectId)id);
		
			List<BasicDBObject> formattedStreams = StreamService.getStream(list,mongo);
			
			response.put("streams", formattedStreams);
		}
		//Se não houverem streams, colocamos uma lista vazia
		else response.put("streams", new ArrayList<BasicDBObject>());
				
		cursor.close();

		return JsonUtil.toJson(response);
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
}
