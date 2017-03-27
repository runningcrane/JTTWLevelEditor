package new_interactable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyBook {

	/**
	 * Map of property IDs to integer values.
	 */
	Map<String, Integer> intList;
	
	/**
	 * Map of property IDs to double values.
	 */
	Map<String, Double> doubList;
	
	/**
	 * List of Point2D.Double values - collision points.
	 */
	ArrayList<Point2D.Double> collPointList; 
	
	/**
	 * Map of property IDs to float values.
	 */
	Map<String, Float> floatList;
	
	/**
	 * Map of property IDs to String values.
	 */
	Map<String, String> strList;
	
	/**
	 * Map of property IDs to boolean values.
	 */
	Map<String, Boolean> boolList;	
	
	public PropertyBook() {
		this.intList = new HashMap<String, Integer>();
		this.doubList = new HashMap<String, Double>();
		this.collPointList = new ArrayList<Point2D.Double>();
		this.floatList = new HashMap<String, Float>();
		this.strList = new HashMap<String, String>();
		this.boolList = new HashMap<String, Boolean>();
	}
	
	public Map<String, Integer> getIntList() {
		return this.intList;
	}
	
	public Map<String, Double> getDoubList() {
		return this.doubList;
	}
	
	public ArrayList<Point2D.Double> getCollPoints() {
		return this.collPointList;
	}
	
	public Map<String, Float> getFloatList() {
		return this.floatList;
	}
	
	public Map<String, String> getStringList() {
		return this.strList;
	}
	
	public Map<String, Boolean> getBoolList() {
		return this.boolList;
	}
	
	/**
	 * Updates the properties of this object. 
	 * If the new property book contains properties not in this book,
	 * those properties will be added.
	 * @param newBook new book of properties
	 */
	public void updateProperties(PropertyBook newBook) {				
		// Integers.		
		newBook.getIntList().forEach((name, value) -> {
			if (getIntList().containsKey(name)) {
				getIntList().replace(name, value);
			} else {
				getIntList().put(name, value);
			}
		});
		
		// Doubles.
		newBook.getDoubList().forEach((name, value) -> {
			if (getDoubList().containsKey(name)) {
				getDoubList().replace(name, value);
			} else {
				getDoubList().put(name, value);
			}
		});
		
		// Float.
		newBook.getFloatList().forEach((name, value) -> {
			if (getFloatList().containsKey(name)) {
				getFloatList().replace(name, value);
			} else {
				getFloatList().put(name, value);
			}
		});
		
		// Strings.
		newBook.getStringList().forEach((name, value) -> {
			if (getStringList().containsKey(name)) {
				getStringList().replace(name, value);
			} else {
				getStringList().put(name, value);
			}
		});
		
		// Booleans.
		newBook.getBoolList().forEach((name, value) -> {
			if (getBoolList().containsKey(name)) {
				getBoolList().replace(name, value);
			} else {
				getBoolList().put(name, value);
			}
		});
	}	
}
