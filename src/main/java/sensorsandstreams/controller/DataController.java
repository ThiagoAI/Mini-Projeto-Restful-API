package sensorsandstreams.controller;

import static spark.Spark.*;
import static jsontransformer.JsonUtil.*;

import java.util.List;

import com.google.gson.Gson;

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
			Gson g = new Gson();
			Data temp = g.fromJson(req.body(), Data.class);
			return dataService.publishData(temp.getTimestamp(),
					temp.getValue(),req.queryParams("key"));
		});
	}
}
