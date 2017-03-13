package new_client;

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
	
	Map<String, Integer> getIntList() {
		return this.intList;
	}
	
	Map<String, Double> getDoubList() {
		return this.doubList;
	}
	
	Map<String, Float> getFloatList() {
		return this.floatList;
	}
	
	Map<String, String> getStringList() {
		return this.strList;
	}
	
	Map<String, Boolean> getBoolList() {
		return this.boolList;
	}
}
