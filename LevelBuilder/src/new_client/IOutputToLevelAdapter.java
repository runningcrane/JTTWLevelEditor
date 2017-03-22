package new_client;

import java.awt.Component;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

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
	public String makeJSON(String levelName, String nextName);
	
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
	 * Sends an (x,y) pair in the context of the frame; offset not taken into account.
	 * @param xp [eclipse, p] coordinate x
	 * @param yp [eclipse, p] coordinate y
	 */
	public void sendCoordinates(double xp, double yp);
	
	/**
	 * Move the viewport by the specified offset.
	 * @param xm x offset in meters
	 * @param ym y offset in meters
	 */
	public void changeOffset(double xm, double ym);
	
}
