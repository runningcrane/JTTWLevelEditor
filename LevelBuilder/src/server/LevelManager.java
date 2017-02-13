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

import interactable.Enemy;
import interactable.Platform;
import interactable.Player;
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
	private ILevelToControlAdapter ltbAdapter;
	
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
		this.ltbAdapter = ltbAdapter;
		this.ltoAdapter = ltoAdapter;
		this.ltlAdapter = ltlAdapter;
		
		this.mToPixel = 100;		
		this.vpOffset = new Point2D.Double(0, 0);
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
		this.lvwm = 8;
		this.lvhm = 6;
		
		// Instantiate various lists.
		this.plats = new HashMap<Integer, Platform>();
		this.fg = new ArrayList<INonInteractable>();
		
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
		sandy.setRescaled(resize(sandyBI, 0.7, 1.7));
		characters.put("Sandy", sandy);		
		
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
		if (bg.getRescaled() != null)
			g.drawImage(bg.getRescaled().getImage(), 0, 0, null);
		
		// Draw platforms		
		plats.forEach((number, plat) -> {
			/*
			 * Unfortunately drawImage draws on the top left, not center, so adjustments are made.
			 * Also unfortunately, cocos uses a different orientation system than swing. 
			 * Namely, the y is flipped in direction.			
			 * Since CenterXm/CenterYm are in swing-orientation, reverse the ys to get them in cocos.
			 */		
			double ulxp = (plat.getCenterXm() - plat.getInGameWidth()/2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - plat.getCenterYm()) - plat.getInGameHeight() /2.0) * this.mToPixel;			
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
	}
	
	/**
	 * For rendering purposes. Offsets the real world coordinates by the viewport offset.
	 * @param x real world swing coordinate
	 * @param y real world swing coordinate
	 * @return (x,y) viewport swing coordanites
	 */
	public Point2D.Double getViewportCoordinates(double x, double y) {
		return new Point2D.Double(x + this.vpOffset.getX(), y + this.vpOffset.getY());
	}
	
	public void changeOffset(double xm, double ym) {
		
		this.vpOffset = new Point2D.Double(this.vpOffset.getX() + xm * this.mToPixel, 
				this.vpOffset.getY() + ym * this.mToPixel);
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
		this.lvwm = wp / this.mToPixel;
		this.lvhm = hp / this.mToPixel;
		
		this.setBg(this.bg.getPath());		
	}
	
	/**
	 * Makes a new platform object.
	 * @param path 
	 * @param xp xpixel location on screen [swing vp coordinates]
	 * @param yp ypixel location on screen [swing vp coordinates]
	 * @param wm expected width in in-game meters
	 * @param hm expected height in in-game meters
	 */
	public void makePlatform(String path, double xp, double yp, double wm, double hm,
			ArrayList<Point2D.Double> points) {
		Platform platform;
		// Unfortunately Eclipse and Coco have different coordinate systems. Change cym appropriately.
		
		// Are we making a new platform, or is this a platform that already has a collision box?
		if (points == null) {
			platform = new Platform(path, (xp - this.vpOffset.getX()) / this.mToPixel, 
					this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel, wm, hm, false);
		} else {
			platform = new Platform(path, (xp - this.vpOffset.getX()) / this.mToPixel, 
					this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel, wm, hm, true);
			platform.setCollisionBox(points);
		}
		
		BufferedImage image;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("File not found: " + path);
			e.printStackTrace();
			return;
		}
		
		platform.setImage(image);
		platform.setRescaled(resize(image, wm, hm));
		
		plats.put(this.ticket, platform);
		ltlAdapter.addEdit(this.ticket);
		this.ticket++;
		
		// Let the layer window know a platform has been made
		
	}	

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
	
	
	/**
	 * The user wants to add a platform. Tell the output window to request focus,
	 * and what kind of platform to make.
	 * @param path
	 */
	public void setActive(String path) {
		ltoAdapter.makePlatform(path);
	}
	
	// START EDITING SECTION
	
	/**
	 * Ask the OutputWindow for new center coordinates.
	 * @param ticket platform identifier
	 */
	public void editPlatCenter(int ticket) {
		ltoAdapter.setPlatPos(ticket);				
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
	
	public void editPlatCollisionBox(int ticket) {
		//this.plats.get(ticket).setPlatCollisionBox();			
	}

	public void removePlat(int ticket) {
		this.plats.remove(ticket);			
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
	
	
	// END EDITING SECTION
	
	
	// START JSON SECTION
	
	/**
	 * Outputs a JSON file.
	 * @param levelName name of the level
	 * @return JSON to be outputted 
	 */
	public JSONObject makeJSON(String levelName, boolean polygon) {
		System.out.println("Writing with collision = " + polygon);
		JSONObject json = new JSONObject();
		JSONArray platList = getPlatList(polygon);
		JSONObject charList = getCharList();
		//JSONObject charLocs = getCharLocs(this.charLocs);
		
		json.put("levelName", levelName);
		json.put("background", this.bg.getJSON());
		json.put("characters", charList);
		json.put("platforms", platList);
		json.put("polygonCollision", polygon);
		//json.put("charactersStart", charLocs);
		return json;
	}

	
	/**
	 * Reads in a level from its JSON.	 
	 * @param levelPath path to the level JSON	 
	 */
	public void readJSON(String levelPath) {
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
		this.vpOffset = new Point2D.Double(0, 0);
		
		JSONObject level = (JSONObject) obj;
		String name = (String) level.get("levelName");
		setLevelName(name);
		
		JSONObject bg = (JSONObject) level.get("background");
		makeBackground(bg);
		
		Boolean polygon = (Boolean) level.get("polygonCollision");
		System.out.println("Collision: " + polygon);
		
		JSONArray plats = (JSONArray) level.get("platforms");
		makePlatList(plats, polygon);
		
		JSONObject characters = (JSONObject) level.get("characters");
		setCharacters(characters);
		
		// Reset LevelManager, etc variables
		
	}
	
	public void setCharacters(JSONObject chars) {
		// TODO: This could be done in a foreach loop if we used a JSONArray... talk to Bryce
		JSONObject monkey = (JSONObject)chars.get("Monkey");
		double cxm = (double)monkey.get("startingXPos");
		double cym = (double)monkey.get("startingYPos");
		boolean present = (boolean)monkey.get("present");
		this.characters.get("Monkey").setCenter(cxm, cym);
		this.characters.get("Monkey").setPresent(present);		
		
		JSONObject monk = (JSONObject)chars.get("Monk");
		double monkcxm = (double)monk.get("startingXPos");
		double monkcym = (double)monk.get("startingYPos");
		boolean monkpresent = (boolean)monk.get("present");
		this.characters.get("Monk").setCenter(monkcxm, monkcym);
		this.characters.get("Monk").setPresent(monkpresent);
		
		JSONObject pig = (JSONObject)chars.get("Piggy");
		double pigcxm = (double)pig.get("startingXPos");
		double pigcym = (double)pig.get("startingYPos");
		boolean pigpresent = (boolean)pig.get("present");
		this.characters.get("Piggy").setCenter(pigcxm, pigcym);
		this.characters.get("Piggy").setPresent(pigpresent);
		
		JSONObject sandy = (JSONObject)chars.get("Sandy");
		double sandycxm = (double)sandy.get("startingXPos");
		double sandycym = (double)sandy.get("startingYPos");
		boolean sandypresent = (boolean)sandy.get("present");
		this.characters.get("Sandy").setCenter(sandycxm, sandycym);
		this.characters.get("Sandy").setPresent(sandypresent);
		
	}
	
	public void setLevelName(String name) {		
		ltoAdapter.setLevelName(name);
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
	
	public void makePlatList(JSONArray list, boolean polygon) {
		// First, empty any platforms that might've existed
		this.plats.clear();
		
		// Now, reset ticket
		this.ticket = 1;
		
		// Collision box array
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		
		for (Object obj : list) {
			JSONObject plat = (JSONObject) obj;
			// Further parsing here
			String path = "assets/" + (String)plat.get("imageName");				
			double cxm = (double)plat.get("centerX");
			double cym = (double)plat.get("centerY");
			double wm = (double)plat.get("imageSizeWidth");
			double hm = (double)plat.get("imageSizeHeight");
			
			if (polygon) {
				JSONArray collisionPoints = (JSONArray)plat.get("collisionPoints");
				
				// Get the points out of the array
				for (Object p : collisionPoints) {
					JSONObject point = (JSONObject) p;
					points.add(new Point2D.Double((double)point.get("x"), (double)point.get("y")));
				}
			} else {
				// Box collision; make two points based on the width and height.
				double collisionWidth = (double)plat.get("collisionWidth");
				double collisionHeight = (double)plat.get("collisionHeight");
				points.add(new Point2D.Double(cxm * this.mToPixel - 0.5 * collisionWidth * this.mToPixel,
						cym * this.mToPixel - 0.5 * collisionHeight * this.mToPixel));
				points.add(new Point2D.Double(cxm * this.mToPixel + 0.5 * collisionWidth * this.mToPixel,
						cym * this.mToPixel + 0.5 * collisionHeight * this.mToPixel));
			}
			
			// makePlatform takes swing coordinates, so m is translated to px and y is flipped.
			makePlatform(path, cxm * this.mToPixel, (this.lvhm - cym) * this.mToPixel, wm, hm, points);
								
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
