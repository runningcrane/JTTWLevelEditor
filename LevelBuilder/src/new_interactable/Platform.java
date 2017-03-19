package new_interactable;

public class Platform extends AInteractable {
	/**
	 * Makes a basic platform.
	 * @param ticket identifier
	 * @param path path to the file that has the platform image
	 */
	public Platform(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
