package client;

public interface ILayerToLevelAdapter {
	public void editPlatCenter(int ticket);
	
	public void editPlatCollisionBox(int ticket);
	
	public void removePlat(int ticket);
	
	public void changeDimPlat(int ticket, double wm, double hm);
}
