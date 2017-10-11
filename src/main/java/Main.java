/*	API de Sensores e Streams
 *  Autor: Thiago Anders Imhoff
 *  
 *  VER README.md PARA DOCUMENTAÇÃO !!!
 */

import static spark.Spark.*;

import sensorsandstreams.controller.DataController;
import sensorsandstreams.controller.SensorController;
import sensorsandstreams.controller.StreamController;
import sensorsandstreams.controller.UnitController;
import sensorsandstreams.controller.UserController;
import sensorsandstreams.service.DataService;
import sensorsandstreams.service.SensorService;
import sensorsandstreams.service.StreamService;
import sensorsandstreams.service.UnitService;
import sensorsandstreams.service.UserService;

public class Main {
	public static void main(String[] args) throws Throwable {

		//Restful api escutará em localhost:8080 por default
		port(8080);
		ipAddress("localhost");
		
		//Ativando controllers
		new SensorController(new SensorService());
		new StreamController(new StreamService());
		new UnitController(new UnitService());
		new DataController(new DataService());

		//new UserController(new UserService());
	
	}
	
}