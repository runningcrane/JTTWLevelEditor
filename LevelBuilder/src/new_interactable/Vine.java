package new_interactable;

public class Vine extends AInteractable {
	
	/**
	 * Length of vine in [cocos, m]. It is also hm.
	 */
	private double length;
	
	/**
	 * How big an arc this vine can make in [degrees].
	 */
	private double arcLimit;
	
	private double startVel;
	
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
