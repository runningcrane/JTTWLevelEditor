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
	
	/**
	 * Set the flavor text of the level.
	 * @param levelName name of the level loaded in
	 */
	public void setLevelName(String levelName);

}
