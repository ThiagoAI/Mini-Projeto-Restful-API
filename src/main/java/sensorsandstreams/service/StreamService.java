package sensorsandstreams.service;

import sensorsandstreams.Stream;
import sensorsandstreams.Data;
import sensorsandstreams.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class StreamService {
	
	//Registra no banco de dados uma stream para um sensor (key)
	/*Recebe label e unit da stream e key do sensor, retorna o json formatted*/
	public JsonObject registerStream(String label,String unit,String sensor) {
		Stream stream = new Stream(label,unit,sensor);
		JsonParser parser = new JsonParser();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("streams");
			
			//Verificamos primeiro se o sensor existe
			BasicDBObject update = new BasicDBObject();
			BasicDBObject query = new BasicDBObject().append("key",sensor);
			DBCollection sensors = db.getCollection("sensors");
			DBCursor cursor = sensors.find(query);
			
			//Se sensor não existir, irá retornar um erro
			if(!cursor.hasNext()) {
				cursor.close();
				return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso. Sensor não existe.\"}"));
			}
			BasicDBObject s = (BasicDBObject) cursor.next();
			
			//Verificamose se a unidade referida também existe
			DBCollection units = db.getCollection("units");
			query.remove("key");
			ObjectId newid;
			
			//Chave do banco pode ser inválida, se for retornamos um erro
			try {
				newid = new ObjectId(unit);
			}
			catch(Exception e) {
				return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso. ID de Unit inválido.\"}"));
			}
			
			query.put("_id", newid);
			cursor = units.find(query);
			if(!cursor.hasNext()) return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso. Unit não existe.\"}"));
			
			//Já que o sensor e a unit existem, criamos e inserimos a stream
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos a stream e registramos no banco de dados
			newobj.put("key",stream.getKey());
			newobj.put("label",stream.getLabel());
			newobj.put("unit",stream.getUnit());
			newobj.put("sensor",s.get("_id").toString());
			newobj.put("totalSize",0);
			table.insert(newobj);
		
			List<ObjectId> streams;
			
			//Se já tiver sido criado alguma stream, entra no if
			if(s.containsField("streams")){
				streams = (List<ObjectId>)s.get("streams");
				streams.add((ObjectId) newobj.get("_id"));
			}
			else {
				//Cria lista e adiciona key da stream nova
				streams = new ArrayList<ObjectId>();
				streams.add((ObjectId) newobj.get("_id"));
			}
			
			//Atualiza o campo "streams" do sensor
			query = new BasicDBObject().append("key",sensor);
			update.append("$set", new BasicDBObject().append("streams",streams));
			sensors.update(query, update);
			
			//Criamos a resposta formatada
			JsonObject formatted = new JsonObject();
			formatted.addProperty("oid",newobj.get("_id").toString());
			formatted.addProperty("key", (String)newobj.get("key"));
			formatted.addProperty("label", (String)newobj.get("label"));
			formatted.addProperty("unit", (String)newobj.get("unit"));
			formatted.addProperty("sensor", (String)newobj.get("sensor"));
			formatted.addProperty("totalSize", (Number)newobj.get("totalSize"));
	
			cursor.close();
			
			return formatted;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}
	
	//Pega os dados de uma stream específica
	/*Recebe chave da stream, retorna a stream com seus dados como json*/
	public JsonObject getSpecificStream(String key) {
		JsonParser parser = new JsonParser();
		Gson g = new Gson();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("streams");
			
			//Verificamos primeiro se o sensor existe
			BasicDBObject query = new BasicDBObject().append("key",key);
			DBCollection streams = db.getCollection("streams");
			DBCursor cursor = streams.find(query);
			
			//Se stream não existir, irá retornar um erro
			if(!cursor.hasNext()) {
				cursor.close();
				return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso. Stream não registrada.\"}"));
			}
			
			//Pegamos a stream, começamos a construir a resposta
			BasicDBObject s = (BasicDBObject) cursor.next();
			
			JsonObject formatted = new JsonObject();
			
			//Inserimos os valores na resposta
			formatted.addProperty("oid",s.get("_id").toString());
			formatted.addProperty("key",(String)s.get("key"));
			formatted.addProperty("label",(String)s.get("label"));
			formatted.addProperty("unit",(String)s.get("unit"));
			formatted.addProperty("sensor",(String)s.get("sensor"));
			formatted.addProperty("totalSize",(Number)s.get("totalSize"));

			//Para inserir as datas, transformamos em uma lista de ids
			BasicDBList listBson = (BasicDBList) s.get("data");
			
			
			if(listBson != null) {
				List<ObjectId> list = new ArrayList<ObjectId>();
				for(Object id: listBson) list.add((ObjectId)id);
				//Chamamos função auxiliar para pegar lista de datas
				List<BasicDBObject> formattedData = DataService.getData(list,mongo);
				formatted.add("data", (JsonArray) parser.parse(g.toJson(formattedData)));
			}
			else formatted.add("data", (JsonElement)new JsonArray());
		
			cursor.close();
			
			return formatted;
		}
		catch(Exception e){
			e.printStackTrace();
		
		}
		
		return (JsonObject) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}
	
	//Função auxiliar, envia BasicDBObject da stream pedida e retorna 5 datas mais recentes
	/*Recebe id da stream e a conexão mongo, retorna BasicDBObject da stream pedida*/
	public static List<BasicDBObject> getStream(List<ObjectId> oids,MongoClient mongo){
		try {
			DB db = mongo.getDB("mini-projeto");
			
			//Pegamos a lista de streams
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put("$in", oids);
			
			BasicDBObject query = new BasicDBObject().append("_id",inQuery);
			DBCollection streams = db.getCollection("streams");
			DBCursor cursor = streams.find(query);
			
			//Criamos a lista de streams resposta e inserimos nela
			List<BasicDBObject> response = new ArrayList<BasicDBObject>();
			
			while(cursor.hasNext()) {
				//Pegamos a stream, começamos a construir a resposta
				BasicDBObject s = (BasicDBObject) cursor.next();
				BasicDBObject formatted = new BasicDBObject();
				
				//Inserimos os valores na resposta
				formatted.put("oid",s.get("_id").toString());
				formatted.put("key",s.get("key"));
				formatted.put("label",s.get("label"));
				formatted.put("unit",s.get("unit"));
				formatted.put("sensor",s.get("sensor"));
				formatted.put("totalSize",s.get("totalSize"));
				
				BasicDBList listBson = (BasicDBList) s.get("data");
			
				//Se não tem data, inserimos data vazia e vamos para a próxima iteração 
				if(listBson == null) {
					formatted.put("data",new ArrayList<ObjectId>());
					continue;
				}
				
				//Para inserir as datas temos de transformar em uma lista de ids
				List<ObjectId> list = new ArrayList<ObjectId>();
				for(Object id: listBson) list.add((ObjectId)id);
				
				List<BasicDBObject> sortedData = DataService.getData(list,mongo);
			
				//Damos sort na sortedData
				Collections.sort(sortedData, (o1,o2) ->{
					long l1 = ((BasicDBObject) o1).getLong("timestamp");
					long l2 = ((BasicDBObject) o2).getLong("timestamp");
					
					if(l1 > l2) return -1;
					if(l2 < l1) return 1;
					return 0;
				});
				
				List<BasicDBObject> formattedData = new ArrayList<BasicDBObject>();
				
				//Passamos as 5 mais recentes. Se tiver menos de 5, esse é o limite.
				int limit = sortedData.size()< 5?sortedData.size():5; 
				
				for(int i=0;i<limit;i++) formattedData.add(sortedData.get(i));
				
				//Inserimos a lista completa na stream
				formatted.put("data", formattedData);
				//Inserimos a stream na lista de streams
				response.add(formatted);
			}
			
			cursor.close();
			
			return response;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	//Função auxiliar, envia BasicDBObject da stream pedida e não retorna data junto
	/*Recebe id da stream e a conexão mongo, retorna BasicDBObject da stream pedida*/
	public static List<BasicDBObject> getStreamNoData(List<ObjectId> oids,MongoClient mongo){
		try {
			DB db = mongo.getDB("mini-projeto");
			
			//Pegamos a lista de streams
			BasicDBObject inQuery = new BasicDBObject();
			inQuery.put("$in", oids);
			
			BasicDBObject query = new BasicDBObject().append("_id",inQuery);
			DBCollection streams = db.getCollection("streams");
			DBCursor cursor = streams.find(query);
			
			//Criamos a lista de streams resposta e inserimos nela
			List<BasicDBObject> response = new ArrayList<BasicDBObject>();
			
			while(cursor.hasNext()) {
				//Pegamos a stream, começamos a construir a resposta
				BasicDBObject s = (BasicDBObject) cursor.next();
				BasicDBObject formatted = new BasicDBObject();
				
				//Inserimos os valores na resposta
				formatted.put("oid",s.get("_id").toString());
				formatted.put("key",s.get("key"));
				formatted.put("label",s.get("label"));
				formatted.put("unit",s.get("unit"));
				formatted.put("sensor",s.get("sensor"));
				formatted.put("totalSize",s.get("totalSize"));
				
				//Inserimos a stream na lista de streams
				response.add(formatted);
			}
			
			cursor.close();
			
			return response;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		return null;
	}
	
}
