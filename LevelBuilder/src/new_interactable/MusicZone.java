package new_interactable;

public class MusicZone extends AInteractable 
{
	private static final String X_MIN = "xmin";
	private static final String Y_MIN = "ymin";
	private static final String X_MAX = "xmax";
	private static final String Y_MAX = "ymax";
	
	public MusicZone(int ticket, String imageName) {
		this.setTicket(ticket);
		this.setPath(imageName);
	}
	
	public double getWidth() {
	    return this.getPropertyBook().getDoubList().get(X_MAX) - this.getPropertyBook().getDoubList().get(X_MIN);
	}
	
	public double getHeight() {
		return this.getPropertyBook().getDoubList().get(Y_MAX) - this.getPropertyBook().getDoubList().get(Y_MIN);
	}
	
	@Override
	public void setCenter(double xm, double ym) {
		double oldX = super.getCenterXM();
		double oldY = super.getCenterYM();
		Double xMin = this.getPropertyBook().getDoubList().get(X_MIN);
		if (xMin != null) {
			Double xMax = this.getPropertyBook().getDoubList().get(X_MAX);
			Double yMax = this.getPropertyBook().getDoubList().get(Y_MAX);
		    double diffXLeft = oldX - xMin;
		    double diffYDown = oldY - this.getPropertyBook().getDoubList().get(Y_MIN);
		    double diffXRight = xMax - oldX;
		    double diffYUp = yMax - oldY;
		    this.getPropertyBook().getDoubList().put(X_MIN, xm - diffXLeft);
		    this.getPropertyBook().getDoubList().put(Y_MIN, ym - diffYDown);
		    this.getPropertyBook().getDoubList().put(X_MAX, xm + diffXRight);
		    this.getPropertyBook().getDoubList().put(Y_MAX, ym + diffYUp);
		}
		super.setCenter(xm, ym);
	}
}
