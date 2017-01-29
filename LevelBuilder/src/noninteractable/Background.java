package noninteractable;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Background implements INonInteractable {
	
	private Image image;
	
	public Background(Image image) {
		this.image = image;
	}
	public Image getImage() {
		return this.image;
	}

}
