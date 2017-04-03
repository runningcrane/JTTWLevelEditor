package interactable;

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
	 * List of Point2D.Double values - collision points, in cocos frame.
	 */
	ArrayList<Point2D.Double> collPointList; 
	
	/**
	 * List of friction values for each edge.
	 * 0 corresponds to the edge between collPoints 0 and 1, etc.
	 */
	ArrayList<Double> edgeFrictionList;
	
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
		this.intList = new HashMap<>();
		this.doubList = new HashMap<>();
		this.collPointList = new ArrayList<>();
		this.edgeFrictionList = new ArrayList<>();
		this.floatList = new HashMap<>();
		this.strList = new HashMap<>();
		this.boolList = new HashMap<>();
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
	
	public ArrayList<Double> getEdgeFrictions() {
		return this.edgeFrictionList;
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
		getIntList().putAll(newBook.getIntList());
		getDoubList().putAll(newBook.getDoubList());
		getFloatList().putAll(newBook.getFloatList());
		getStringList().putAll(newBook.getStringList());
		getBoolList().putAll(newBook.getBoolList());;
	}	
}
