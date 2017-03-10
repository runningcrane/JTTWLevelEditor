package utils;

public class Constants {
	/**
	 * The Path that leads to all of the image assets used by both the level 
	 * editor and the game itself. The '..' at the beginning is because the working
	 * directory is set to be ${workspace}/bin.
	 */
	public static final String ASSETS_PATH = "../assets/";
	
	/**
	 * The Path that leads to all of the json collision files made by
	 * the collision editor (collision.CollisionWindow.java). The name of each 
	 * file corresponds to the name of it's image asset in ASSETS_PATH, but
	 * with a json extension instead of png.
	 * The '..' at the beginning is because the working directory is set 
	 * to be ${workspace}/bin.
	 */
	public static final String COL_PATH = "../collisions/";
	
	/**
	 * The path that leads to all of the image thumbnail assets used by the 
	 * level editor. ALl names are ${asset_name}Thumbnail.png, where ${asset_name}
	 * is the name of the full size image in ASSETS_PATH.
	 * 
	 * The '..' at the beginning is because the working
	 * directory is set to be ${workspace}/bin.
	 */
	public static final String THUMBNAIL_PATH = "../assets/thumbnails/";
	
	/**
	 * The path that leads to all of the level json files used by both 
	 * the level editor and the game itself.
	 * 
	 * The '..' at the beginning is because the working
	 * directory is set to be ${workspace}/bin.
	 */
	public static final String LEVELS_PATH = "../levelFiles/";
}
