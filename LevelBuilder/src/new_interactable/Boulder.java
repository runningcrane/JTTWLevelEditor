package new_interactable;

public class Boulder extends AInteractable {
	
	/**
	 * Previous ticket of the boulder.
	 */
	private int oldTicket;
	
	/**
	 * Scaled radius of the boulder.
	 */
	private double scaledRadius;
	
	/**
	 * Makes a basic boulder.
	 * @param ticket identifier
	 * @param path path to the file that has the boulder image
	 */
	public Boulder(int ticket, String path) {
		setTicket(ticket);
		this.setPath(path);		
	}
	
	public int getOldTicket() {
		return this.oldTicket;
	}
	
	public void setOldTicket(int ticket) {
		this.oldTicket = ticket;
	}
	
	public void updateTicket() {
		this.oldTicket = getTicket();
	}
	
	public double getScaledRadius() {
		return this.scaledRadius;
	}
	
	public void scaleRadius(double scale) {
		this.scaledRadius = this.getDefaultPropertyBook().getDoubList().get("radius") * scale;
	}
}
