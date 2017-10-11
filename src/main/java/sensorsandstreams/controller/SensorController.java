package sensorsandstreams.controller;

import static spark.Spark.*;

import java.util.Map;

import org.eclipse.jetty.server.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import sensorsandstreams.Sensor;
import sensorsandstreams.service.SensorService;

public class SensorController {
	
	public SensorController(final SensorService sensorService){
		
		//Post para registrar um sensor
		post("/sensors", (req,res)->{
			res.type("application/json");
			//Passamos de JSON para Sensor
			//Em caso de problemas com esta transformação, imprimimos um erro
			Gson g = new Gson();
			
			try {
				Sensor temp = g.fromJson(req.body(), Sensor.class);
				//owner fixo Thiago já que não temos cadastro de usuário
				JsonObject json = sensorService.registerSensor(temp.getLabel(),
						temp.getDescription(),"Thiago");
				
				//Só tem status se ocorreu um erro 
				if(json.has("status")) res.status(400);
			
				return json;
			}
			catch(Exception e) {
				res.status(400);
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
		
		//Get para pegar um sensor específico (passada a chave)
		get("/sensors", (req,res) ->{
			res.type("application/json");
			JsonObject json = sensorService.getSpecificSensor(req.queryParams("key"));
			
			//Só tem status se ocorreu um erro 
			if(json.has("status")) res.status(400);
			
			return json;
		});
		
		//Get para pegar sensores específicos de um usuário
		get("/sensors/:username", (req,res )->{
			res.type("application/json");
			JsonArray json =  sensorService.getUserSensors(req.params(":username"));
			
			//Só tem status se ocorreu um erro 
			if(((JsonObject)json.get(0)).has("status")) res.status(400);
			
			return json;
		});
		
	}
}
