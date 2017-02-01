package interactable;

public abstract class AInteractable {
	private String imageName;
	private double centerX;
	private double centerY;
	private double imageSizeWidth;
	private double imageSizeHeight;
	private double collisionWidth;
	private double collisionHeight;
	
	protected String getImageName() {
		return this.imageName;
	}
	
	protected void setImageName(String name) {
		this.imageName = name;
	}
	
	protected double getCenterX() {
		return this.centerX;
	}
	
	protected void setCenterX(double x) {
		this.centerX = x;
	}
	
	protected double getCenterY() {
		return this.centerY;
	}
	
	protected void setCenterY(double y) {
		this.centerY = y;
	}
	
	protected double ImageSizeWidth() {
		return this.imageSizeWidth;
	}
	
	protected void setImageSizeWidth(double width) {
		this.imageSizeWidth = width;
	}
	
	protected double getImageSizeHeight() {
		return this.imageSizeHeight;
	}
	
	protected void setImageSizeHeight(double height) {
		this.imageSizeHeight = height;	
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
