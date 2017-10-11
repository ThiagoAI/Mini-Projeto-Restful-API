package sensorsandstreams.controller;

import static spark.Spark.*;

import java.util.List;

import org.eclipse.jetty.server.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import sensorsandstreams.Data;
import sensorsandstreams.Stream;
import sensorsandstreams.Unit;
import sensorsandstreams.service.DataService;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.UnitService;

public class DataController {

	public DataController(final DataService dataService){
	
		//Post para publicar informação do sensor
		post("/data", (req,res) -> {
			res.type("application/json");
			//Convertemos os parâmetros passados e chamamos o serviço
			//Em caso de problema com JSON ou com query parameters, imprimimos um erro
			Gson g = new Gson();
			Data temp; 
			try {
				temp = g.fromJson(req.body(), Data.class);
				JsonObject json = dataService.publishData(temp.getTimestamp(),
						temp.getValue(),req.queryParams("key"));
				
				//Só tem status se ocorreu um erro 
				if(json.has("status")) res.status(400);
				
				return json;
			}
			catch(Exception e) {
				res.status(400);
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
	}
}
