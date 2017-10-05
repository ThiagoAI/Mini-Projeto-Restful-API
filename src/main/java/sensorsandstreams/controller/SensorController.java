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
			res.type("application/json");
			//Passamos de JSON para Sensor
			//Em caso de problemas com esta transformação, imprimimos um erro
			Gson g = new Gson();
			
			try {
				Sensor temp = g.fromJson(req.body(), Sensor.class);
				//owner fixo Thiago já que não temos cadastro de usuário
				return sensorService.registerSensor(temp.getLabel(),
					temp.getDescription(),"Thiago");
			}
			catch(Exception e) {
				res.status(400);
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
		
		//Get para pegar um sensor específico (passada a chave)
		get("/sensors", (req,res) ->{
			res.type("application/json");
			return sensorService.getSpecificSensor(req.queryParams("key"));
		});
		
		//Get para pegar sensores específicos de um usuário
		get("/sensors/:username", (req,res )->{
			res.type("application/json");
			return sensorService.getUserSensors(req.params(":username"));
		});
		
	}
}
