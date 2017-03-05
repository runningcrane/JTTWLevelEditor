package client;

public interface ILayerToLevelAdapter {
	public void editPlatCenter(int ticket);
	
	public void editPlatScale(int ticket, double scale);
	
	public void editBoulderScale(int ticket, double scale);
	
	public void editBoulderMass(int ticket, double mass);
	
	public void editVineArcl(int ticket, double arcl);
	
	public void editBoulderCenter(int ticket);
	
	public void editPegScale(int ticket, double scale);
	
	public void editBJOff(int ticket, double obx1, double oby1, double obx2, double oby2);
	
	public void editVineCenter(int ticket);
	
	public void editPegCenter(int ticket);
	
	public void editPegJointID(int ticket, int jid);
	
	public void editVineStartVel(int ticket, double startVel);
	
	public void editPegRotation(int ticket, double rotation);
	
	public void changeDimVine(int ticket, double wm, double hm);	
	
	public void changeBoulderJoint(int ticket, int b1, int b2);
	
	public void removeBoulder(int ticket);
	
	public void removeJoint(int ticket);
	
	public void removePeg(int ticket);
	
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
