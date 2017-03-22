package new_interactable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

public class LevelFile {
	/**
	 * Meter-to-pixel ratio. For example, 80 means 80 pixels : 1 m
	 */
	public double mToPixel;
	
	/**
	 * EOL location.
	 */
	public Point2D.Double eol;		
	
	/**
	 * Level width (meters).
	 */
	public double lvwm;

	/**
	 * Level height (meters).
	 */
	public double lvhm;
	
	/**
	 * List of respawn points for the level.
	 */
	public ArrayList<Point2D.Double> respawnPoints; 
	
	/**
	 * Platform array.
	 */
	public Map<Integer, Platform> plats;

	/**
	 * Character array.
	 */
	public Map<String, Player> characters;

	/**
	 * Vine array.
	 */
	public Map<Integer, Vine> vines;

	/**
	 * Boulder map.
	 */
	public Map<Integer, Boulder> boulders;
	
	/**
	 * Peg list. These are connected to boulder joints.
	 */
	public Map<Integer, Peg> pegs;

	/**
	 * NPC array.
	 */
	public Map<Integer, NPC> npcs;
	
	/**
	 * Name of the currently-loaded level.
	 */
	public String levelName;
	
	/**
	 * Name of the level after this level.
	 */
	public String nextLevelName;
	
	public LevelFile() {
		
	}
}
