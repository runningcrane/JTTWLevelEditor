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

}
