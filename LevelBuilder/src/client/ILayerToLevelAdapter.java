package client;

public interface ILayerToLevelAdapter {
	public void editPlatCenter(int ticket);
	
	public void editPlatScale(int ticket, double scale);
	
	public void editBoulderScale(int ticket, double scale);
	
	public void editBoulderMass(int ticket, double mass);
	
	public void editVineArcl(int ticket, double arcl);
	
	public void editBoulderCenter(int ticket);
	
	public void editVineCenter(int ticket);
	
	public void editVineStartVel(int ticket, double startVel);
	
	public void changeDimVine(int ticket, double wm, double hm);	
	
	public void removeBoulder(int ticket);
	
	public void removePlat(int ticket);
	
	public void removeVine(int ticket);
	
	public void toggleDisappearsPlat(int ticket, boolean selected);
	
	public void makeEndpointPlat(int ticket);
	
	public void toggleMoveablePlat(int ticket, boolean selected);
		
	public void toggleSinkablePlat(int ticket, boolean selected);
	
	public void toggleClimbablePlat(int ticket, boolean selected);
	
	public void toggleCollidablePlat(int ticket, boolean selected);
	
	public void togglePolygonBoulder(int ticket, boolean selected);
	
	public void setPhysicsPlat(int ticket, double scK);
	
	public void setVelocityPlat(int ticket, double velocity);
}
