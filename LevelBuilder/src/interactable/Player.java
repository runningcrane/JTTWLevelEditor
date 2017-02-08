package interactable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import client.CollisionWindow;

public class Player extends Character {
	private boolean present;
	
	public Player (String path, String name, double cxm, double cym, boolean present) {		
		this.setPath(path);		
		this.setName(name);
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
		this.present = present;
		
		System.out.println("Setting " + name + " center to " + cxm + ", " + cym + "; meters");
		this.setInGameWidth(0.7);
		this.setInGameHeight(1.7);				
				
	}
	
	public Player() {
		this.present = false;
	}
	
	public void setPresent(boolean present) {
		this.present = present;		
	}
	
	public void setCenter(double cxm, double cym) {
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
	}
	
	public JSONObject getJSON() {		
		JSONObject properties = new JSONObject();
		
		properties.put("present", this.present);
		properties.put("startingXPos", this.getCenterXm());
		properties.put("startingYPos", this.getCenterYm());
		
		return properties;
	}

}
