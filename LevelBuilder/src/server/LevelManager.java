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

import interactable.Platform;
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
	 * Background object. 
	 */
	private Background bg;
	
	private double wm;
	
	private double hm;
	
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
	private Map<String, Point2D.Double> charLocs; 
	
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
	 */
	public LevelManager(ILevelToControlAdapter ltbAdapter, ILevelToOutputAdapter ltoAdapter,
			ILevelToLayerAdapter ltlAdapter) {
		this.ltbAdapter = ltbAdapter;
		this.ltoAdapter = ltoAdapter;
		this.ltlAdapter = ltlAdapter;
		
		this.mToPixel = 100;		
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
		this.wm = 7;
		this.hm = 4;
		
		// Instantiate various lists.
		this.plats = new HashMap<Integer, Platform>();
		this.fg = new ArrayList<INonInteractable>();
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
		
		this.bg = new Background(bgImage, path, this.wm, this.hm);
		this.bg.setRescaled(resize(bgImage, this.wm, this.hm));
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
			// Unfortunately drawImage draws on the top left, not center, so adjustments are made.			
			g.drawImage(plat.getRescaled().getImage(), (int)((plat.getCenterXm() - plat.getInGameWidth()/2) * this.mToPixel),
					(int)((plat.getCenterYm() - plat.getInGameHeight()/2) * this.mToPixel), null);
			
			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);
			g.fillOval((int)(plat.getCenterXm() * this.mToPixel), (int)(plat.getCenterYm() * this.mToPixel), 15, 15);
			// Label point
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(number), (int)(plat.getCenterXm() * this.mToPixel + 5), 
					(int)(plat.getCenterYm() * this.mToPixel + 10));
		});				
	}
	
	/**
	 * Request output window to change its size.
	 * @param wm width in in-game meters
	 * @param hm height in in-game meters
	 */
	public void setLevelDimensions(double wm, double hm) {
		ltoAdapter.setDimensions((int)(wm * mToPixel), (int)(hm * mToPixel));	
		this.wm = wm;
		this.hm = hm;
		
		// Need to resize the background based on whichever scale is the biggest
		this.setBg(this.bg.getPath());
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
		this.wm = wp / this.mToPixel;
		this.hm = hp / this.mToPixel;
		
		this.setBg(this.bg.getPath());		
	}
	
	/**
	 * Makes a new platform object.
	 * @param path 
	 * @param xp xpixel location on screen
	 * @param yp ypixel location on screen
	 * @param wm expected width in in-game meters
	 * @param hm expected height in in-game meters
	 */
	public void makePlatform(String path, double xp, double yp, double wm, double hm,
			ArrayList<Point2D.Double> points) {
		Platform platform;
		
		// Are we making a new platform, or is this a platform that already has a collision box?
		if (points == null) {
			platform = new Platform(path, xp / this.mToPixel, yp / this.mToPixel, wm, hm, false);
		} else {
			platform = new Platform(path, xp / this.mToPixel, yp / this.mToPixel, wm, hm, true);
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
		this.ticket++;
		
		// Let the layer window know a platform has been made
		
	}

	/**
	 * Toggle players. 
	 * TODO: This is work for later.	
	 * @param name name of character to toggle
	 * @param status state of existance or not
	 */
	public void togglePlayer(String name, boolean status) {
		
	}
	
	/**
	 * The user wants to add a platform. Tell the output window to request focus,
	 * and what kind of platform to make.
	 * @param path
	 */
	public void setActive(String path) {
		ltoAdapter.makePlatform(path);
	}
	
	/**
	 * Outputs a JSON file.
	 * @param levelName name of the level
	 * @return JSON to be outputted 
	 */
	public JSONObject makeJSON(String levelName, boolean polygon) {
		System.out.println("Writing with collision = " + polygon);
		JSONObject json = new JSONObject();
		JSONArray platList = getPlatList(polygon);
		//JSONObject charLocs = getCharLocs(this.charLocs);
		
		json.put("levelName", levelName);
		json.put("background", this.bg.getJSON());		
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
		JSONObject level = (JSONObject) obj;
		String name = (String) level.get("levelName");
		setLevelName(name);
		
		JSONObject bg = (JSONObject) level.get("background");
		makeBackground(bg);
		
		Boolean polygon = (Boolean) level.get("polygonCollision");
		System.out.println("Collision: " + polygon);
		
		JSONArray plats = (JSONArray) level.get("platforms");
		makePlatList(plats, polygon);
		
		// Reset LevelManager, etc variables
		
	}
	
	public void setLevelName(String name) {		
		ltoAdapter.setLevelName(name);
	}
	
	
	public void makeBackground(JSONObject bg) {
		String imageName = (String)bg.get("imageName");
		double levelWidth = (double)bg.get("width");
		double levelHeight = (double)bg.get("height");
		
		// Set wm, hm.
		this.wm = levelWidth;
		this.hm = levelHeight;
		
		// Set the background.
		setBg("assets/" + imageName);
		
		// Set the dimensions.
		setLevelDimensions(this.wm, this.hm);
		System.out.println("Dimensions set to " + this.wm + "x" + this.hm);
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
			
			makePlatform(path, cxm * this.mToPixel, cym * this.mToPixel, wm, hm, points);
								
		}
	}
	
	public JSONArray getCharList() {
		JSONArray list = new JSONArray();
		
		// TODO: Characters would be next if I had them in.
		return null;
		
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

}
