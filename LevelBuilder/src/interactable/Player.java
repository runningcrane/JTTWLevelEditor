package interactable;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import org.json.simple.JSONObject;

public class Player extends Character {
	private boolean present;
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	/**
	 * Make a new Player Character.
	 * @param path path to the imagefile.
	 * @param name name of character
	 * @param cxm x position in meters [COCOS coordinates]
	 * @param cym y position in meters [COCOS coordinates]
	 * @param present whether the player is part of the level or not
	 */
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

	/**
	 * Get image representing the player.
	 * @return player image
	 */
	public BufferedImage getImage() {
		return this.image;
	}	
	
	/**
	 * Set the image that represents this player.
	 * @param image player image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}	
	
	/**
	 * Get the rescaled image for rendering on the output window.
	 * @return the rescaled image
	 */
	public ImageIcon getRescaled() {
		return this.rescaledImage;
	}
	
	/**
	 * Sets the rescaled image for rendering on the output window.
	 * @param rescaled rescaled image
	 */
	public void setRescaled(ImageIcon rescaled) {
		this.rescaledImage = rescaled;
	}
	
	/**
	 * Set whether the character is present in the level or not.
	 * @param present boolean that is true if the character is present
	 */
	public void setPresent(boolean present) {
		this.present = present;		
	}
	
	/**
	 * Returns whether the character is present in the level or not.
	 * @return boolean that is true if the character is present
	 */
	public boolean getPresent() {
		return this.present;
	}
	
	/**
	 * Set where the center of the character spawns. 
	 * TODO: This might change to feet in the future to avoid mental math.
	 * @param cxm in COCOS coordinates
	 * @param cym in COCOS coordinates
	 */
	public void setCenter(double cxm, double cym) {
		this.setCenterXm(cxm);
		this.setCenterYm(cym);
	}
	
	/**
	 * Get JSON of this player.
	 * @return JSON representation of the player
	 */
	public JSONObject getJSON() {		
		JSONObject properties = new JSONObject();
		
		properties.put("present", this.present);
		properties.put("startingXPos", this.getCenterXm());
		properties.put("startingYPos", this.getCenterYm());
		
		return properties;
	}

}
