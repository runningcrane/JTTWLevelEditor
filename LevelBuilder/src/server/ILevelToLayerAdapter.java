package server;

public interface ILevelToLayerAdapter {
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param initial wm
	 * @param initial hm
	 */
	public void addEdit(int ticket, double wm, double hm);	
	
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
