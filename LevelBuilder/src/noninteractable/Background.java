package noninteractable;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

public class Background extends ANonInteractable {
	
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	
	public Background(BufferedImage image, String path, double wm, double hm) {
		this.image = image;
		this.setPath(path);
		this.setImageSizeWidth(wm);
		this.setImageSizeHeight(hm);
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
		this.setImageSizeWidth(wm);
		this.setImageSizeHeight(hm);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject obj = new JSONObject();
		// TODO: Regex out the assets/blahblah/image.png in JSON
		obj.put("imageName", this.getPath());
		obj.put("centerX", 0);
		obj.put("centerY", 0);
		obj.put("imageSizeWidth", 0);
		obj.put("imageSizeHeight", 0);
		obj.put("collisionWidth", 0);
		obj.put("collisionHeight", 0);	
		
		return obj;
	}
	
	public JSONObject anticipatedJSON() {
		// TODO: Regex out the assets/blahblah/image.png in JSON
		JSONObject obj = new JSONObject();
		return obj;
	}

}
