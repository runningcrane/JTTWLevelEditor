package server;

public interface ILevelToLayerAdapter {
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param initial wm
	 * @param initial hm
	 */
	public void addPlatformEdit(int ticket, double wm, double hm);
	
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param initial wm
	 * @param initial hm
	 * @param initial arcLength
	 */
	public void addVineEdit(int ticket, double wm, double hm, double arcLength);
	
	/**
	 * Remove all edit panels.
	 */
	public void removeAllWindows();
	
	public void setDimensions(int ticket, double wm, double hm);
	
	public void setDisappears(int ticket, boolean selected);
	
	public void setMoveable(int ticket, boolean selected);
	
	public void setClimbable(int ticket, boolean selected);
	
	public void setSinkable(int ticket, boolean selected);
	
	public void setSCK(int ticket, double scK);
	
	public void setVelocity(int ticket, double velocity);
}
