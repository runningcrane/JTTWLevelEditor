package client;

public interface ILayerToLevelAdapter {
	public void editPlatCenter(int ticket);
	
	public void editPlatCollisionBox(int ticket);
	
	public void editVineArcl(int ticket, double arcl);
	
	public void editVineCenter(int ticket);
	
	public void changeDimVine(int ticket, double wm, double hm);
	
	public void changeDimPlat(int ticket, double wm, double hm);
	
	public void removePlat(int ticket);
	
	public void removeVine(int ticket);
	
	public void toggleDisappearsPlat(int ticket, boolean selected);
	
	public void makeEndpointPlat(int ticket);
	
	public void toggleMoveablePlat(int ticket, boolean selected);
		
	public void toggleSinkablePlat(int ticket, boolean selected);
	
	public void toggleClimbablePlat(int ticket, boolean selected);
	
	public void setPhysicsPlat(int ticket, double scK);
	
	public void setVelocityPlat(int ticket, double velocity);
}
