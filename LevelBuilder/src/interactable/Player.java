package interactable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import utils.Constants;

public class Player extends AInteractable {
	
	/**
	 * Makes one of the main character players.
	 * @param path path to the file that has the player image
	 */
	public Player(String path) {
		this.setPath(path);		
		this.postDeserialization();
	}	
	
	public Player() {}
	
	public void postDeserialization() {
		BufferedImage playerBI;
		try {
			playerBI = ImageIO.read(new File(Constants.ASSETS_PATH + this.getPath()));
		} catch (IOException e) {
			System.err.println("File not found: " + Constants.ASSETS_PATH + this.getPath());
			e.printStackTrace();
			return;
		}
		this.setBI(playerBI);
	}
	
	public void setPresent(boolean present) {
		this.getPropertyBook().getBoolList().put("Present", present);
	}
}
