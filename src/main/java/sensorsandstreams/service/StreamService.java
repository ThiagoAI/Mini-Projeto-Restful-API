package sensorsandstreams.service;

import sensorsandstreams.Stream;
import sensorsandstreams.Data;
import sensorsandstreams.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;

import jsontransformer.JsonUtil;

public class StreamService {
	
	//Registra no banco de dados uma stream para um sensor (key)
	/*Recebe label e unit da stream e key do sensor, retorna o json response*/
	public String registerStream(String label,String unit,String sensor) {
		Stream stream = new Stream(label,unit,sensor);
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
				return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
			}
			BasicDBObject s = (BasicDBObject) cursor.next();
			
			//Já que o sensor existe, criamos e inserimos a stream
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos a stream e registramos no banco de dados
			newobj.put("key",stream.getKey());
			newobj.put("label",stream.getLabel());
			newobj.put("unit",stream.getUnit());
			newobj.put("sensor",s.get("_id").toString());
			newobj.put("totalSize",0);
			table.insert(newobj);
			
			//Pegamos a stream inserida para atualizar a lista com o oid
			//assim como enviar uma resposta formata posteriormente
			query = new BasicDBObject();
			query.put("key", stream.getKey());
			cursor = table.find(query);
			BasicDBObject streamDB = (BasicDBObject)cursor.next();
		
			List<ObjectId> streams;
			
			//Se já tiver sido criado alguma stream, entra no if
			if(s.containsField("streams")){
				streams = (List<ObjectId>)s.get("streams");
				streams.add((ObjectId) streamDB.get("_id"));
			}
			else {
				//Cria lista e adiciona key da stream nova
				streams = new ArrayList<ObjectId>();
				streams.add((ObjectId) streamDB.get("_id"));
			}
			
			//Atualiza o campo "streams" do sensor
			query = new BasicDBObject().append("key",sensor);
			update.append("$set", new BasicDBObject().append("streams",streams));
			sensors.update(query, update);
			
			//Criamos a resposta formatada usando a stream do banco
			BasicDBObject formatted = new BasicDBObject();
			
			formatted.put("oid",streamDB.get("_id").toString());
			formatted.put("key", streamDB.get("key"));
			formatted.put("label", streamDB.get("label"));
			formatted.put("unit", streamDB.get("unit"));
			formatted.put("sensor", streamDB.get("sensor"));
			formatted.put("totalSize", streamDB.get("totalSize"));
			
			cursor.close();
			
			return JsonUtil.toJson(formatted);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
	//Pega os dados de uma stream específica
	/*Recebe chave da stream, retorna a stream com seus dados*/
	public String getSpecificStream(String key) {
		
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
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Stream não registrada.\"}");
			}
			
			//Pegamos a stream, começamos a construir a resposta
			BasicDBObject s = (BasicDBObject) cursor.next();
			BasicDBObject response = new BasicDBObject();
			
			//Já que o sensor existe, criamos e inserimos a stream
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos a stream e a registramos no banco de dados
			response.put("oid",s.get("_id").toString());
			response.put("key",s.get("key"));
			response.put("label",s.get("label"));
			response.put("unit",s.get("unit"));
			response.put("sensor",s.get("sensor"));
			response.put("totalSize",s.get("totalSize"));

			//Para inserir as datas, temos de iterar pelos ids
			BasicDBList list = (BasicDBList) s.get("data");
			List<BasicDBObject> formattedData = new ArrayList<BasicDBObject>();
			
			//Se tiver data, entra no if e itera pela lista de data
			if(list != null) {
				Iterator it = list.iterator();
				DataService ds = new DataService();
				while(it.hasNext()) {
					ObjectId temp = (ObjectId) it.next();
					BasicDBObject newData = ds.getData(temp);
					formattedData.add(newData);
				}
				//Inserimos a lista completa
				response.put("data", formattedData);
			}
			else {
				//Se estiver vazio colocamos uma lista vazia
				response.put("data", new ArrayList<BasicDBObject>());
			}
			cursor.close();
			
			return JsonUtil.toJson(response);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
	//Função auxiliar, envia BasicDBObject da stream pedida
	/*Recebe id da stream, retorna BasicDBObject da stream pedida*/
	public BasicDBObject getStream(ObjectId oid){
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("streams");
			
			//Verificamos primeiro se o sensor existe
			BasicDBObject query = new BasicDBObject().append("_id",oid);
			DBCollection streams = db.getCollection("streams");
			DBCursor cursor = streams.find(query);
			
			//Se stream não existir, irá retornar um erro
			if(!cursor.hasNext()) {
				cursor.close();
				return null;
			}
			
			//Pegamos a stream, começamos a construir a resposta
			BasicDBObject s = (BasicDBObject) cursor.next();
			BasicDBObject response = new BasicDBObject();
			
			//Já que o sensor existe, criamos e inserimos a stream
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos a stream e a registramos no banco de dados
			response.put("oid",s.get("_id").toString());
			response.put("key",s.get("key"));
			response.put("label",s.get("label"));
			response.put("unit",s.get("unit"));
			response.put("sensor",s.get("sensor"));
			response.put("totalSize",s.get("totalSize"));

			//Para inserir as datas, temos de iterar pelos ids
			BasicDBList list = (BasicDBList) s.get("data");
			List<BasicDBObject> formattedData = new ArrayList<BasicDBObject>();
			
			//Se houver data na stream...
			if(list != null) {
				Iterator it = list.iterator();
				DataService ds = new DataService();
				//Limite de 5 datas mais recentes, vamos ordernar outra lista
				List<BasicDBObject> sortedData = new ArrayList<BasicDBObject>();
				
				while(it.hasNext()) {
					ObjectId temp = (ObjectId) it.next();
					BasicDBObject newData = ds.getData(temp);
					sortedData.add(newData);
				}
				
				//Damos sort na sortedData
				Collections.sort(sortedData, (o1,o2) ->{
					long l1 = ((BasicDBObject) o1).getLong("timestamp");
					long l2 = ((BasicDBObject) o2).getLong("timestamp");
					
					if(l1 > l2) return -1;
					if(l2 < l1) return 1;
					return 0;
				});
				
				//Passamos as 5 mais recentes. Se tiver menos de 5, esse é o limite.
				int limit = sortedData.size()< 5?sortedData.size():5; 
				
				for(int i=0;i<limit;i++) formattedData.add(sortedData.get(i));
				
				//Inserimos a lista completa
				response.put("data", formattedData);
			}
			else {
				//Se a lista é nula, colocamos ela vazia
				response.put("data", new ArrayList<BasicDBObject>());
			}
			
			
			cursor.close();
			
			return response;
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	//Função auxiliar que envia em BasicDBObject uma stream 
	}
}

