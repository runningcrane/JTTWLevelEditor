package interactable;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

public class Vine extends AInteractable {
	
	/**
	 * Length of vine in [cocos, m]. It is also hm.
	 */
	private double length;
	
	/**
	 * How big an arc this vine can make in [degrees].
	 */
	private double arcLimit;
	
	private double startVel;
	
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	public Vine (String path, double cxm, double cym,
			double wm, double hm, double arcLimit, double startingVelocity) {
		this.setPath(path);
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);
		
		/*
		 * In this class, cxm and cym do not refer to the middle of the image.
		 * They refer to the center point that the vine rotates around.
		 * i.e. If the vine is hanging straight down, cxm and cym mark the spot
		 * it is hanging down from.
		 */
		this.setCenterXm(cxm);
		this.setCenterYm(cym);		
		this.length = hm;
		this.arcLimit = arcLimit;
		this.startVel = startingVelocity;
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
	
	public void editVineDim(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);	
	}
	
	public void editVineArcl(double arcl) {
		this.arcLimit = arcl;
	}
	
	public void editVineStartVel(double startVel) {
		this.startVel = startVel;
	}
	
	/**
	 * Manually change the center of the vine.
	 * @param cxm in COCOS coordinates [m]
	 * @param cym in COCOS coordinates [m]
	 */
	public void setCenter(double cxm, double cym) {		
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
	}
	
	public JSONObject getJSON() {
		JSONObject obj = new JSONObject();
		obj.put("imageName", this.getPath());
		obj.put("swingCenterX", this.getCenterXm());
		obj.put("swingCenterY", this.getCenterYm());
		obj.put("width", this.getInGameWidth());
		obj.put("length", this.length);
		obj.put("arcLimit", this.arcLimit);
		obj.put("startingVelocity", this.startVel);
		
		return obj;
	}
}
