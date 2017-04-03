package client;

public interface ILayerToLevelAdapter {

	/**
	 * Remove some object from the level.
	 * @param number ticket number
	 * @param type type of the object, like Boulder or Platform
	 */
	public void removeEntity(int number, String type);	
	
	/**
	 * Request the selected group movies.
	 */
	public void groupMove();
	
	/**
	 * Request the level manager deselect all selected group objects.
	 */
	public void deselectAll();
	
	/**
	 * Request the level manager select ALL group objects.
	 */
	public void selectAll();
}
