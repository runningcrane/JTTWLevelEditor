package interactable;

public class NPC extends AInteractable {
	/**
	 * Makes an NPC.
	 * @param ticket identifier
	 * @param path path to the file that has the NPC image
	 */
	public NPC(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
