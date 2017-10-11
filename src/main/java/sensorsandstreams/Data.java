package sensorsandstreams;

public class Data {
	//Timestamp e o valor da medição
	long timestamp;
	double value;
	
	//Chave da stream desta data
	transient String stream;
	
	public Data(long timestamp, Double value,String stream) {
		this.timestamp = timestamp;
		this.value = value;
		this.stream = stream;
	}

	//Getters e setters
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
}
