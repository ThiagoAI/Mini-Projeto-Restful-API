package sensorsandstreams.controller;

import static spark.Spark.*;
import static jsontransformer.JsonUtil.*;

import java.util.List;

import sensorsandstreams.Unit;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.UnitService;

public class UnitController {

	public UnitController(final UnitService unitService){
	
		get("/units", (req,res) -> {
			return unitService.getUnits();
		});
	}
}
