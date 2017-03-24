package new_interactable;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import utils.annotations.Exclude;

public abstract class AInteractable {
	
	public AInteractable() {
		start();
	}
	
	/**
	 * Relative path to file.
	 */
	private String path;	
	
	/**
	 * Center X position on level in terms of meters. (0,0) is the bottom left.
	 */
	private double centerXM;
	/**
	 * Center Y position on level in terms of meters. (0,0) is the bottom left.
	 */
	private double centerYM;
	
	/**
	 * Scaling of the width/height of the default image size.
	 * For boulders, radius gets scaled too.
	 */
	private double scale;
	
	/**
	 * An identifier for this entity.
	 */
	private int ticket;
	
	/**
	 * Original image.
	 */
	@Exclude
	private BufferedImage image;
	
	/**
	 * Rescaled image for output on level editor.
	 */
	@Exclude
	private ImageIcon rescaledImage;
	
	/**
	 * Scaled in-game width [m]. 
	 */
	private double scaledIGWM;
	
	/**
	 * Scaled in-game height [m].
	 */
	private double scaledIGHM;	
	
	/**
	 * Book of object-specific properties.
	 */
	private PropertyBook book;
	
	/**
	 * Book of default properties.
	 */
	@Exclude
	private PropertyBook defaultBook;
	
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
	public double getCenterXM() {
		return this.centerXM;
	}
	
	/**
	 * Set the center X position on level in terms of meters.
	 * @param xm center X pos on level in terms of meters; 0 is the left boundary
	 */
	public void setCenterXM(double xm) {
		this.centerXM = xm;
	}
	
	/**
	 * Get the center Y position on level in terms of meters.
	 * @param  center Y pos on level in terms of meters
	 */
	public double getCenterYM() {
		return this.centerYM;
	}
	
	/**
	 * Set the center Y position on level in terms of meters.
	 * @param ym center Y pos on level in terms of meters; 0 is the south boundary
	 */
	public void setCenterYM(double y) {
		this.centerYM = y;
	}	
	
	/**
	 * Get the identifier for this entity.
	 * @return ticket identifier
	 */
	public int getTicket() {
		return this.ticket;
	}
	
	/**
	 * Set the identifier for the entity.
	 * @param number identifier
	 */
	public void setTicket(int number) {
		this.ticket = number;
	}
	/**
	 * Get scale of the entity.
	 * @return scale
	 */
	public double getScale() {
		return this.scale;		
	}
	
	/**
	 * Set scale of the entity.
	 * @param scale scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
		setScaledIGWM();
		setScaledIGHM();
		setScaledCoordinates();
	}
	
	/**
	 * Get the original image of this entity.
	 * @return buffered image, full size
	 */
	public BufferedImage getBI() {
		return this.image;
	}
	
	/**
	 * Set the original image of this entity.
	 * @param BI buffered image
	 */
	public void setBI(BufferedImage BI) {
		this.image = BI;
	}
	
	/**
	 * Get the rescaled image of this entity.
	 * @return rescaled image icon to fit into the level
	 */
	public ImageIcon getRI() {
		return this.rescaledImage;
	}
	
	/**
	 * Set the rescaled image of the entity.
	 * @param RI rescaled image icon
	 */
	public void setRI(ImageIcon RI) {
		this.rescaledImage = RI;
	}
	
	/**
	 * Get width in terms of meters.
	 * @return width in meters
	 */
	public double getInGameWidth() {
		return defaultBook.doubList.get("widthm");
	}
	
	/**
	 * Set width in terms of meters.
	 * @return width width in meters
	 */
	protected void setInGameWidth(double width) {
		defaultBook.doubList.replace("widthm", width);
	}
	
	/**
	 * Get height in terms of meters.
	 * @return height in meters
	 */
	public double getInGameHeight() {
		return defaultBook.doubList.get("heightm");
	}
	
	/**
	 * Set height in terms of meters.
	 * @return height width in meters
	 */
	protected void setInGameHeight(double height) {
		defaultBook.doubList.replace("heightm", height);
	}
	
	/**
	 * Get scaled width in terms of meters.
	 * @return scaled width in meters
	 */
	public double getScaledIGWM() {
		return this.scaledIGWM;
	}
	
	/**
	 * Set scaled width in terms of meters.
	 * @return scaled width width in meters
	 */
	protected void setScaledIGWM() {
		this.scaledIGWM = defaultBook.doubList.get("widthm") * this.scale;	
	}
	
	/**
	 * Get scaled height in terms of meters.
	 * @return scaled height in meters
	 */
	public double getScaledIGHM() {
		return this.scaledIGHM;
	}
	
	/**
	 * Set scaled height in terms of meters.
	 * @return scaled height width in meters
	 */
	protected void setScaledIGHM() {
		this.scaledIGHM = defaultBook.doubList.get("heightm") * this.scale;	
	}
	
	/**
	 * Rescale the collision points based on the current scale.
	 */
	protected void setScaledCoordinates() {
		this.book.collPointList.clear();
		this.defaultBook.collPointList.forEach((point) -> {
			this.book.collPointList.add(new Point2D.Double(point.x * this.scale, point.y * this.scale));
		});
	}
	
	/**
	 * Get the book of object-specific properties.
	 * @return book of properties
	 */
	public PropertyBook getPropertyBook() {
		return this.book;
	}
	
	/**
	 * Set an object's specific properties
	 * @param book property book
	 */
	public void setPropertyBook(PropertyBook book) {
		this.book = book;
	}
	
	/**
	 * Get the book of default properties.
	 * @return default properties book
	 */
	public PropertyBook getDefaultPropertyBook() {
		return this.defaultBook;
	}
	
	/**
	 * Set default properties, such as base image width/height in meters.
	 * @param defaultBook book of default properties
	 */
	public void setDefaultPropertyBook(PropertyBook defaultBook) {
		this.defaultBook = defaultBook;				
	}
	
	/**
	 * Updates the properties of this object. 
	 * If the new property book contains properties not in this book,
	 * those properties will be added.
	 * @param newBook new book of properties
	 */
	public void updateProperties(PropertyBook newBook) {				
		// Integers.		
		newBook.getIntList().forEach((name, value) -> {
			if (book.getIntList().containsKey(name)) {
				book.getIntList().replace(name, value);
			} else {
				book.getIntList().put(name, value);
			}
		});
		
		// Doubles.
		newBook.getDoubList().forEach((name, value) -> {
			if (book.getDoubList().containsKey(name)) {
				book.getDoubList().replace(name, value);
			} else {
				book.getDoubList().put(name, value);
			}
		});
		
		// Float.
		newBook.getFloatList().forEach((name, value) -> {
			if (book.getFloatList().containsKey(name)) {
				book.getFloatList().replace(name, value);
			} else {
				book.getFloatList().put(name, value);
			}
		});
		
		// Strings.
		newBook.getStringList().forEach((name, value) -> {
			if (book.getStringList().containsKey(name)) {
				book.getStringList().replace(name, value);
			} else {
				book.getStringList().put(name, value);
			}
		});
		
		// Booleans.
		newBook.getBoolList().forEach((name, value) -> {
			if (book.getBoolList().containsKey(name)) {
				book.getBoolList().replace(name, value);
			} else {
				book.getBoolList().put(name, value);
			}
		});
	}	
	
	/**
	 * Change where this object is located.
	 * @param x [cocos,m] coordinates
	 * @param y [cocos,m] coordinates
	 */
	public void setCenter(double x, double y) {
		this.centerXM = x;
		this.centerYM = y;
	}
	
	public void start() {
		this.book = new PropertyBook();
		this.defaultBook = new PropertyBook();
	}
}
