package interactable;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Platform extends AInteractable {
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	private boolean disappears;
	private boolean moveable;
	private boolean sinkable;
	private boolean climbable;
	
	private double scK;
	private double velocity;
	/**
	 * Aspect ratio.
	 */
	private double ar;
	
	/**
	 * In terms of [cocos, m].
	 */
	private Point2D.Double endpoint;
	
	
	/**
	 * @param path path to image
	 * @param cxm in COCOS coordinates [m]
	 * @param cym in COCOS coordinates [m]
	 * @param loadedBox there exists a pre-loaded collision box
	 * @param ar aspect ratio
	 */
	public Platform (String path, double cxm, double cym, double ar) {
		// Take in the given arguments.
		this.setPath(path);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		this.ar = ar;
		System.out.println("Setting platform center to " + cxm + ", " + cym + "; cocos world meters");
		
		// Initialize the others.
		this.disappears = false;
		this.moveable = false;
		this.sinkable = false;
		this.climbable = false;
		this.scK = 1.0;
		this.velocity = 1.0;
		this.endpoint = new Point2D.Double(0, 0);						
	}
	
	public void setCollisionBox(ArrayList<Point2D.Double> points) {
		this.setCollisionPoints(points);
		System.out.println("Points: " + points.get(0).getX() + " x " + points.get(0).getY() + 
				"\n\t" + points.get(1).getX() + " x " + points.get(1).getY());
	}	
	
	/**
	 * Changes the default width and height of the platform.
	 * @param wm width (in meters)
	 * @param hm height (in meters)
	 */
	public void editPlatDim(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);			
	}
	
	/**
	 * Manually change the center of the platform.
	 * @param cxm in COCOS coordinates [m]
	 * @param cym in COCOS coordinates [m]
	 */
	public void setCenter(double cxm, double cym) {		
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
	}
	
	public void setDisappears(boolean selected) {
		this.disappears = selected;
	}
	
	public void setMoveable(boolean selected) {
		this.moveable = selected;
	}

	public void setSinkable(boolean selected) {
		this.sinkable = selected;
	}

	public void setClimbable(boolean selected) {
		this.climbable = selected;
	}
	
	public void setPhysics(double scK) {
		this.scK = scK;
	}
	
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public void setEndpoint(double x, double y) {
		this.endpoint = new Point2D.Double(x, y);
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
	
	public boolean isMoveable() {
		return this.moveable;
	}
	
	public double getEndX() {
		return this.endpoint.getX();
	}
	
	public double getEndY() {
		return this.endpoint.getY();
	}
	
	/**
	 * Set the aspect ratio.
	 * @param ar aspect ratio
	 */
	public void setAR(double ar) {
		this.ar = ar;
	}
	
	/**
	 * Get the aspect ratio.
	 * @return the aspect ratio
	 */ 
	public double getAR() {
		return this.ar;
	}
	
	public JSONObject getJSON(boolean polygon) {
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
		obj.put("imageSizeWidth", this.getInGameWidth());
		obj.put("imageSizeHeight", this.getInGameHeight());
		obj.put("disappears", this.disappears);
		obj.put("moveable", this.moveable); 
		obj.put("sinkable", this.sinkable);
		obj.put("climbable", this.climbable);
		obj.put("springCK", this.scK);
		obj.put("velocity", this.velocity);
		obj.put("endX", this.endpoint.getX());
		obj.put("endY", this.endpoint.getY());
		
		// Replace t)he below with collision points.
		JSONArray pointsList = new JSONArray();
		ArrayList<Point2D.Double> points = this.getCollisionPoints();
		points.forEach((point) -> {
			System.out.println("Collision points made initially: " + point.getX() + " x " +  point.getY());
			JSONObject couple = new JSONObject();
			couple.put("x", point.getX());
			couple.put("y", point.getY());
			pointsList.add(couple);
		});		
		
		if (polygon) {
			obj.put("collisionPoints", pointsList);	
		} else {
			if (pointsList.size() < 2) {
				System.err.println("ERROR: two points needed for bounded box");
			} else {
				Point2D.Double p1 = points.get(0);
				Point2D.Double p2 = points.get(1);
				double width = Math.abs(p1.getX() - p2.getX()); 
				double height = Math.abs(p1.getY() - p2.getY());
				obj.put("collisionWidth", width);
				obj.put("collisionHeight", height);
				
				// add to the collisionPoints anyways for easy reading in by me
				obj.put("collisionPoints", pointsList);
			}
		}
		
		return obj;
	}
}
