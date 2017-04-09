package server;

import client.EditWindow;

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
	
	public static ILevelToLayerAdapter VoidPattern = new ILevelToLayerAdapter(){

		@Override
		public EditWindow makeEditWindow(int number, String name) { 
			return new EditWindow("?", 2); 
		}

		@Override
		public void clear() { /* do nothing */ }
		
	};
}
