package new_server;

import new_client.EditWindow;

public interface ILevelToLayerAdapter {

	/**
	 * Make a blank EditWindow. 
	 * @param number ticket number of object this is editing
	 * @param name type of object this is editing
	 */
	public EditWindow makeEditWindow(int number, String name);
	
	/**
	 * Clear all layers.
	 */
	public void clear();
}
