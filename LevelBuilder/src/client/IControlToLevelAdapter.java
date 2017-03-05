package client;

import java.awt.Component;
import java.awt.Graphics;

/**
 * Adapter from the ControlWindow to the LevelManager.
 * @author Melinda Crane
 */
public interface IControlToLevelAdapter {				
	
	public void setMToPixel(double mToPixel);
	
	/**
	 * Request to resize actual level.
	 * @param wm width in meters
	 * @param hm height in meters
	 */
	public void setLevelDimensions(double wm, double hm);	
	
	/**
	 * Request to resize OutputWindow.
	 * @param wm width in meters
	 * @param hm height in meters
	 */
	public void setViewportDimensions(double wm, double hm);
	
	/**
	 * Toggle whether a character is in this scene.
	 * @param name name of character to toggle existence
	 * @param status state to toggle to
	 */
	public void togglePlayer (String name, boolean status);
	
	/**
	 * Request to change background of OutputWindow.
	 * @param path path to background file
	 */
	public void setBg(String path);
	
	/**
	 * Request to change the EOL position.
	 */
	public void markEOL();
	
	/**
	 * Request a new platform to be added.
	 * @param path
	 */
	public void makePlatform(String path);
	
	/**
	 * Request a new vine to be added.
	 * @param path
	 */
	public void makeVine(String path);
	
	public void makePeg(String path);
	
	/**
	 * Request a new rock to be added.
	 * @param path
	 */
	public void makeRock(String path);
	
	public void markRP();
	
	public void removeRP();
	
	public void makeBoulderJoint();

}
