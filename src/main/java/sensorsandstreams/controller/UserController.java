package sensorsandstreams.controller;

import static spark.Spark.post;

import com.google.gson.Gson;

import sensorsandstreams.User;
import sensorsandstreams.service.UserService;

public class UserController {
	
	public UserController(UserService userService) {
	
		post("/users", (req,res)->{
			Gson g = new Gson();
			User temp = g.fromJson(req.body(), User.class);
		
			return userService.registerUser(temp.getUsername(),
				temp.getEmail());
		});
	
	}

}
