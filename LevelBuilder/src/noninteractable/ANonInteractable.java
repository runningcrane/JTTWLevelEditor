package noninteractable;

public abstract class ANonInteractable implements INonInteractable {
	/**
	 * Relative path to file.
	 */
	private String path;	
	
	/**
	 * Center X position on level in terms of meters. (0,0) is the bottom left.
	 */
	private double centerXm;
	/**
	 * Center Y position on level in terms of meters. (0,0) is the bottom left.
	 */
	private double centerYm;
	
	/**
	 * Width in terms of meters.
	 */
	private double widthm;
	
	/**
	 * Height in terms of meters.
	 */
	private double heightm;
	
	/**
	 * Hit box width. 
	 * TODO: Will be replaced by an array of collision box points.
	 */
	private double collisionWidth;
	
	/**
	 * Hit box height. 
	 * TODO: Will be replaced by an array of collision box points.
	 */
	private double collisionHeight;
	
	/**
	 * Gets the path to its image.
	 * @return relative path to image
	 */
	public String getPath() {
		return this.path;
	}	
	
	/**
	 * Set the relative path to its image.
	 * @param name relative path name
	 */
	protected void setPath(String name) {
		this.path = name;
	}
	
	/**
	 * Get the center X position on level in terms of meters.
	 * @return center x position in terms of meters
	 */
	protected double getCenterXm() {
		return this.centerXm;
	}
	
	/**
	 * Set the center X position on level in terms of meters.
	 * @param xm center X pos on level in terms of meters; 0 is the left boundary
	 */
	protected void setCenterXm(double xm) {
		this.centerXm = xm;
	}
	
	/**
	 * Get the center Y position on level in terms of meters.
	 * @param  center Y pos on level in terms of meters
	 */
	protected double getCenterYm() {
		return this.centerYm;
	}
	
	/**
	 * Set the center Y position on level in terms of meters.
	 * @param ym center Y pos on level in terms of meters; 0 is the south boundary
	 */
	protected void setCenterYm(double y) {
		this.centerYm = y;
	}	
	
	/**
	 * Get width in terms of meters.
	 * @return width in meters
	 */
	protected double getInGameWidth() {
		return this.widthm;
	}
	
	/**
	 * Set width in terms of meters.
	 * @return width width in meters
	 */
	protected void setInGameWidth(double width) {
		this.widthm = width;
	}
	
	/**
	 * Get height in terms of meters.
	 * @return height in meters
	 */
	protected double getInGameHeight() {
		return this.heightm;
	}
	
	/**
	 * Set height in terms of meters.
	 * @return height width in meters
	 */
	protected void setInGameHeight(double height) {
		this.heightm = height;	
	}
	
	protected double getCollisionWidth() {
		return this.collisionWidth;
	}	
	
	protected void setCollisionWidth(double width) {
		this.collisionWidth = width;
	}
	
	protected double getCollisionHeight() {
		return this.collisionHeight;
	}	
	
	protected void setCollisionHeight(double height) {
		this.collisionHeight = height;
	}		
}
