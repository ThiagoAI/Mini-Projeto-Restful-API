package sensorsandstreams;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import com.mongodb.spark.sql.fieldTypes.api.java.ObjectId;

public class User {
	
	//Nome e email
	String username;
	String email;
	
	//Sensores deste usuário
	List<ObjectId> sensors;
	
	//Builder
	public User(String username,String email){
		this.username = username;
		this.email = email;
		this.sensors = null;
	}
	
	//getters and setters
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
