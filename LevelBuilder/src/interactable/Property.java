package interactable;

public class Property<U> { 
	
	private Class<U> classType;
	private Object value;
	
	public Property(Class<U> classType, Object value) {
		this.classType = classType;
		this.value = value;
	}	
}
