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
	
}
