package interactable;

import java.awt.Image;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;

import server.IElement;

public interface IInteractable extends IElement {
	
	public Image getImage();	
	public Point2D.Double getPosition();

}
