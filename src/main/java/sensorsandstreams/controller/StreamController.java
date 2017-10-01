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
			Gson g = new Gson();
			Stream temp = g.fromJson(req.body(), Stream.class);
			return streamService.registerStream(temp.getLabel(),
					temp.getUnit(),req.queryParams("key"));			
		});
		
		//Get para pegar informações de uma stream específica
		get("/streams", (req,res) -> {
			return streamService.getSpecificStream(req.queryParams("key"));
		});
		
	}
}
