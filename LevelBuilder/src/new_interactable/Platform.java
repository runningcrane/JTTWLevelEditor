package new_interactable;

import java.awt.geom.Point2D;

public class Platform extends AInteractable {
	
	private Point2D.Double endpoint;
	/**
	 * Makes a basic platform.
	 * @param ticket identifier
	 * @param path path to the file that has the platform image
	 */
	public Platform(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
	
	public void setEndpoint(Point2D.Double endpoint) {
		this.endpoint = endpoint;
	}
	
	public Point2D.Double getEndpoint() {
		return this.endpoint;
	}
	
	public int getZorder() {
		Integer x = this.getPropertyBook().getIntList().get("Z-order");
		if (x == null) {
			return -1;
		} else {
			return x;
		}
	}
}
