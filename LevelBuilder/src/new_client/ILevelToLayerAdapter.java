package new_client;

public interface ILevelToLayerAdapter {

	/**
	 * Make a blank EditWindow. 
	 * @param number ticket number of object this is editing
	 * @param name type of object this is editing
	 */
	EditWindow makeEditWindow(int number, String name);
}
