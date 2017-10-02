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
			BasicDBObject query = new BasicDBObject();
			BasicDBObject formatted = new BasicDBObject();

			//Formatando resposta
			formatted.put("oid",newobj.get("_id").toString());
			formatted.put("key", newobj.get("key"));
			formatted.put("label", newobj.get("label"));
			formatted.put("description", newobj.get("description"));
			
			return JsonUtil.toJson(formatted);
			//sensor.setOid(cursor.next().get("_id").toString());
			//System.out.println(sensor.getOid());
			//cursor.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
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
			while(cursor.hasNext()) {
				BasicDBObject tempSensor = (BasicDBObject) cursor.next();
				BasicDBObject formatted = new BasicDBObject();
			
				formatted.put("oid", tempSensor.get("_id").toString());
				formatted.put("key", tempSensor.get("key"));
				formatted.put("label", tempSensor.get("label"));
				formatted.put("description", tempSensor.get("description"));
			
				//Agora, pegamos as streams
				BasicDBList list = (BasicDBList) tempSensor.get("streams");
				
				//Se tiver streams, precisam ser adicionadas
				if(list != null) {
					List<BasicDBObject> formattedStreams = new ArrayList<BasicDBObject>();
					
					//Criamos as streams formatadas e adicionamos a lista para o json
					Iterator it = list.iterator();
					StreamService ss = new StreamService();
					while(it.hasNext()) {
						ObjectId temp = (ObjectId) it.next();
						BasicDBObject newStream = ss.getStream(temp);
						//Removemos o campo data para response dessa operação
						newStream.remove("data");
						formattedStreams.add(newStream);
					}
					
					formatted.put("streams", formattedStreams);
				}
				//Colocamos uma lista vazia para os sem stream
				else formatted.put("streams", new ArrayList<BasicDBObject>());
				
				//Adicionamos na lista de sensores
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
		DBCollection streams = db.getCollection("streams");
		BasicDBList list = (BasicDBList) sensor.get("streams");
		List<BasicDBObject> formattedStreams = new ArrayList<BasicDBObject>();
		
		//Criamos as streams formatadas e adicionamos a lista para o json
		if(list != null) {
			Iterator it = list.iterator();
			StreamService ss = new StreamService();
			while(it.hasNext()) {
				ObjectId temp = (ObjectId) it.next();
				BasicDBObject newStream = ss.getStream(temp);
				formattedStreams.add(newStream);
			}
			
			//Inserimos a lista da streams na resposta
			response.put("streams", formattedStreams);
		}
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
