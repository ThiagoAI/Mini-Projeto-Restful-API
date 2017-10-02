package sensorsandstreams.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import jsontransformer.JsonUtil;
import sensorsandstreams.Sensor;
import sensorsandstreams.User;

public class UserService {
	
	//Registra o usuário no banco. Username deve ser único.
	/*Recebe username e email, responde com o json do usuário cadastrado*/
	public String registerUser(String username,String email) {
		
		User user = new User(username, email);
		
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("sensors");
			BasicDBObject newobj = new BasicDBObject();
			
			//Verificamos se já existe um usuário com o mesmo username
			BasicDBObject query = new BasicDBObject();
			query.put("username", username);
			DBCursor cursor = table.find(query);
			if(cursor.hasNext()) return new String("{\"status\":\"Operação não foi realizada com sucesso. Usuário com este username já está cadastrado.\"}");
			
			//Criamos o user e registramos no banco de dados
			newobj.put("username",user.getUsername());
			newobj.put("email",user.getEmail());
			table.insert(newobj);
			
			BasicDBObject formatted = new BasicDBObject();

			//Formatando resposta
			formatted.put("oid",newobj.get("_id").toString());
			formatted.put("username", newobj.get("username"));
			formatted.put("email", newobj.get("email"));
			
			cursor.close();
			
			return JsonUtil.toJson(formatted);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}

}
