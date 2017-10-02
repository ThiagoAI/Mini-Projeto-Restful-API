package sensorsandstreams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

import jsontransformer.JsonUtil;

public class Sensor {
	//Chave gerada interna
	String key;
	
	//Strings que definem sensor
	String label;
	String description;
	
	//User que registrou o sensor
	String owner;
	
	//Streams deste sensor
	List<ObjectId> streams;
	
	public Sensor(String label, String description,String owner){
		this.label = label;
		this.description = description;
		this.owner = owner;
		this.key = UUID.randomUUID().toString();
		this.streams = null;
	}
	
	//getters e setters
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
