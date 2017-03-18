package new_client;

public interface ILayerToLevelAdapter {

	/**
	 * Remove some object from the level.
	 * @param number ticket number
	 * @param type type of the object, like Boulder or Platform
	 */
	public void removeEntity(int number, String type);	
}
