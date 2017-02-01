package noninteractable;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

public class Background extends ANonInteractable {
	
	private BufferedImage image;
	
	public Background(BufferedImage image, String imageName) {
		this.image = image;
		// TOOD: Regex out the assets/blahblah/image.png
		this.setImageName(imageName);
	}
	public BufferedImage getImage() {
		return this.image;
	}	
	@Override
	public JSONObject getJSON() {
		JSONObject obj = new JSONObject();
		obj.put("imageName", this.getImageName());
		obj.put("centerX", 0);
		obj.put("centerY", 0);
		obj.put("imageSizeWidth", 0);
		obj.put("imageSizeHeight", 0);
		obj.put("collisionWidth", 0);
		obj.put("collisionHeight", 0);	
		
		return obj;
	}

}
