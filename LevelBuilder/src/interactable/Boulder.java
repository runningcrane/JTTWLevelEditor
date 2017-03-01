package interactable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Boulder extends AInteractable {
	/**
	 * Path to the boulder image.
	 */
	private String path;
	
	/**
	 * Will either be POLYGON or CIRCLE.
	 * Defines whether collision box is defined by radius or points.
	 */
	private String type;
	
	/**
	 * Mass of the boulder.
	 */
	private double mass;
	
	/**
	 * Radius of the collision box if CIRCLE.
	 */
	private double radius;
	
	/**
	 * Scaling of the width/height of the default image size.
	 * For boulders, radius gets scaled too.
	 */
	private double scale;
	
	/**
	 * Makes a new boulder.
	 * @param path path to boulder image
	 * @param type type of collision
	 * @param mass mass of boulder
	 * @param cxm center x position [cocos;meters]
	 * @param cym center y position [cocos;meters]
	 */	
	public Boulder (String path, String type, double mass, double cxm, double cym,
			ArrayList<Point2D.Double> points, double radius, double scale) {
		this.path = path;
		this.type = type;
		this.mass = mass;
		this.scale = scale;
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		
		if (type.equals("POLYGON")) {
			if (points == null) {
				System.err.println("Polygon collision chosen, but no points available!");
				this.setCollisionPoints(new ArrayList<Point2D.Double>());
			} else {
				this.setCollisionPoints(points);
			}
		} else if (type.equals("CIRCLE")) {		
			this.radius = radius;
		} else {
			System.err.println("Unrecognized collision chosen!");
			this.type = "CIRCLE";
			this.radius = 3;
		}
	}
		
	public void setMass(double newMass) {
		this.mass = newMass;
	}
	
	public void setType(String newType) {
		if (newType.equals("POLYGON") || newType.equals("CIRCLE")) {
			this.type = newType;
		} else {
			System.err.println("Unrecognized boulder type processed!");
		}
	}	
	
	public JSONObject makeJSON() {
		JSONObject obj = new JSONObject();
		
		// Regex out the path to just get the image name.
		System.out.println("path: " + this.getPath());
		Pattern endOfPath = Pattern.compile("[\\w\\s]+\\.png|\\.jpg");
		Matcher m = endOfPath.matcher(this.getPath());
		String imageName;
		imageName = m.find() ? m.group() : "MATCHING ERROR";
		System.out.println("image name: " + imageName);
		
		obj.put("imageName", imageName);
		obj.put("centerX", this.getCenterXm());
		obj.put("centerY", this.getCenterYm());
		obj.put("type", this.type);
		obj.put("mass", this.mass);
		obj.put("scale",  this.scale);	
		
		if (this.type.equals("POLYGON")) {
			JSONArray pointsList = new JSONArray();
			ArrayList<Point2D.Double> points = this.getCollisionPoints();
			points.forEach((point) -> {
				System.out.println("Collision points made initially: " + point.getX() * this.scale + " x " +  point.getY() * this.scale);
				JSONObject couple = new JSONObject();
				couple.put("x", point.getX() * this.scale);
				couple.put("y", point.getY() * this.scale);
				pointsList.add(couple);
			});		
			
			obj.put("points", pointsList);
		} else {
			obj.put("radius", this.radius * this.scale);
		}
		
		return obj;
	}

}
