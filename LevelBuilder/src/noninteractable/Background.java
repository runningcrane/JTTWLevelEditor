package noninteractable;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Background implements INonInteractable {
	
	private BufferedImage image;
	
	public Background(BufferedImage image) {
		this.image = image;
	}
	public BufferedImage getImage() {
		return this.image;
	}

}
