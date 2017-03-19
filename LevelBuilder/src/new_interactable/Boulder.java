package new_interactable;

public class Boulder extends AInteractable {
	/**
	 * Makes a basic boulder.
	 * @param ticket identifier
	 * @param path path to the file that has the boulder image
	 */
	public Boulder(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
