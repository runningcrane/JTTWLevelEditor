package server;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
	private ArrayList<Platform> plats;
	
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
		this.bg.setRescaled(resize(bgImage));
	}
	
	/**
	 * Renders the output window.
	 * @param panel JPanel to render
	 * @param g graphics of component
	 */
	public void render(Component panel, Graphics g) {
		// Draw background
		//if (bg != null)			
			//g.drawImage(bg.getImage(), 0, 0, null);
		if (bg.getRescaled() != null)
			g.drawImage(bg.getRescaled().getImage(), 0, 0, null);
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
	public ImageIcon resize(BufferedImage original) {			
		double expectedWidth = this.wm * this.mToPixel;
		double widthScale = expectedWidth/original.getWidth();
		
		double expectedHeight = this.hm * this.mToPixel;
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
	 * TODO: I really should be checking these return types. 
	 */
	public JSONObject makeJSON() {
		JSONObject json = new JSONObject();
		JSONArray platList = getPlatList();
		json.put("platforms", platList);
		return json;
	}
	
	public JSONArray getPlatList() {
		JSONArray list = new JSONArray();
		
		// Background first
		list.add(this.bg.getJSON());
		
		// Platforms next
		// plats.forEach((platform) -> list.add(platform.getJSON()));	
		
		return list;
	}
	
	public JSONArray getCharList() {
		JSONArray list = new JSONArray();
		
		// TODO: Characters would be next if I had them in.
		return null;
		
	}
	
	public JSONArray getFGList() {
		JSONArray list = new JSONArray();
		
		// TODO: Then foreground objects.
		return null;
		
	}

}
