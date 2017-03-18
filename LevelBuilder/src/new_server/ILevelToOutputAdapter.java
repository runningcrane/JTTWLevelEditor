package new_server;

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
	 * Request coordinates from the output window.
	 */
	public void requestCoordinates();

}
