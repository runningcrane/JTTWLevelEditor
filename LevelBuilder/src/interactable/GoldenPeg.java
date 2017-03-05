package interactable;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GoldenPeg extends AInteractable {
	/**
	 * In radians.
	 */
	double rotation;
	
	int jointID;
	
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	private double scale;
	
	/**
	 * Scaled in-game width [m]. 
	 */
	private double scaledIGWM;
	
	/**
	 * Scaled in-game height [m].
	 */
	private double scaledIGHM;
	
	public GoldenPeg(String path, double cxm, double cym, double scale, int jointID, double rotation) {
		this.setPath(path);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		this.scale = scale;
		this.jointID = jointID;
		this.rotation = rotation;
	}
	
	/**
	 * Manually change the center of the platform.
	 * @param cxm in COCOS coordinates [m]
	 * @param cym in COCOS coordinates [m]
	 */
	public void setCenter(double cxm, double cym) {		
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		System.out.println("Center made: " + this.getCenterXm() + ", " + this.getCenterYm());
	}
	
	public void editPegDim(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);			
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
	
	/**
	 * Set the scale.
	 * @param scale new scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
		this.scaledIGWM = scale * this.getInGameWidth();
		this.scaledIGHM = scale * this.getInGameHeight();
		System.out.println("New wm x hm: " + this.scaledIGWM + ", " + this.scaledIGHM);
	}
	
	public double getScaledIGW() {
		return this.scaledIGWM;
	}
	
	public double getScaledIGH() {
		return this.scaledIGHM;
	}
	
	public void editRotation(double rot) {
		this.rotation = rot;
	}
	
	public void editPegJointID(int jid) {
		this.jointID = jid;
	}
	
	public int getJointID() {
		return this.jointID;
	}
	
	/**
	 * Get the scale of the image.
	 * @return the scale of the wm & hm
	 */ 
	public double getScale() {
		return this.scale;
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
		obj.put("imageWidth", this.scaledIGWM);
		obj.put("imageHeight", this.scaledIGHM);
		obj.put("scale", this.scale);
		obj.put("rotation", this.rotation);
		obj.put("jointID", this.jointID);		
		
		return obj;
	}
}
