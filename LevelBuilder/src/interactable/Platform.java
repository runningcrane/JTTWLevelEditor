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
	
	public Platform (String path, double cxm, double cym,
			double wm, double hm) {
		this.setPath(path);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		System.out.println("Setting platform center to " + cxm + ", " + cym + "; meters");
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);		
		this.settings = new CollisionWindow (path, wm, hm, cxm, cym);
		this.settings.start();
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

	public JSONObject getJSON() {
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
		
		// Replace the below with collision points.
		JSONArray pointsList = new JSONArray();
		ArrayList<Point2D.Double> points = this.settings.returnPoints();
		points.forEach((point) -> {
			JSONObject couple = new JSONObject();
			couple.put("x", point.getX());
			couple.put("y", point.getY());
			pointsList.add(couple);
		});		
		obj.put("collisionPoints", pointsList);		
		
		return obj;
	}
}
