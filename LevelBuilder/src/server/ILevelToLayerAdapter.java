package server;

public interface ILevelToLayerAdapter {
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param initial wm
	 * @param initial hm
	 * @param scale of the wm & hm
	 */
	public void addPlatformEdit(int ticket, double wm, double hm, double scale);
	
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param initial wm
	 * @param initial hm
	 * @param initial arcLength
	 * @param initial starting velocity
	 */
	public void addVineEdit(int ticket, double wm, double hm, double arcLength, double startVel);
	
	public void addPegEdit(int ticket, double rotation, int jid, double scale);
	
	/**
	 * Add an edit panel.
	 * @param ticket
	 * @param radius
	 * @param scale
	 * @param mass
	 */
	public void addBoulderEdit(int ticket, double radius, double mass, double scale);
	
	public void addJointsEdit(int ticket, int id1, int id2, double obx1, double oby1, double obx2, double oby2);
	
	/**
	 * Remove all edit panels.
	 */
	public void removeAllWindows();
	
	public void setDimensions(int ticket, double scale);
	
	public void setDisappears(int ticket, boolean selected);
	
	public void setMoveable(int ticket, boolean selected);
	
	public void setClimbable(int ticket, boolean selected);
	
	public void setCollidable(int ticket, boolean selected);
	
	public void setSinkable(int ticket, boolean selected);
	
	public void setPolygonBoulder(int ticket, boolean selected);
	
	public void setSCK(int ticket, double scK);
	
	public void setVelocity(int ticket, double velocity);
}
