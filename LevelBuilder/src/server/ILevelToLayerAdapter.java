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
}
