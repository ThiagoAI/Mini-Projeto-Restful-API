package sensorsandstreams.controller;

import static spark.Spark.*;

import java.util.Map;

import org.eclipse.jetty.server.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static jsontransformer.JsonUtil.*;

import sensorsandstreams.Sensor;
import sensorsandstreams.service.SensorService;

public class SensorController {
	
	public SensorController(final SensorService sensorService){
		
		//Post para registrar um sensor
		post("/sensors", (req,res)->{
			Gson g = new Gson();
			Sensor temp = g.fromJson(req.body(), Sensor.class);
			//owner fixo Thiago para testes
			return sensorService.registerSensor(temp.getLabel(),
				temp.getDescription(),"Thiago");
		});
		
		//Get para pegar um sensor específico (passada a chave)
		get("/sensors", (req,res) ->{
			return sensorService.getSpecificSensor(req.queryParams("key"));
		});
		
		//Get para pegar sensores específicos de um usuário
		get("/sensors/:username", (req,res )->{
			return sensorService.getUserSensors(req.params(":username"));
		});
		
		

	}
}
