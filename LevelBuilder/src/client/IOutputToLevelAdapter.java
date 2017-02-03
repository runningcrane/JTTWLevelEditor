package client;

import java.awt.Component;
import java.awt.Graphics;

import org.json.simple.JSONObject;

public interface IOutputToLevelAdapter {

	/**
	 * Tells LevelManager to draw the control window on the screen.
	 * @param panel JPanel to draw
	 * @param g graphics of panel
	 */
	public void render(Component panel, Graphics g);
	
	/**
	 * Request JSON form of level.
	 * @return JSON representation of the level
	 */
	public JSONObject makeJSON();
	
	/**
	 * Update the imagery on the screen due to a manual resize by user.
	 * @param wp new width in pixels
	 * @param hp new height in pixels
	 */
	public void manualResize(double wp, double hp);
	
	/**
	 * Request a platform to be made.
	 * @param path path to image file
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 * @param wm expected width in in-game meters
	 * @param hm expected height in in-game meters
	 */
	public void makePlatform(String path, double xp, double yp, double wm, double hm);
	
}
