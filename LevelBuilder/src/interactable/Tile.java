package interactable;

import java.awt.Point;
import java.util.HashMap;

public class Tile {		
	private int x;
	private int y;	
	private HashMap<String, Property<?>> properties;
	private IInteractable inter;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		this.properties = new HashMap<String, Property<?>>();
		
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point getPosition(){
		return new Point(x,y);
	}
	
	public void setInteractable(IInteractable inter) {
		this.inter = inter;
	}
	
	public void setProperty(String propertyName, Property<?> propertyValue) {
		if (!this.properties.containsKey(propertyName)) {
			this.properties.replace(propertyName, propertyValue);
		} else {
			this.properties.put(propertyName, propertyValue);
		}
	}	
}
