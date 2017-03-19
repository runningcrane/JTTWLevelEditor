package new_interactable;

public class Trap extends AInteractable {
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
