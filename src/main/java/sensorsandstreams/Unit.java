package sensorsandstreams;

public class Unit {	
	//Nome da medida
	String label;
	
	//Builder
	public Unit(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
