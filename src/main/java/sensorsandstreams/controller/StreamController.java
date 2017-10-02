package sensorsandstreams.controller;

import static spark.Spark.*;

import com.google.gson.Gson;

import sensorsandstreams.Sensor;
import sensorsandstreams.Stream;
import sensorsandstreams.service.StreamService;

import static jsontransformer.JsonUtil.*;

public class StreamController {

	public StreamController(final StreamService streamService){
		
		//Post para registrar uma stream 
		post("/streams", (req,res) -> {
			//Passamos de JSON para Stream
			//Em caso de problemas com esta transformação, imprimimos um erro
			Gson g = new Gson();
			try {
			Stream temp = g.fromJson(req.body(), Stream.class);
			return streamService.registerStream(temp.getLabel(),
					temp.getUnit(),req.queryParams("key"));	
			}
			catch(Exception e) {
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
		
		//Get para pegar informações de uma stream específica
		get("/streams", (req,res) -> {
			return streamService.getSpecificStream(req.queryParams("key"));
		});
		
	}
}
