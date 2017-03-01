package server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.javafx.geom.Line2D;

import interactable.Boulder;
import interactable.Enemy;
import interactable.Platform;
import interactable.Player;
import interactable.Vine;
import noninteractable.Background;
import noninteractable.INonInteractable;

/**
 * Manages the level building.
 * @author Melinda Crane
 */
public class LevelManager {
	/**
	 * Meter-to-pixel ratio. For example, 80 means 80 pixels : 1 m
	 */
	private double mToPixel;
	
	/**
	 * Viewport offset (px).
	 */
	private Point2D.Double vpOffset;
	
	/**
	 * EOL location.
	 */
	private Point2D.Double eol;
	
	/**
	 * Background object. 
	 */
	private Background bg;
	
	/**
	 * Level width (meters).
	 */
	private double lvwm;
	
	/**
	 * Level height (meters).
	 */
	private double lvhm;
	
	/**
	 * Viewport width (meters).	
	 */
	private double vpwm;
	
	/**
	 * Viewport height (meters).
	 */
	private double vphm;
	
	/**
	 * Map of objects to their default JSON.
	 */
	private Map<String, JSONObject> defaultJSON;
	
	/**
	 * Foreground array.
	 */
	private ArrayList<INonInteractable> fg;
	
	/**
	 * Platform array.
	 */
	private Map<Integer, Platform> plats;
	
	/**
	 * Character array.
	 */
	private Map<String, Player> characters;
	
	/**
	 * Vine array.
	 */
	private Map<Integer, Vine> vines;
		
	/**
	 * Boulder map.
	 */
	private Map<Integer, Boulder> boulders;
	
	/**
	 * NPC array.
	 */
	private Map<String, Enemy> npcs; 
	
	/**
	 * Counter used to label editable objects on output window.
	 */
	private int ticket;
	
	/**
	 * Adapter to communicate with the controls panel.
	 */
	private ILevelToControlAdapter ltcAdapter;
	
	/**
	 * Adapter to communicate with the output panel.
	 */
	private ILevelToOutputAdapter ltoAdapter;
	
	/**
	 * Adapter to communicate with the layers panel.
	 */
	private ILevelToLayerAdapter ltlAdapter;
	
	/**
	 * Time to update the level view.
	 */
	private Timer timer;
	
	/**
	 * How often the timer should be called.
	 */
	private int timeSlice;
	
	/**
	 * Constructor for this level manager.
	 * Takes in adapters to each necessary editing window.
	 * @param ltbAdapter
	 * @param ltoAdapter
	 * @param ltlAdapter
	 */
	public LevelManager(ILevelToControlAdapter ltbAdapter, ILevelToOutputAdapter ltoAdapter,
			ILevelToLayerAdapter ltlAdapter) {
		this.ltcAdapter = ltbAdapter;
		this.ltoAdapter = ltoAdapter;
		this.ltlAdapter = ltlAdapter;
		
		this.mToPixel = 100;		
		this.vpOffset = new Point2D.Double(0, 0);
		
		// No real reason to put it to 5,5 other than just to initialize it.
		this.eol = new Point2D.Double(5, 8);
		this.ticket = 1;
		
		// Call upon the layer view to update itself frequently
		ActionListener listen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ltoAdapter.redraw();
			}
		};
		
		this.timeSlice = 50; // update every 50 milliseconds
		this.timer = new Timer(timeSlice, listen);
		
		// TODO: Replace later. This is a manual set of width and height of the background.
		this.vpwm = 8;
		this.vphm = 6;
		this.lvwm = 20;
		this.lvhm = 15;
		
		// Instantiate various lists.
		this.defaultJSON = new HashMap<String, JSONObject>();
		this.plats = new HashMap<Integer, Platform>();
		this.fg = new ArrayList<INonInteractable>();
		this.vines = new HashMap<Integer, Vine>();
		this.boulders = new HashMap<Integer, Boulder>();
		
		// Set up the player characters.
		characters = new HashMap<String, Player>();		
		
		// String path, String name, double cxm, double cym, boolean present
		
		Player monkey = new Player("assets/Monkey.png", "Monkey", 0, 0, false);
		BufferedImage monkeyBI;
		try {
			monkeyBI = ImageIO.read(new File("assets/Monkey.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + "assets/Monkey.png");
			e.printStackTrace();
			return;
		}
		monkey.setImage(monkeyBI);
		monkey.setRescaled(resize(monkeyBI, 0.7, 1.7));
		characters.put("Monkey", monkey);		

		Player monk = new Player("assets/Monk.png", "Monk", 0, 0, false);		
		BufferedImage monkBI;
		try {
			monkBI = ImageIO.read(new File("assets/Monk.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + "assets/Monk.png");
			e.printStackTrace();
			return;
		}
		monk.setImage(monkBI);
		monk.setRescaled(resize(monkBI, 0.7, 1.7));
		characters.put("Monk", monk);
		
		Player pig = new Player("assets/Piggy.png", "Piggy", 0, 0, false);
		BufferedImage pigBI;
		try {
			pigBI = ImageIO.read(new File("assets/Piggy.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + "assets/Piggy.png");
			e.printStackTrace();
			return;
		}
		pig.setImage(pigBI);
		pig.setRescaled(resize(pigBI, 0.7, 1.7));
		characters.put("Piggy", pig);
		
		Player sandy = new Player("assets/Sandy.png", "Sandy", 0, 0, false);
		BufferedImage sandyBI;
		try {
			sandyBI = ImageIO.read(new File("assets/Sandy.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + "assets/Sandy.png");
			e.printStackTrace();
			return;
		}
		sandy.setImage(sandyBI);
		sandy.setRescaled(resize(sandyBI, 0.7, 1.7));
		characters.put("Sandy", sandy);		
		
		// Initialize the NPCs.
		npcs = new HashMap<String, Enemy>();
	}
	
	/**
	 * After constructor is done initializing, start operations.
	 */
	public void start() {			
		setBg("assets/bgSunny.png");
		this.timer.start();
	}
	
	/**
	 * Sets background of level.
	 * @param path path to background image
	 */
	public void setBg(String path) {
		BufferedImage bgImage;
		try {
			bgImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("File not found: " + path);
			e.printStackTrace();
			return;
		}
		
		this.bg = new Background(bgImage, path, this.lvwm, this.lvhm);
		this.bg.setRescaled(resize(bgImage, this.lvwm, this.lvhm));
	}
	
	/**
	 * Renders the output window.
	 * @param panel JPanel to render
	 * @param g graphics of component
	 */
	public void render(Component panel, Graphics g) {
		// Draw background
		if (bg.getRescaled() != null) {
			Point2D.Double vpbg = getViewportCoordinates(0, 0);
			g.drawImage(bg.getRescaled().getImage(), (int)vpbg.getX(), (int)vpbg.getY(), null);
		}
		
		// Draw platforms		
		plats.forEach((number, plat) -> {
			/*
			 * Unfortunately drawImage draws on the top left, not center, so adjustments are made.
			 * Also unfortunately, cocos uses a different orientation system than swing. 
			 * Namely, the y is flipped in direction.			
			 * Since CenterXm/CenterYm are in swing-orientation, reverse the ys to get them in cocos.
			 */		
			double ulxp = (plat.getCenterXm() - plat.getScaledIGW()/2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - plat.getCenterYm()) - plat.getScaledIGH() /2.0) * this.mToPixel;			
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);
			
			g.drawImage(plat.getRescaled().getImage(), (int)vpulp.getX(), (int)vpulp.getY(), null);
			
			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(plat.getCenterXm() * this.mToPixel,
					(this.lvhm - (plat.getCenterYm())) * this.mToPixel);
			g.fillOval((int)(vplp.getX()), (int)(vplp.getY()), 15, 15);
			
			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(plat.getCenterXm() * this.mToPixel + 5, 
					(this.lvhm - (plat.getCenterYm())) * this.mToPixel + 10);
			g.drawString(Integer.toString(number), (int)(vplbp.getX()), 
					(int)(vplbp.getY()));
			
			// If moveable, show its endpoint and a line to its endpoint.
			if (plat.isMoveable()) {
				Point2D.Double vplbep = getViewportCoordinates(plat.getEndX() * this.mToPixel + 5, 
						(this.lvhm - (plat.getEndY())) * this.mToPixel + 10);
				
				// Draw the line first.	
				g.setColor(Color.MAGENTA);
		        g.drawLine((int)vplp.getX(), (int)vplp.getY(), (int)vplbep.getX(), (int)vplbep.getY());
		        
		        // Then draw the endpoint.
		        g.setColor(Color.RED);
		        g.fillOval((int)(vplbep.getX()), (int)(vplbep.getY()), 15, 15);
		        
		        // Then label it.
		        g.setColor(Color.BLACK);
				Point2D.Double vplbnep = getViewportCoordinates(plat.getEndX() * this.mToPixel + 5, 
						(this.lvhm - (plat.getEndY())) * this.mToPixel + 10);
				g.drawString(Integer.toString(number) + "EP", (int)(vplbnep.getX()), 
						(int)(vplbnep.getY()));
			}
		});				
		
		// Draw vines
		vines.forEach((ticket, vine) -> {
			double ulxp = (vine.getCenterXm() - vine.getInGameWidth()/2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - vine.getCenterYm())) * this.mToPixel;			
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);
			
			g.drawImage(vine.getRescaled().getImage(), (int)vpulp.getX(), (int)vpulp.getY(), null);
			
			// Draw the label on top of it.
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(vine.getCenterXm() * this.mToPixel,
					(this.lvhm - (vine.getCenterYm() - vine.getInGameHeight()/2)) * this.mToPixel);
			g.fillOval((int)(vplp.getX()), (int)(vplp.getY()), 15, 15);
			
			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(vine.getCenterXm() * this.mToPixel + 5, 
					(this.lvhm - (vine.getCenterYm() - vine.getInGameHeight()/2)) * this.mToPixel + 10);
			g.drawString(Integer.toString(ticket), (int)(vplbp.getX()), 
					(int)(vplbp.getY()));
		});
		
		// Draw player characters
		characters.forEach((name, player) -> {
			if (player.getPresent()) {
				double ulxp = (player.getCenterXm() - player.getInGameWidth()/2.0) * this.mToPixel;
				double ulyp = ((this.lvhm - (player.getCenterYm())) - player.getInGameHeight() /2.0) * this.mToPixel;	
				Point2D.Double vpcp = getViewportCoordinates(ulxp, ulyp);				
				g.drawImage(player.getRescaled().getImage(), (int)vpcp.getX(), (int)vpcp.getY(), null);
			}
		});
		
		// Draw EOL		
		g.setColor(Color.GREEN);
		Point2D.Double vpeol = getViewportCoordinates(this.eol.getX() * this.mToPixel,
				(this.lvhm - (this.eol.getY())) * this.mToPixel);
		g.fillOval((int)(vpeol.getX()), (int)(vpeol.getY()), 15, 15);
							
		g.setColor(Color.BLACK);
		Point2D.Double vplbeol = getViewportCoordinates(this.eol.getX() * this.mToPixel + 5,
				(this.lvhm - (this.eol.getY())) * this.mToPixel + 10);
		g.drawString("EOL", (int)(vplbeol.getX()), 
				(int)(vplbeol.getY()));
	}
	
	/**
	 * For rendering purposes. Offsets the real world coordinates by the viewport offset.
	 * @param x real world swing coordinate
	 * @param y real world swing coordinate
	 * @return (x,y) viewport swing coordinates
	 */
	public Point2D.Double getViewportCoordinates(double x, double y) {
		return new Point2D.Double(x + this.vpOffset.getX(), y + this.vpOffset.getY());
	}
	
	public void changeOffset(double xm, double ym) {
		
		this.vpOffset = new Point2D.Double(this.vpOffset.getX() + xm * this.mToPixel, 
				this.vpOffset.getY() + ym * this.mToPixel);
	}
	
	/**
	 * Request output window to change the number of pixels in a meter.
	 * @param mToPixel
	 */
	public void setMToPixel(double mToPixel) {
		this.mToPixel = mToPixel;
		
		// Unfortunately this requires resetting ALL of the rescaled images.		
		this.plats.forEach((ticket, plat) -> plat.setRescaled(resize(plat.getImage(), plat.getScaledIGW(), plat.getScaledIGH())));		
		this.characters.forEach((name, player) -> player.setRescaled(resize(player.getImage(), 0.7, 1.7)));
		this.vines.forEach((ticket, vine) -> vine.setRescaled(resize(vine.getImage(), vine.getInGameWidth(), vine.getInGameHeight())));
		// this.npcs.forEach((name, enemy) -> enemy.setRescaled(resize(enemy.getImage(), enemy.getInGameWidth(), enemy.getInGameHeight())));
		this.bg.setRescaled(resize(this.bg.getImage(), this.lvwm, this.lvhm));		 
	}
	
	/**
	 * Request output window to change its size.
	 * @param wm width in in-game meters
	 * @param hm height in in-game meters
	 */
	public void setLevelDimensions(double wm, double hm) {			
		this.lvwm = wm;
		this.lvhm = hm;
		
		// Need to resize the background based on whichever scale is the biggest
		this.setBg(this.bg.getPath());
	}
	
	public void setViewportDimensions(double wm, double hm) {
		ltoAdapter.setDimensions((int)(wm * mToPixel), (int)(hm * mToPixel));
		this.vpwm = wm;
		this.vphm = hm;
	}
	/**
	 * Scales an image to in-game meters size.
	 * @param widthMeters expected in-game meters
	 * @param original contains image in original pixel dimensions
	 * @return
	 */
	public ImageIcon resize(BufferedImage original, double wm, double hm) {			
		double expectedWidth = wm * this.mToPixel;
		double widthScale = expectedWidth/original.getWidth();
		
		double expectedHeight = hm * this.mToPixel;
		double heightScale = expectedHeight/original.getHeight();		
		
		return new ImageIcon(original.getScaledInstance((int)(original.getWidth() * widthScale), 
				(int)(original.getHeight() * heightScale),
				java.awt.Image.SCALE_SMOOTH));
	}
	
	/**
	 * Manual call to reset the level dimensions.
	 * @param wp width in pixels
	 * @param hp height in pixels
	 */
	public void manualResize(double wp, double hp) {
		this.setViewportDimensions( wp / this.mToPixel, hp / this.mToPixel);	
	}
	
	public void setVinePosition(String path) {
		ltoAdapter.makeVine(path);
	}		
	
	public void setRockPosition(String path){
		ltoAdapter.makeRock(path);
	}
	
	public int makeBoulder(String path, double xp, double yp, double scale) {
		Boulder boulder;
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym appropriately.		
		boulder = new Boulder(path, (xp - this.vpOffset.getX()) / this.mToPixel, 
				this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel, scale);
			
		// Set up the default collision points & radius of the boulder.
		setBoulderDefaults(boulder);		
		
		BufferedImage image;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Image file not found: " + path);
			e.printStackTrace();
			return -1;
		}
		
		boulder.setImage(image);
		System.out.println("Defaults set");
		boulder.setScale(scale);
		boulder.setRescaled(resize(image, boulder.getScaledRadius(), boulder.getScaledRadius()));
		
		boulders.put(this.ticket, boulder);
		ltlAdapter.addBoulderEdit(this.ticket, boulder.getRadius(),boulder.getMass(), boulder.getScale());
		this.ticket++;	
		
		return this.ticket - 1;
	}
	
	public int makeVine(String path, double xp, double yp, double wm, double hm, 
			double arcLength, double startVel) {
		Vine vine = new Vine(path, (xp - this.vpOffset.getX()) / this.mToPixel, 
				this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel, wm, hm, arcLength, startVel);
		
		BufferedImage image;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("File not found: " + path);
			e.printStackTrace();
			return -1;
		}
		
		vine.setImage(image);
		vine.setRescaled(resize(image, wm, hm));
		
		vines.put(this.ticket, vine);	
		ltlAdapter.addVineEdit(this.ticket, wm, hm, arcLength, startVel);
		this.ticket++;	
		
		return this.ticket - 1;
	}
	
	/**
	 * Makes a new platform object.
	 * @param path 
	 * @param xp xpixel location on screen [swing vp coordinates]
	 * @param yp ypixel location on screen [swing vp coordinates]
	 * @param scale expected scaling off of the default
	 * @return the platform's ticket number
	 */
	public int makePlatform(String path, double xp, double yp, double scale) {
		Platform platform;
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym appropriately.		
		platform = new Platform(path, (xp - this.vpOffset.getX()) / this.mToPixel, 
				this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel, scale);
			
		// Set up some defaults for this platform, such as wm, hm, and box.
		setPlatformDefaults(platform);		
		
		BufferedImage image;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Image file not found: " + path);
			e.printStackTrace();
			return -1;
		}
		
		platform.setImage(image);
		System.out.println("Defaults set");
		platform.setScale(scale);
		platform.setRescaled(resize(image, platform.getScaledIGW(), platform.getScaledIGH()));
		
		plats.put(this.ticket, platform);
		ltlAdapter.addPlatformEdit(this.ticket, platform.getInGameWidth(), platform.getInGameHeight(),
				platform.getScale());
		this.ticket++;	
		
		return this.ticket - 1;
	}	
	
	// TOGGLE SECTION - BEGIN

	/**
	 * Toggle players. 	
	 * @param name name of character to toggle
	 * @param status state of existance or not
	 */
	public void togglePlayer(String name, boolean status) {
		if (status) {
			System.out.println("Setting " + name + " to " + status);
			this.characters.get(name).setPresent(true);			
			ltoAdapter.setCharPos(name);
		} else {
			this.characters.get(name).setPresent(false);
		}
		
	}
	
	public void toggleDisappearsPlat(int ticket, boolean selected) {
		// Update the specified platform.
		this.plats.get(ticket).setDisappears(selected);
	}
	
	public void toggleMoveablePlat(int ticket, boolean selected) {
		this.plats.get(ticket).setMoveable(selected);
	}
	
	public void toggleSinkablePlat(int ticket, boolean selected) {
		this.plats.get(ticket).setSinkable(selected);
	}
	
	public void toggleClimbablePlat(int ticket, boolean selected) {
		this.plats.get(ticket).setClimbable(selected);
	}
	
	public void togglePolygonBoulder(int ticket, boolean selected) {
		this.boulders.get(ticket).toggleType(selected);
	}
			
	// TOGGLE SECTION - END
	
	/**
	 * The user wants to add a platform. Tell the output window to request focus,
	 * and what kind of platform to make.
	 * @param path
	 */
	public void setActive(String path) {
		ltoAdapter.makePlatform(path);
	}
	
	public void setPhysicsPlat(int ticket, double scK) {
		this.plats.get(ticket).setPhysics(scK);
	}
	
	public void setVelocityPlat(int ticket, double velocity) {
		this.plats.get(ticket).setVelocity(velocity);
	}
	
	// START EDITING SECTION
	
	/**
	 * Ask the OutputWindow for new center coordinates.
	 * @param ticket platform identifier
	 */
	public void editPlatCenter(int ticket) {
		ltoAdapter.setPlatPos(ticket);				
	}
	
	public void editVineCenter(int ticket) {
		ltoAdapter.setVinePos(ticket);
	}
	
	public void editBoulderCenter(int ticket) {
		ltoAdapter.setBoulderPos(ticket);
	}

	/**
	 * OutputWindow just called this method to set the platform's new center.
	 * @param ticket platform identifier
	 * @param xp x position [swing vp, pixel]
	 * @param yp y position [swing vp, pixel]
	 */
	public void editPlatCenterRes(int ticket, double xp, double yp) {
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym.		
		this.plats.get(ticket).setCenter((xp - this.vpOffset.getX()) / this.mToPixel,
						this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);		
	}
	
	/**
	 * OutputWindow just called this method to set the vine's new swing center.
	 * @param ticket platform identifier
	 * @param xp x position [swing vp, pixel]
	 * @param yp y position [swing vp, pixel]
	 */
	public void editVineCenterRes(int ticket, double xp, double yp) {
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym.		
		this.vines.get(ticket).setCenter((xp - this.vpOffset.getX()) / this.mToPixel,
						this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);		
	}	
	
	public void editBoulderCenterRes(int ticket, double xp, double yp) {
		this.boulders.get(ticket).setCenterXm((xp - this.vpOffset.getX()) / this.mToPixel);
		this.boulders.get(ticket).setCenterYm(this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);
	}
	
	public void editPlatScale(int ticket, double scale) {
		
		Platform toEditPlat = this.plats.get(ticket);
		toEditPlat.setScale(scale);
		
		// Rescale the image
		toEditPlat.setRescaled(resize(toEditPlat.getImage(), toEditPlat.getScaledIGW(), 
				toEditPlat.getScaledIGH()));
	}
	
	public void editBoulderScale(int ticket, double scale) {
		Boulder toEditBould = this.boulders.get(ticket);
		toEditBould.setScale(scale);
		
		// Rescale the image
		toEditBould.setRescaled(resize(toEditBould.getImage(), toEditBould.getScaledRadius(), 
				toEditBould.getScaledRadius()));
	}
	
	public void editVineDim(int ticket, double wm, double hm) {
		this.vines.get(ticket).editVineDim(wm, hm);
		
		// Rescale the image
		System.out.println("New size: " + wm + " , " + hm);
		this.vines.get(ticket).setRescaled(resize(this.vines.get(ticket).getImage(), wm, hm));
	}
	
	public void editVineArcl(int ticket, double arcl) {
		this.vines.get(ticket).editVineArcl(arcl);
	}
	
	public void editBoulderMass(int ticket, double mass) {
		this.boulders.get(ticket).setMass(mass);
	}
	
	public void editVineStartVel(int ticket, double startVel) {
		this.vines.get(ticket).editVineStartVel(startVel);
	}

	public void removePlat(int ticket) {
		this.plats.remove(ticket);			
	}
	
	public void removeVine(int ticket) {
		this.vines.remove(ticket);			
	}
	
	public void makeEndpointPlat(int ticket) {
		this.ltoAdapter.makeEndpointPlat(ticket);
	}
	
	public void setEndpointPlat(int ticket, double xp, double yp) {
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym.		
		this.plats.get(ticket).setEndpoint((xp - this.vpOffset.getX()) / this.mToPixel,
								this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);		
	}
	
	/**
	 * Set the character to a new position.
	 * @param name name of character to set
	 * @param xp x position of character [swing vp, pixel]
	 * @param yp y position of character [swing vp, pixel]
	 */
	public void setCharacterPosition(String name, double xp, double yp) {
		System.out.println("Received; setting " + name + " to " + xp / this.mToPixel + ", " + yp / this.mToPixel + "; m");
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym.
		this.characters.get(name).setCenter((xp - this.vpOffset.getX()) / this.mToPixel,
				this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);
	}
	
	public void markEOL() {
		ltoAdapter.markEOL();
	}
	
	public void setEOL(double xp, double yp) {
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym.
		this.eol = new Point2D.Double((xp - this.vpOffset.getX()) / this.mToPixel, 
				this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel);
	}
	
	
	// END EDITING SECTION
	
	
	// START JSON SECTION
	
	/**
	 * Outputs a JSON file.
	 * @param levelName name of the level
	 * @return JSON to be outputted 
	 */
	public JSONObject makeJSON(String levelName, String nextName, boolean polygon) {
		System.out.println("Writing with collision = " + polygon);
		JSONObject json = new JSONObject();
		JSONArray platList = getPlatList(polygon);
		JSONArray vineList = getVineList();
		JSONObject charList = getCharList();
		//JSONObject charLocs = getCharLocs(this.charLocs);
		
		// Level specific items
		json.put("levelName", levelName);
		json.put("nextLevelName", nextName);
		json.put("mToPixel", this.mToPixel);
		json.put("levelEndX", this.eol.getX());
		json.put("levelEndY", this.eol.getY());
		
		// Makeup-of-level items
		json.put("background", this.bg.getJSON());
		json.put("characters", charList);
		json.put("platforms", platList);
		json.put("vines", vineList);
		json.put("polygonCollision", polygon);
		//json.put("charactersStart", charLocs);
		return json;
	}

	public void setBoulderDefaults(Boulder boulder) {
		JSONObject json;
		
		System.out.println("boulder: " + boulder.getPath());
		String name = boulder.getPath().substring(7, boulder.getPath().length() - 4);
		
		if (this.defaultJSON.containsKey(name)) {
			json = this.defaultJSON.get(name);
		} else {		
		
			JSONParser parser = new JSONParser();
			Object obj;
			try {
				obj = parser.parse(new FileReader("../src/collision/" + name + ".json"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + "../src/collision/" + name + ".json\n" +
						"Please make the JSON in the collision box editor.");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				System.out.println("Illegal path: " + "../src/collision/" + name + ".json");
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				System.out.println("Cannot parse JSON at: " + "../src/collision/" + name + ".json");
				e.printStackTrace();
				return;
			}
			
			json = (JSONObject) obj;
			this.defaultJSON.put(name, json);
		}
		
		// Get the collision points.
		JSONArray cpJSON = (JSONArray) json.get("points");
		ArrayList<Point2D.Double> collisionPoints = new ArrayList<Point2D.Double>();
		if (cpJSON != null) {
			cpJSON.forEach((point) -> {
				JSONObject pointJSON = (JSONObject) point;
				collisionPoints.add(new Point2D.Double((double)pointJSON.get("x"), (double)pointJSON.get("y")));
			});
		}		
		
		// Get the radius.
		Double radiusD = (Double) json.get("radius");
		double radius;
		if (radiusD == null) {
			radius = 3;
		} else {
			radius = radiusD.doubleValue();
		}
		
		// Get the type.
		String type = (String) json.get("type");
		if (type == null || !type.equals("POLYGON")) {
			type = "CIRCLE";
		}
		
		// Get the mass.
		Double massD = (Double) json.get("mass");
		double mass;
		if (massD == null) {
			mass = 1000;
		} else {
			mass = massD.doubleValue();
		}
		
		// Set the defaults.		
		boulder.setDefaults(collisionPoints, radius);
		boulder.toggleType((type.equals("POLYGON")) ? true : false);
		boulder.setMass(mass);
	}
	
	/**
	 * Reads the defaults of a platform out of its JSON file.	
	 */
	public void setPlatformDefaults(Platform plat) {
		// Have we already loaded in the JSON? If so, get it out of the hash.
		JSONObject json;
		
		// Path name of the json file; name includes .png on the end.
		System.out.println("plat: " + plat.getPath());
		String name = plat.getPath().substring(7, plat.getPath().length() - 4);
		
		if (this.defaultJSON.containsKey(name)) {
			json = this.defaultJSON.get(name);
		} else {		
		
			JSONParser parser = new JSONParser();
			Object obj;
			try {
				obj = parser.parse(new FileReader("../src/collision/" + name + ".json"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + "../src/collision/" + name + ".json\n" +
						"Please make the JSON in the collision box editor.");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				System.out.println("Illegal path: " + "../src/collision/" + name + ".json");
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				System.out.println("Cannot parse JSON at: " + "../src/collision/" + name + ".json");
				e.printStackTrace();
				return;
			}
			
			json = (JSONObject) obj;
			this.defaultJSON.put(name, json);
		}
		
		// Get the collision points.
		JSONArray cpJSON = (JSONArray) json.get("points");
		ArrayList<Point2D.Double> collisionPoints = new ArrayList<Point2D.Double>();
		cpJSON.forEach((point) -> {
			JSONObject pointJSON = (JSONObject) point;
			collisionPoints.add(new Point2D.Double((double)pointJSON.get("x"), (double)pointJSON.get("y")));
		});
		
		// Get the width and height.
		Double widthD = (Double) json.get("width");
		double width;
		if (widthD == null) {
			System.err.println("Could not find width!");
			width = 3;
		} else {
			width = widthD.doubleValue();
		}
		
		Double heightD = (Double) json.get("height");
		double height;
		if (heightD == null) {
			System.err.println("Could not find height!");
			height = 3;
		} else {
			height = heightD.doubleValue();
		}
		
		// Set the fields.
		plat.setCollisionBox(collisionPoints);
		plat.editPlatDim(width, height);
	}
	
	public void getCollisionSphere(String path) {
		
	}
	
	
	/**
	 * Reads in a level from its JSON.	 
	 * @param levelPath path to the level JSON	 
	 */
	public void readJSON(String levelPath) {
		System.out.println("READING IN FILE " + levelPath);
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(levelPath));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + levelPath);
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Illegal path: " + levelPath);
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			System.out.println("Cannot parse JSON at: " + levelPath);
			e.printStackTrace();
			return;
		}
		
		// Begin parsing		
		clearAndSet();
		
		JSONObject level = (JSONObject) obj;
		String name = (String) level.get("levelName");
		// Default case.
		if (name == null) {
			name = "levelName";
		}
		
		setLevelName(name);
		
		String nextName = (String) level.get("nextLevelName");
		// Default case.
		if (nextName == null) {
			nextName = "nextLevelName";
		}
		setNextName(nextName);		
		
		JSONObject bg = (JSONObject) level.get("background");
		// Default case.
		if (bg == null) {
			bg = new JSONObject();
			bg.put("imageName", "bgSunny");
			bg.put("width", 20);
			bg.put("height", 15);
		}
		makeBackground(bg);
		
		Double eolXMD = (Double) level.get("levelEndX");
		double eolXM;
		// Default case.
		if (eolXMD == null) {
			eolXM = 14;
		} else {
			eolXM = eolXMD.doubleValue();
		}
		
		Double eolYMD = (Double) level.get("levelEndY");
		double eolYM;
		// Default case.
		if (eolYMD == null) {
			eolYM = 14;
		} else {
			eolYM = eolYMD.doubleValue();
		}
		
		this.eol = new Point2D.Double(eolXM, eolYM);
		
		Boolean polygon = (Boolean) level.get("polygonCollision");
		// Default case.
		if (polygon == null) {
			polygon = false;
		}
		System.out.println("Collision: " + polygon);
		
		JSONArray plats = (JSONArray) level.get("platforms");
		// Default case. 
		if (plats == null) {
			plats = new JSONArray();
		}
		
		makePlatList(plats, polygon);
		
		JSONArray vines = (JSONArray) level.get("vines");
		// Default case.
		if (vines == null) {
			vines = new JSONArray();
		}
		makeVinesList(vines);
		
		JSONObject characters = (JSONObject) level.get("characters");
		// Default case: don't change any of the characters' positions, etc if no info found.
		if (characters != null) {
			setCharacters(characters);
		}		
		
		// Resize last
		Double mToPixelD = (Double) level.get("mToPixel");
		double mToPixel;
		// Default case.
		if (mToPixelD == null) {
			mToPixel = 100;
		} else {
			mToPixel = mToPixelD.doubleValue();
		}
		jsonSetMToPixel(mToPixel);
		
	}
	
	public void clearAndSet() {
		// Reset the NPCs.
		this.npcs.clear();
		
		// Clear the current list of platforms. 
		this.plats.clear();
		
		// Clear the current list of vines.
		this.vines.clear();
		
		// By the way, they all need to be removed from the LayerWindow as well.
		this.ltlAdapter.removeAllWindows();
		
		// Reset the ticketer.
		this.ticket = 1;
		
		// Reset the viewpoint starting point.
		this.vpOffset = new Point2D.Double(0, 0);
	}
	
	public void setCharacters(JSONObject chars) {		
		JSONObject monkey = (JSONObject)chars.get("Monkey");
		// Default case: If monkey not found, don't change the current monkey at all.
		if (monkey != null) {				
			Double cxmD = (Double)monkey.get("startingXPos");
			double cxm;
			// Default case.
			if (cxmD == null) {
				cxm = 0;
			} else {
				cxm = cxmD.doubleValue();
			}
			
			Double cymD = (Double)monkey.get("startingYPos");
			double cym;
			// Default case.
			if (cymD == null) {
				cym = 0;
			} else {
				cym = cymD.doubleValue();
			}
			
			Boolean present = (Boolean)monkey.get("present");
			// Default case.
			if (present == null) {
				present = false;
			}
			this.characters.get("Monkey").setCenter(cxm, cym);
			this.characters.get("Monkey").setPresent(present);		
		}
		
		JSONObject monk = (JSONObject)chars.get("Monk");
		// Default case: If monk not found, don't change the current monk at all.
		if (monk != null) {												
			Double monkcxmD = (Double)monk.get("startingXPos");
			double monkcxm;
			// Default case.
			if (monkcxmD == null) {
				monkcxm = 0;
			} else {
				monkcxm = monkcxmD.doubleValue();
			}
			
			Double monkcymD = (Double)monk.get("startingYPos");
			double monkcym;
			// Default case.
			if (monkcymD == null) {
				monkcym = 0;
			} else {
				monkcym = monkcymD.doubleValue();
			}
			
			Boolean monkpresent = (Boolean)monk.get("present");
			// Default case.
			if (monkpresent == null) {
				monkpresent = false;
			}
			
			this.characters.get("Monk").setCenter(monkcxm, monkcym);
			this.characters.get("Monk").setPresent(monkpresent);
		}
		
		JSONObject pig = (JSONObject)chars.get("Piggy");
		// Default case: If pig not found, don't change the current pig at all.
		if (pig != null) {		
			// TODO: defaults
			double pigcxm = (double)pig.get("startingXPos");
			double pigcym = (double)pig.get("startingYPos");
			boolean pigpresent = (boolean)pig.get("present");
			this.characters.get("Piggy").setCenter(pigcxm, pigcym);
			this.characters.get("Piggy").setPresent(pigpresent);
		}
		
		JSONObject sandy = (JSONObject)chars.get("Sandy");
		// Default case: If sandy not found, don't change the current sandy at all.
		if (sandy != null) {
			// TODO: defaults
			double sandycxm = (double)sandy.get("startingXPos");
			double sandycym = (double)sandy.get("startingYPos");
			boolean sandypresent = (boolean)sandy.get("present");
			this.characters.get("Sandy").setCenter(sandycxm, sandycym);
			this.characters.get("Sandy").setPresent(sandypresent);
		}	
	}
	
	public void setLevelName(String name) {		
		ltoAdapter.setLevelName(name);
	}
	
	public void setNextName(String name) {		
		ltoAdapter.setNextName(name);
	}
	
	public void jsonSetMToPixel(double mToPixel) {
		setMToPixel(mToPixel);
		ltcAdapter.setMToPixel(mToPixel);
	}
	
	
	public void makeBackground(JSONObject bg) {
		String imageName = (String)bg.get("imageName");
		double levelWidth = (double)bg.get("width");
		double levelHeight = (double)bg.get("height");
		
		// Set wm, hm.
		this.lvwm = levelWidth;
		this.lvhm = levelHeight;
		
		// Set the background.
		setBg("assets/" + imageName);
		
		// Set the dimensions.
		setLevelDimensions(this.lvwm, this.lvhm);
		System.out.println("Dimensions set to " + this.lvwm + "x" + this.lvhm);
	}
	
	public JSONArray getPlatList(boolean polygon) {
		JSONArray list = new JSONArray();		
		
		// Add all present platforms	
		plats.forEach((number, platform) -> {
			list.add(platform.getJSON(polygon));			
		});	
		
		return list;
	}
	
	public JSONArray getVineList() {
		JSONArray list = new JSONArray();
		
		// Add all present vines
		vines.forEach((ticket, vine) -> {
			list.add(vine.getJSON());
		});
		
		return list;
	}
	
	public void makeVinesList(JSONArray list) {
		
		for (Object obj : list) {			
			JSONObject vine = (JSONObject) obj;
			
			// Further parsing here
			String path = (String)vine.get("imageName");
			// Default case.
			if (path == null) {
				path = "assets/vine1.png";
			}
			
			Double cxmD = (Double)vine.get("swingCenterX");
			double cxm;			
			// Default case.
			if (cxmD == null) {
				cxm = 3;
			} else {
				cxm = cxmD.doubleValue();
			}
			
			Double cymD = (Double)vine.get("swingCenterY");
			double cym;
			// Default case.
			if (cymD == null) {
				cym = 3;
			} else {
				cym = cymD.doubleValue();
			}
			
			Double wmD = (Double)vine.get("width");
			double wm;
			// Default case.
			if (wmD == null) {
				wm = 0.7;
			} else {
				wm = wmD.doubleValue();
			}
			
			Double hmD = (Double)vine.get("length");
			double hm;
			// Default case.
			if (hmD == null) {
				hm = 3;
			} else {
				hm = hmD.doubleValue();
			}
			
			Double arcLimitD = (Double)vine.get("arcLimit");
			double arcLimit;
			// Default case.
			if (arcLimitD == null) {
				arcLimit = 180;
			} else {
				arcLimit = arcLimitD.doubleValue();
			}
			
			Double startingVelD = (Double)vine.get("startingVelocity");
			double startingVel;
			// Default case.
			if (startingVelD == null) {
				startingVel = 0;
			} else {
				startingVel = startingVelD.doubleValue();
			}			
			
			// makeVine takes swing coordinates, so m is translated to px and y is flipped.
			makeVine(path, cxm * this.mToPixel, (this.lvhm - cym) * this.mToPixel, wm, hm, arcLimit, startingVel);
		}
	}
	
	
	public void makePlatList(JSONArray list, boolean polygon) {
		
		for (Object obj : list) {
			// Collision box array		
			ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
			
			JSONObject plat = (JSONObject) obj;
			// Further parsing here
			String path = "assets/" + (String)plat.get("imageName");				
			double cxm = (double)plat.get("centerX");
			double cym = (double)plat.get("centerY");
			
			// The width and height are already scaled.
			double swm = (double)plat.get("imageSizeWidth");
			double shm = (double)plat.get("imageSizeHeight");
			
			Double scaleD = (Double)plat.get("scale");
			double scale;
			if (scaleD == null) {
				scale = 1;
			} else {
				scale = scaleD.doubleValue();
			}
			
			if (polygon) {
				JSONArray collisionPoints = (JSONArray)plat.get("collisionPoints");
				
				// Get the points out of the array
				for (Object p : collisionPoints) {
					JSONObject point = (JSONObject) p;
					points.add(new Point2D.Double((double)point.get("x"), (double)point.get("y")));
				}
			} else {
				// Box collision. 
				double collisionWidth = (double)plat.get("collisionWidth");
				double collisionHeight = (double)plat.get("collisionHeight");
				
				JSONArray collisionPoints = (JSONArray)plat.get("collisionPoints");
				// Default case.
				if (collisionPoints == null) {
					// Use the collisionWidth and collisionHeight to make the points.
					points.add(new Point2D.Double(0 - collisionWidth/2, 0 - collisionHeight/2));
					points.add(new Point2D.Double(collisionWidth/2, collisionHeight/2));
				} else {				
					for (Object p : collisionPoints) {
						JSONObject point = (JSONObject) p;
						points.add(new Point2D.Double((double)point.get("x"), (double)point.get("y")));
					}		
				}
			}
			
			// makePlatform takes swing coordinates, so m is translated to px and y is flipped.			
			int ticket = makePlatform(path, cxm * this.mToPixel, (this.lvhm - cym) * this.mToPixel, scale);
			Platform newPlat = this.plats.get(ticket);
			
			// Set the other fields of the platform and its corresponding settings on load.
			ltlAdapter.setDimensions(ticket, scale);
			
			boolean disappears = (boolean)plat.get("disappears");
			newPlat.setDisappears(disappears);
			ltlAdapter.setDisappears(ticket, disappears);			
			
			boolean moveable = (boolean)plat.get("moveable");
			newPlat.setMoveable(moveable);
			ltlAdapter.setMoveable(ticket, moveable);
			
			boolean sinkable = (boolean)plat.get("sinkable");
			newPlat.setSinkable(sinkable);
			ltlAdapter.setSinkable(ticket, sinkable);
			
			boolean climbable = (boolean)plat.get("climbable");
			newPlat.setClimbable(climbable);
			ltlAdapter.setClimbable(ticket, climbable);
			
			double scK = (double)plat.get("springCK");
			newPlat.setPhysics(scK);
			ltlAdapter.setSCK(ticket, scK);
			
			double velocity = (double)plat.get("velocity");
			newPlat.setVelocity(velocity);
			ltlAdapter.setVelocity(ticket, velocity);
			
			double endX = (double)plat.get("endX");
			double endY = (double)plat.get("endY");
			newPlat.setEndpoint(endX, endY);
		}
	}
	
	public JSONObject getCharList() {
		JSONObject obj = new JSONObject();
		
		this.characters.forEach((name, character) -> {
			obj.put(name, character.getJSON());
		});
		
		return obj;		
	}
	
	
	public void makeCharList() {
		
	}
	
	public JSONArray getFGList() {
		JSONArray list = new JSONArray();		
		fg.forEach((foregroundObj) -> list.add(foregroundObj.getJSON()));
		return list;
		
	}
	
	public void makeFGList() {
		
	}
	
	// END JSON SECTION

}
