package interactable;

import java.awt.Image;
import java.awt.geom.Point2D.Double;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

public class Platform extends AInteractable {		
	
	public Platform (String path, double cxm, double cym,
			double wm, double hm, double[] collisionPoints) {
		this.setPath(path);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		this.setInGameWidth(wm);
		this.setInGameHeight(hm);
		this.setCollisionPoints(collisionPoints);
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
		obj.put("collisionWidth", 0);
		obj.put("collisionHeight", 0);	
		
		return obj;
	}
}
