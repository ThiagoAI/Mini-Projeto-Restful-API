package sensorsandstreams.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import org.bson.types.ObjectId;

import jsontransformer.JsonUtil;
import sensorsandstreams.Data;
import sensorsandstreams.Stream;

public class DataService {

	//Função para publicar uma medição em uma stream
	/*Recebe timestamp, valor e em qual stream deve registrar, retorna json da data*/
	public String publishData(long timestamp,double value,String stream) {
		Data d = new Data(timestamp,value,stream);
		try {
			//Abrindo mongo
			MongoClient mongo = new MongoClient("0.0.0.0",27017);
			DB db = mongo.getDB("mini-projeto");
			DBCollection table = db.getCollection("data");
			
			//Verificamos primeiro se a stream existe
			BasicDBObject update = new BasicDBObject();
			BasicDBObject query = new BasicDBObject().append("key",stream);
			DBCollection streams = db.getCollection("streams");
			DBCursor cursor = streams.find(query);
			
			//Se stream não existir, irá retornar um erro
			if(!cursor.hasNext()) {
				cursor.close();
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Stream não está registrada.\"}");
			}
			//s guarda a stream da data
			BasicDBObject s = (BasicDBObject) cursor.next();
			
			//Já que o sensor existe, criamos e inserimos a stream
			BasicDBObject newobj = new BasicDBObject();
			
			//Criamos o sensor e registramos no banco de dados
			newobj.put("timestamp",d.getTimestamp());
			newobj.put("value",d.getValue());
			table.insert(newobj);
			String oid = newobj.get("_id").toString();
			
			//Pegamos a data inserida para atualizar a lista com o oid
			//assim como enviar uma resposta formatada posteriormente
			query = new BasicDBObject();
			query.put("_id", oid);
		
			List<ObjectId> data;
			
			//Se já tiver sido criado alguma data, entra no if
			if(s.containsField("data")){
				data = (List<ObjectId>)s.get("data");
				data.add((ObjectId) newobj.get("_id"));
			}
			else {
				//Cria lista e adiciona key da stream nova
				data = new ArrayList<ObjectId>();
				data.add((ObjectId) newobj.get("_id"));
			}
			
			//Atualiza o campo totalSize e data desta stream
			query = new BasicDBObject().append("key",stream);
			BasicDBObject newStream = new BasicDBObject();
			newStream.append("data", data);
			long newSize = s.getLong("totalSize") + 1;
			newStream.append("totalSize", newSize);
			update.append("$set", newStream);
			streams.update(query, update);
			
			//Criamos a resposta formatada usando a stream do banco
			BasicDBObject formatted = new BasicDBObject();
			
			//Colocamos os valores na resposta formatada
			formatted.put("oid", oid);
			formatted.put("timestamp",d.getTimestamp());
			formatted.put("value",d.getValue());
			
			cursor.close();
			
			return JsonUtil.toJson(formatted);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return new String("{\"status\":\"Operação não foi realizada com sucesso.\"}");
	}
	
	//Função auxiliar, envia BasicDBObject da data pedida
	/*Recebe o id da data e a conexão mongo, retorna ela como objeto do banco formatada*/
	public static List<BasicDBObject> getData(List<ObjectId> oids,MongoClient mongo) {
		try {
		//Pegando a collection
		DB db = mongo.getDB("mini-projeto");
		DBCollection table = db.getCollection("data");
		
		//Executamos a query obtendo todas as datas
		BasicDBObject inQuery = new BasicDBObject();
		inQuery.put("$in", oids);
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", inQuery);
		DBCursor cursor = table.find(query);
		
		List<BasicDBObject> response = new ArrayList<BasicDBObject>();

		//Se estiver vazio retornamos nulo
		if(!cursor.hasNext()){
			return new ArrayList<>();
		}
		
		//Colocamos na lista removendos os ids
		int i = 0;
		while(cursor.hasNext()) {
			response.add((BasicDBObject)cursor.next());
			response.get(i).removeField("_id");
			i++;
		}
		
		cursor.close();
		
		return response;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
}
