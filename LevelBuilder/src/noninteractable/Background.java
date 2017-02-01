package noninteractable;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

public class Background extends ANonInteractable {
	
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	
	public Background(BufferedImage image, String path, double wm, double hm) {
		this.image = image;
		this.setPath(path);
		
		// Set dimension-related fields
		this.setInGameWidth(wm);
		this.setCenterXm(wm/2);		
		this.setInGameHeight(hm);
		this.setCenterYm(hm/2);
	}
	
	public BufferedImage getImage() {
		return this.image;
	}	
	
	public ImageIcon getRescaled() {
		return this.rescaledImage;
	}
	
	public void setRescaled(ImageIcon rescaled) {
		this.rescaledImage = rescaled;
	}
	
	public void setDimensions(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setCenterXm(wm/2);
		this.setInGameHeight(hm);
		this.setCenterYm(hm/2);
	}
	
	@SuppressWarnings("unchecked") // Assuming we are using JSON correctly here.
	@Override
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
		obj.put("collisionWidth", 0);
		obj.put("collisionHeight", 0);	
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject anticipatedJSON() {
		// TODO: Regex out the assets/blahblah/image.png in JSON
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
		obj.put("levelWidth", this.getInGameWidth());
		obj.put("levelHeight", this.getInGameHeight());
		return obj;
	}

}
