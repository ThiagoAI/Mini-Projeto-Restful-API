package sensorsandstreams;

import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

public class Stream {	
	//Chave gerada interna
	String key;
	
	//Label e unidade desta stream
	String label;
	String unit;
	
	//Sensor no qual a stream foi registrada
	String sensor;
	
	//Quantas datas tem (atributo para facilitar json)
	long totalSize;
	
	//Datas publicadas nesta stream
	List<ObjectId> data;
	
	//Builder
	public Stream(String label,String unit,String sensor){
		this.label = label;
		this.unit = unit;
		this.sensor = sensor;
		this.key = UUID.randomUUID().toString();
		this.totalSize = 0;
		this.data = null;
	}

	//Setters e getters
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
}
