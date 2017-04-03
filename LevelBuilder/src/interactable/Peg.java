package interactable;

public class Peg extends AInteractable {
	/**
	 * Makes a basic peg - a "golden" peg.
	 * @param ticket identifier
	 * @param path path to the file that has the peg image
	 */
	public Peg(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}	
}
