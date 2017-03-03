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
	 * @param levelName name of the level
	 * @param nextName name of the next level
	 * @return JSON representation of the level
	 */
	public JSONObject makeJSON(String levelName, String nextName);
	
	/**
	 * Recreate a level from its JSON.
	 * @param levelName path of the level's JSON
	 */
	public void readJSON(String levelPath);
	
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
	 * @param wm scaling of the default width-height of this platform
	 */
	public void makePlatform(String path, double xp, double yp, double scale);
	
	/**
	 * Request a vine to be made.
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 * @param wm expected width in in-game meters
	 * @param hm expected height in in-game meters
	 * @param arcl arc length (degrees)
	 */
	public void makeVine(String path, double xp, double yp, double wm, double hm, 
			double arcl, double startVel);
	
	/**
	 * Request a boulder to be made.
	 * @param path
	 * @param xp
	 * @param y
	 * @param scale
	 */
	public void makeBoulder(String path, double xp, double yp, double scale);

	/**
	 * Update a character's position.
	 * @param name name of character to edit
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 */
	public void setPlayerPosition(String name, double xp, double yp);
	
	/**
	 * Update a platform's position.
	 * @param ticket identifier of the platform
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 */
	public void editPlatCenter(int ticket, double xp, double yp);
	
	/**
	 * Update a vine's position.
	 * @param ticket identifier of the vine
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 */
	public void editVineCenter(int ticket, double xp, double yp);
	
	/**
	 * Update a boulder's position.
	 * @param ticket identifier of the boulder
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 */
	public void editBoulderCenter(int ticket, double xp, double yp);
	
	/**
	 * Update a moving platform's endpoint.
	 * @param ticket identifier of the platform
	 * @param xp x position in pixels
	 * @param yp y position in pixels
	 */
	public void setEndpointPlat(int ticket, double xp, double yp);
	
	/**
	 * Move the viewport by the specified offset.
	 * @param xm x offset in meters
	 * @param ym y offset in meters
	 */
	public void changeOffset(double xm, double ym);
	
	/**
	 * Update the EOL of the level.
	 * @param xp
	 * @param yp
	 */
	public void setEOL(double xp, double yp);
	
	/**
	 * Make a respawn point for the level.
	 * @param xp
	 * @param yp
	 */
	public void setRP(double xp, double yp);
	
	/**
	 * Remove a respawn point from the level.	
	 * @param xp
	 * @param yp
	 */
	public void removeRP(double xp, double yp);
	
}
