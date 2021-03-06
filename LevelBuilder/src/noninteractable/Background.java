package noninteractable;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import utils.annotations.Exclude;

public class Background extends ANonInteractable {
	
	@Exclude
	private BufferedImage image;
	
	@Exclude
	private BufferedImage rescaledImage;
	
	public Background(BufferedImage image, String path, double wm, double hm) {
		this.image = image;
		this.setPath(path);
		
		// Set dimension-related fields
		System.out.println("Background size now " + wm + " x " + hm);
		this.setInGameWidth(wm);	
		this.setInGameHeight(hm);
	}
	
	public BufferedImage getImage() {
		return this.image;
	}	
	
	public BufferedImage getRescaled() {
		return this.rescaledImage;
	}
	
	public void setRescaled(BufferedImage rescaled) {
		this.rescaledImage = rescaled;
	}
	
	public void setDimensions(double wm, double hm) {
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);
	}	
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSON() {
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
		obj.put("width", this.getInGameWidth());
		obj.put("height", this.getInGameHeight());
		return obj;
	}

}
