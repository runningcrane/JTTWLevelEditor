package interactable;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Boulder extends AInteractable {
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
	 * Original image.
	 */
	private BufferedImage image;
	
	/**
	 * Rescaled image for output on level editor.
	 */
	private ImageIcon rescaledImage;
	
	/**
	 * Scaled in-game width [m]. 
	 */
	private double scaledIGWM;
	
	/**
	 * Scaled in-game height [m].
	 */
	private double scaledIGHM;
	
	private int oldTicket;
	
	private int newTicket;
	
	/**
	 * Makes a new boulder.
	 * @param path path to boulder image
	 * @param cxm center x position [cocos;meters]
	 * @param cym center y position [cocos;meters]
	 */	
	public Boulder (String path, double cxm, double cym, double scale, int oldTicket) {
		this.setPath(path);
		this.scale = scale;
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		this.oldTicket = oldTicket;
		System.out.println("Cocos[m] center set to " + this.getCenterXm() + ", " + this.getCenterYm());
	}
	
	public void setNewTicket(int newTicket) {
		this.newTicket = newTicket;
	}
		
	public void setOldTicket(int oldTicket) {
		this.oldTicket = oldTicket;
	}
	
	/**
	 * Return the scaled radius.
	 * @return scaled radius
	 */
	public double getScaledRadius() {
		return this.radius * this.scale;
	}
	
	/**
	 * Return the boulder's radius.
	 * @return radius
	 */
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * Set the mass of the boulder.
	 * @param newMass mass of boulder
	 */
	public void setMass(double newMass) {
		this.mass = newMass;
	}
	
	/**
	 * Return the mass of the boulder.
	 * @return mass of the boulder [cocos]
	 */
	public double getMass() {
		return this.mass;
	}
	
	/**
	 * Set the scale for this boulder.
	 * @param scale amount the represented boulder should be changed from default sizes
	 */
	public void setScale(double scale) {
		this.scale = scale;
		this.scaledIGWM = scale * this.getInGameWidth();
		this.scaledIGHM = scale * this.getInGameHeight();
	}
	
	/**
	 * Return the scale for this object.
	 * @return scale of the boulder's dimensions
	 */
	public double getScale() {
		return this.scale;
	}
	
	/**
	 * Changes the default width and height of the platform.
	 * @param wm width (in meters)
	 * @param hm height (in meters)
	 */
	public void editBoulderDim(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);			
	}
	
	public void setDefaults(ArrayList<Point2D.Double> points, double radius) {
		this.setCollisionPoints(points);
		this.radius = radius;		
	}
	
	/**
	 * Set the type of collision this boulder will use.
	 * true = POLYGON
	 * false = CIRCLE
	 * @param newType type of collision
	 */
	public void toggleType(boolean type) {
		if (type) {
			this.type = "POLYGON";
		} else {
			this.type = "CIRCLE";
		}
	}	
	
	/**
	 * Returns the string representation of what type of collision the boulder uses.
	 * @return type of collision boulder uses
	 */
	public String getType() {
		return this.type;
	}
	
	public BufferedImage getImage() {
		return this.image;
	}	
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}	
	
	public ImageIcon getRescaled() {
		return this.rescaledImage;
	}
	
	public void setRescaled(ImageIcon rescaled) {
		this.rescaledImage = rescaled;
	}
	
	public double getScaledIGW() {
		return this.scaledIGWM;
	}
	
	public double getScaledIGH() {
		return this.scaledIGHM;
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
	
		
		return obj;
	}

}
