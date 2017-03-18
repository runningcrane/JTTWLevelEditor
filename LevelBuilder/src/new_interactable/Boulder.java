package new_interactable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Boulder {
	
	/**
	 * Cocos, meters location on the x-axis.
	 */
	private double centerXM;
	
	/**
	 * Cocos, meters location on the y-axis.
	 */
	private double centerYM;
/*
 * 		// Regex out the path to just get the image name.
		System.out.println("path: " + this.getPath());
		Pattern endOfPath = Pattern.compile("[\\w\\s]+\\.png|\\.jpg");
		Matcher m = endOfPath.matcher(this.getPath());
		String imageName;
		imageName = m.find() ? m.group() : "MATCHING ERROR";
		System.out.println("image name: " + imageName);
		
		obj.put("imageName", imageName);
		obj.put("centerX", this.getCenterXm());
		obj.put("centerY", this.getCenterYm());
		obj.put("imageSizeWidth", this.scaledIGWM);
		obj.put("imageSizeHeight", this.scaledIGHM);
		obj.put("type", this.type);
		obj.put("mass", this.mass);
		obj.put("scale",  this.scale);	
		
		// Replace the old ticket value.
		System.out.println("old: " + this.oldTicket + ", new: " + this.newTicket);
		this.oldTicket = this.newTicket;
		
		obj.put("ticket", this.oldTicket);
		
		JSONArray pointsList = new JSONArray();
		ArrayList<Point2D.Double> points = this.getCollisionPoints();
		points.forEach((point) -> {
			System.out.println("Collision points made initially: " + point.getX() * this.scale + " x " +  point.getY() * this.scale);
			JSONObject couple = new JSONObject();
			couple.put("x", point.getX() * this.scale);
			couple.put("y", point.getY() * this.scale);
			pointsList.add(couple);
		});		
		
		obj.put("collisionPoints", pointsList);
	
		obj.put("radius", this.radius * this.scale);
 */
	/**
	 * PropertyBook of this object.
	 */
	private PropertyBook book;
	
	/**
	 * Ticket number of this object.
	 */
	private int ticket;
	
	/**
	 * Makes a basic boulder.
	 * @param ticket identifier
	 * @param imageName name of the file that has the boulder image
	 */
	public Boulder(int ticket, String imageName) {
		this.ticket = ticket;
		
		// TODO: Set up imageName
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
			if (this.book.getIntList().containsKey(name)) {
				this.book.getIntList().replace(name, value);
			} else {
				this.book.getIntList().put(name, value);
			}
		});
		
		// Doubles.
		newBook.getDoubList().forEach((name, value) -> {
			if (this.book.getDoubList().containsKey(name)) {
				this.book.getDoubList().replace(name, value);
			} else {
				this.book.getDoubList().put(name, value);
			}
		});
		
		// Float.
		newBook.getFloatList().forEach((name, value) -> {
			if (this.book.getFloatList().containsKey(name)) {
				this.book.getFloatList().replace(name, value);
			} else {
				this.book.getFloatList().put(name, value);
			}
		});
		
		// Strings.
		newBook.getStringList().forEach((name, value) -> {
			if (this.book.getStringList().containsKey(name)) {
				this.book.getStringList().replace(name, value);
			} else {
				this.book.getStringList().put(name, value);
			}
		});
		
		// Booleans.
		newBook.getBoolList().forEach((name, value) -> {
			if (this.book.getBoolList().containsKey(name)) {
				this.book.getBoolList().replace(name, value);
			} else {
				this.book.getBoolList().put(name, value);
			}
		});
	}
	
	/**
	 * Change where this object is located.
	 * @param x [cocos,m] coordinates
	 * @param y [cocos,m] coordinates
	 */
	public void setCenter(double x, double y) {
		this.centerXM = x;
		this.centerYM = y;
	}
}
