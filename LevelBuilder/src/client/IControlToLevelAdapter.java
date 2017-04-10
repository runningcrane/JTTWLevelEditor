package client;

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
	 * Request to resize the viewport limits (all units in meters).
	 */
    public void setViewportLimits(double xmin, double xmax, double ymin, double ymax);
	
	/**
	 * Make present the character and request a new position for them.
	 */
	public void toggleMonk();
	
	/**
	 * Make present the character and request a new position for them.
	 */
	public void toggleMonkey();
	
	/**
	 * Make present the character and request a new position for them.
	 */
	public void togglePiggy();
	
	/**
	 * Make present the character and request a new position for them.
	 */
	public void toggleSandy();
	
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
	
	public void makeTrap(String path);
	
	/**
	 * Request a new boulder to be added.
	 * @param path
	 */
	public void makeBoulder(String path);
	
	/**
	 * Request a new text tip to be added.
	 */
	public void makeTextTip();
	
	public void markRP();
	
	public void removeRP();

	public void makeAttackZone(String string);

	public void makeQuicksand(String string);
}
