package sensorsandstreams.controller;

import static spark.Spark.*;
import static jsontransformer.JsonUtil.*;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import sensorsandstreams.Unit;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.UnitService;

public class UnitController {

	public UnitController(final UnitService unitService){
	
		//Get para pegar todas as unidades
		get("/units", (req,res) -> {
			res.type("application/json");
			JsonArray json = unitService.getUnits();
			
			//Só tem status se ocorreu um erro 
			if(((JsonObject)json.get(0)).has("status")) res.status(400);
			
			return json;
		});
	}
}
