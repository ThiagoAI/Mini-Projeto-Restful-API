package sensorsandstreams.service;

import java.util.ArrayList;
import java.util.List;

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
	public String getUnits(){
		
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("units");
			
			//Colocamos todas as unidades na lista de retorno
			DBCursor cursor = table.find();
			List<BasicDBObject> units = new ArrayList<BasicDBObject>();
			while(cursor.hasNext()) {
				BasicDBObject temp = (BasicDBObject)cursor.next();
				BasicDBObject formatted = new BasicDBObject();
				formatted.put("oid",temp.get("_id").toString());
				formatted.put("label",temp.get("label"));
				units.add(formatted);
			}
			
			cursor.close();
			
			return JsonUtil.toJson(units);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}

}
