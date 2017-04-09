package interactable;

public class Trap extends AInteractable {
	// Properties include density, bounciness, friction
	// wall thickness, trapWidth and height, image width and height
	// and offset.
	
	/**
	 * Makes a basic trap.
	 * @param ticket identifier
	 * @param path path to the file that has the trap image
	 */
	public Trap(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
