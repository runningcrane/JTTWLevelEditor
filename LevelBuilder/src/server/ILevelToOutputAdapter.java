package server;

public interface ILevelToOutputAdapter {
	
	/**
	 * Set dimensions of the level's JFrame.
	 * @param wm width in pixels
	 * @param hm height in pixels
	 */
	public void setDimensions(int wm, int hm);
	
	/**
	 * Refresh the level image.
	 */
	public void redraw();
	
	/**
	 * Listen in for the position of a new platform with the given path.
	 * @param path path to platform image
	 */
	public void makePlatform(String path);		
	
	public void makeVine(String path);
	
	public void makeRock(String path);
	
	public void makeBoulderJoint();
	
	/**
	 * Set the flavor text of the level.
	 * @param levelName name of the level loaded in
	 */
	public void setLevelName(String levelName);
	
	/**
	 * Set the flavor text of what the next level is.
	 * @param nextName name of the next level following this
	 */
	public void setNextName(String nextName);
	
	/**
	 * Character name to edit position of. 
	 * @param playerName name of the player to edit
	 */
	public void setCharPos(String playerName);
	
	public void setPlatPos(int ticket);
	
	public void setVinePos(int ticket);
	
	public void setBoulderPos(int ticket);
	
	public void markEOL();
	
	public void markRP();
	
	public void makeEndpointPlat(int ticket);
	
	public void requestRemoveRP();

}
