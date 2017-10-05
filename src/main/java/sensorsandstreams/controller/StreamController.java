package sensorsandstreams.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import sensorsandstreams.Sensor;
import sensorsandstreams.Stream;
import sensorsandstreams.service.StreamService;

import static jsontransformer.JsonUtil.*;

public class StreamController {

	public StreamController(final StreamService streamService){
		
		//Post para registrar uma stream 
		post("/streams", (req,res) -> {
			res.type("application/json");
			//Passamos de JSON para Stream
			//Em caso de problemas com esta transformação, enviamos um erro
			Gson g = new Gson();
			try {
				Stream temp = g.fromJson(req.body(), Stream.class);
				JsonObject json = streamService.registerStream(temp.getLabel(),
						temp.getUnit(),req.queryParams("key"));
				
				//Só tem status se ocorreu um erro 
				if(json.has("status")) res.status(400);
				
				return json;
			}
			catch(Exception e) {
				res.status(400);
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
		
		//Get para pegar informações de uma stream específica
		get("/streams", (req,res) -> {
			res.type("application/json");
			JsonObject json = streamService.getSpecificStream(req.queryParams("key"));
			
			//Só tem status se ocorreu um erro 
			if(json.has("status")) res.status(400);
			
			return json;
		});
		
	}
}
