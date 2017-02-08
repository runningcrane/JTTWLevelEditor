package interactable;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import client.CollisionWindow;

public class Platform extends AInteractable {
	
	private CollisionWindow settings;
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	/**
	 * 
	 * @param path path to image
	 * @param cxm in COCOS coordinates
	 * @param cym in COCOS coordinates
	 * @param wm width (in meters)
	 * @param hm height (in meters)
	 * @param loadedBox there exists a pre-loaded collision box
	 */
	public Platform (String path, double cxm, double cym,
			double wm, double hm, boolean loadedBox) {
		this.setPath(path);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		System.out.println("Setting platform center to " + cxm + ", " + cym + "; meters");
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);
		
		this.settings = new CollisionWindow (path, wm, hm, cxm, cym);
		
		if (!loadedBox) {
			this.settings.start();
		}
	}
	
	
	public void setCollisionBox(ArrayList<Point2D.Double> points) {
		this.setCollisionPoints(points);
		this.settings.startWithPoints(points);
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
		obj.put("moveable", false); // TODO: This will change when moveability is implemented. 
		
		// Replace the below with collision points.
		JSONArray pointsList = new JSONArray();
		ArrayList<Point2D.Double> points = this.settings.returnPoints();
		points.forEach((point) -> {
			JSONObject couple = new JSONObject();
			couple.put("x", point.getX());
			couple.put("y", point.getY());
			pointsList.add(couple);
		});		
		
		if (polygon) {
			obj.put("collisionPoints", pointsList);	
		} else {
			if (pointsList.size() != 2) {
				System.err.println("ERROR: two points needed for bounded box");
			} else {
				Point2D.Double p1 = points.get(0);
				Point2D.Double p2 = points.get(1);
				double width = Math.abs(p1.getX() - p2.getX()); 
				double height = Math.abs(p1.getY() - p2.getY());
				obj.put("collisionWidth", width);
				obj.put("collisionHeight", height);
			}
		}
		
		return obj;
	}
}
