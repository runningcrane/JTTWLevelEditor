package new_interactable;

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
	
	public Map<String, Float> getFloatList() {
		return this.floatList;
	}
	
	public Map<String, String> getStringList() {
		return this.strList;
	}
	
	public Map<String, Boolean> getBoolList() {
		return this.boolList;
	}
}
