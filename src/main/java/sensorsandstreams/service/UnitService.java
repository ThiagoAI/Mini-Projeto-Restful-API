package sensorsandstreams.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import jsontransformer.JsonUtil;
import sensorsandstreams.Unit;

public class UnitService {
	
	//Pega todas as unidades diferentes do banco
	/*Não tem entrada e retorna o json para impressão*/
	public JsonArray getUnits(){
		JsonParser parser = new JsonParser();
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("units");
			
			//Colocamos todas as unidades na lista de retorno
			DBCursor cursor = table.find();
			
			//Criamos o Json de retorno iterando pelo cursor
			JsonArray units = new JsonArray();
			while(cursor.hasNext()) {
				BasicDBObject temp = (BasicDBObject)cursor.next();
				JsonObject formatted = new JsonObject();
				formatted.addProperty("oid",temp.get("_id").toString());
				formatted.addProperty("label",(String) temp.get("label"));
				units.add(formatted);
			}
			
			cursor.close();
			
			return units;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return (JsonArray) parser.parse(new String("{\"status\":\"Operação não foi realizada com sucesso.\"}"));
	}

}
