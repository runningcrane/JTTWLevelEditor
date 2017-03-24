package new_interactable;

public class TextTip extends AInteractable {
	
	private final static String TEXT_MEMBER = "text";
	private final static String FONT_SIZE = "fontSize";
	
	public TextTip(int ticket) {
		this.setTicket(ticket);
		this.setPath("");
	}
	
	
	// Empty constructor for GSON.
	public TextTip() {}
	
	public void setString(String text) {
	    this.getPropertyBook().getStringList().put(TEXT_MEMBER, text);
	}
	
	public String getString() {
		return this.getPropertyBook().getStringList().get(TEXT_MEMBER);
	}
	
	public void setSize(int fontSize) {
		this.getPropertyBook().getIntList().put(FONT_SIZE, fontSize);
	}
	
	public int getSize() {
		return this.getPropertyBook().getIntList().get(FONT_SIZE);
	}
}
