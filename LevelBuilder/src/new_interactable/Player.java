package new_interactable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
			playerBI = ImageIO.read(new File(this.getPath()));
		} catch (IOException e) {
			System.err.println("File not found: " + this.getPath());
			e.printStackTrace();
			return;
		}
		this.setBI(playerBI);
	}
	
	public void setPresent(boolean present) {
		this.getPropertyBook().getBoolList().put("Present", present);
	}
}
