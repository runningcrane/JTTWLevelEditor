package interactable;

public class Vine extends AInteractable {
	/**
	 * Makes a basic vine.
	 * @param ticket identifier
	 * @param path path to the file that has the vine image
	 */
	public Vine(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
