package sensorsandstreams.controller;

import static spark.Spark.post;

import com.google.gson.Gson;

import sensorsandstreams.User;
import sensorsandstreams.service.UserService;

public class UserController {
	
	public UserController(UserService userService) {
	
		//Post de registro de usuário
		post("/users", (req,res)->{
			//Tentamos converter de Json para User
			//Em caso de problema, imprimimos menssagem de erro
			Gson g = new Gson();
			try {
			User temp = g.fromJson(req.body(), User.class);
			return userService.registerUser(temp.getUsername(),
				temp.getEmail());
			}
			catch(Exception e){
				return new String("{\"status\":\"Operação não foi realizada com sucesso. Problema com parâmetros passados.\"}");
			}
		});
	
	}

}
