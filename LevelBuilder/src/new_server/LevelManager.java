package new_server;

import static utils.Constants.ASSETS_PATH;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.json.simple.JSONObject;

import interactable.BoulderJoint;
import interactable.Enemy;
import interactable.GoldenPeg;
import interactable.Platform;
import interactable.Player;
import interactable.Vine;
import new_client.EditWindow;
import new_interactable.Boulder;
import new_interactable.PropertyBook;
import noninteractable.Background;
import noninteractable.INonInteractable;

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
	 * List of respawn points for the level.
	 */
	private ArrayList<Point2D.Double> respawnPoints; 

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
	 * Boulder joint list.
	 */
	private Map<Integer, BoulderJoint> joints;
	
	/**
	 * Peg list. These are connected to boulder joints.
	 */
	private Map<Integer, GoldenPeg> pegs;

	/**
	 * NPC array.
	 */
	private Map<String, Enemy> npcs;
	
	public enum Request {
		NONE, 
		MAKE_PLATFORM, MAKE_VINE, MAKE_BOULDER, MAKE_NPC, MAKE_BOULDER_JOINT, MAKE_PEG,
		EDIT_OLD_PLAT, EDIT_OLD_VINE, EDIT_OLD_BOULDER, EDIT_OLD_PEG,
		SET_PLAYER_START_POS, SET_PLAT_ENDPOINT, 
		MARK_EOL, MARK_RP,
		REMOVE_RP
	}
	private Request request;
	
	/**
	 * Ticket number of the requesting object.
	 */
	private int requestNum;		

	/**
	 * Gives out ticket values.
	 */
	int ticketer;
	
	/**
	 * Adapter from the LevelManager to a LayerWIndow.
	 */
	ILevelToLayerAdapter ltlAdapter;
	
	/**
	 * Adapter from the LevelManager to the OuputWindow.
	 */
	ILevelToOutputAdapter ltoAdapter;
	
	/**
	 * Adapter to communicate with the controls panel.
	 */
	private ILevelToControlAdapter ltcAdapter;
	
	/**
	 * Time to update the level view.
	 */
	private Timer timer;

	/**
	 * How often the timer should be called.
	 */
	private int timeSlice;
	
	/**
	 * The model side of the MVC structure. Level Manager manages everything 
	 * about the current level, including loading and saving levels.
	 */
	public LevelManager(ILevelToControlAdapter ltbAdapter, ILevelToOutputAdapter ltoAdapter,
			ILevelToLayerAdapter ltlAdapter) {
		this.ltcAdapter = ltbAdapter;
		this.ltoAdapter = ltoAdapter;
		this.ltlAdapter = ltlAdapter;
		
		this.ticketer = 1;
		this.request = Request.NONE;
		this.requestNum = 1;
		
		// No real reason to put it to 5,8 other than just to initialize it.
		this.eol = new Point2D.Double(5, 8);
		
		this.mToPixel = 100;
		this.vpOffset = new Point2D.Double(0, 0);
		
		// Call upon the layer view to render itself frequently.
		ActionListener listen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ltoAdapter.redraw();
			}
		};

		this.timeSlice = 50; // update every 50 milliseconds
		this.timer = new Timer(timeSlice, listen);
		
		// TODO: Replace later. This is a manual set of width and height of the
		// background.
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
		this.respawnPoints = new ArrayList<Point2D.Double>();
		this.joints = new HashMap<Integer, BoulderJoint>();
		this.pegs = new HashMap<Integer, GoldenPeg>();

		// Set up the player characters.
		characters = new HashMap<String, Player>();

		// String path, String name, double cxm, double cym, boolean present

		Player monkey = new Player(ASSETS_PATH + "Monkey.png", "Monkey", 0, 0, false);
		BufferedImage monkeyBI;
		try {
			monkeyBI = ImageIO.read(new File(ASSETS_PATH + "Monkey.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + "Monkey.png");
			e.printStackTrace();
			return;
		}
		monkey.setImage(monkeyBI);
		monkey.setRescaled(resize(monkeyBI, 0.7, 1.7));
		characters.put("Monkey", monkey);

		Player monk = new Player(ASSETS_PATH + "Monk.png", "Monk", 0, 0, false);
		BufferedImage monkBI;
		try {
			monkBI = ImageIO.read(new File(ASSETS_PATH + "Monk.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + "Monk.png");
			e.printStackTrace();
			return;
		}
		monk.setImage(monkBI);
		monk.setRescaled(resize(monkBI, 0.7, 1.7));
		characters.put("Monk", monk);

		Player pig = new Player(ASSETS_PATH + "Piggy.png", "Piggy", 0, 0, false);
		BufferedImage pigBI;
		try {
			pigBI = ImageIO.read(new File(ASSETS_PATH + "Piggy.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + "Piggy.png");
			e.printStackTrace();
			return;
		}
		pig.setImage(pigBI);
		pig.setRescaled(resize(pigBI, 0.7, 1.7));
		characters.put("Piggy", pig);

		Player sandy = new Player(ASSETS_PATH + "Sandy.png", "Sandy", 0, 0, false);
		BufferedImage sandyBI;
		try {
			sandyBI = ImageIO.read(new File(ASSETS_PATH + "Sandy.png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + "Sandy.png");
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
	 * Receive coordinates from the OuputWindow. Do various actions depending on the request type.
	 * @param x
	 * @param y
	 */
	public void receiveCoordinates(double x, double y) {
		switch (request) {
		case EDIT_OLD_BOULDER: {
			Boulder target = this.boulders.get(requestNum);
			if (target != null) {
				// TODO: Chance x & y into [cocos,m] coordinates.
				
				// Set the center.
				target.setCenter(x, y);
			}
		}
		case NONE: {
			
		}
		}
	}
	
	public void makeBoulder(String imageName, PropertyBook book) {
		Boulder newBoulder = new Boulder(this.ticketer, imageName);
		
		// Make an EditWindow.
		makeBoulderEditWindow(this.ticketer, newBoulder, book);
		
		// Add it the known list of boulders.
		this.boulders.put(this.ticketer, newBoulder);
		
		// Increase the ticket count.
		this.ticketer++;
	}
	
	/**
	 * Make a boulder's edit window.
	 * @param ticket ticket number of boulder
	 * @param boulder actual boulder object
	 * @param book property book for the boulder, if it had pre-existing properties
	 */
	public void makeBoulderEditWindow(int ticket, Boulder boulder, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Boulder");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boulder.updateProperties(window.getPropertyBook());				
			}			
		});
		
		// Set up boulder properties.
		window.makeButton("New center", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.EDIT_OLD_BOULDER;
				requestNum = ticket;
				ltoAdapter.requestCoordinates();	
			}		
		});
		
		window.makeDoubleProperty("Mass", (book == null) 
				? 1000.0 
				: ((book.getDoubList().get("Mass") == null 
					? 1000.0 
					: book.getDoubList().get("Mass")))
		);
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeBooleanProperty("Polygon collision", (book == null) 
				? false
				: ((book.getBoolList().get("Polygon collision") == null 
					? false
					: book.getBoolList().get("Polygon collision")))
		);

	}
	
	public void changeOffset(double xm, double ym) {

		this.vpOffset = new Point2D.Double(this.vpOffset.getX() + xm * this.mToPixel,
				this.vpOffset.getY() + ym * this.mToPixel);
	}

	public void clearAndSet() {
		// Reset the NPCs.
		this.npcs.clear();

		// Clear the current list of platforms.
		this.plats.clear();

		// Clear the current list of vines.
		this.vines.clear();
		
		// Clear the current list of boulders.
		this.boulders.clear();
		
		// Clear the boulder joints.
		this.joints.clear();
		
		// Clear the current list of respawn points.		
		this.respawnPoints.clear();
		
		// By the way, they all need to be removed from the LayerWindow as well.
		this.ltlAdapter.clear();

		// Reset the ticketer.
		this.ticketer = 1;

		// Reset the viewpoint starting point.
		this.vpOffset = new Point2D.Double(0, 0);
	}
	
	public void setViewportDimensions(double wm, double hm) {
		ltoAdapter.setDimensions((int) (wm * mToPixel), (int) (hm * mToPixel));
		this.vpwm = wm;
		this.vphm = hm;
	}
	
	public void setNextName(String name) {
		ltoAdapter.setNextName(name);
	}
	
	/**
	 * Request output window to change its size.
	 * 
	 * @param wm
	 *            width in in-game meters
	 * @param hm
	 *            height in in-game meters
	 */
	public void setLevelDimensions(double wm, double hm) {
		this.lvwm = wm;
		this.lvhm = hm;

		// Need to resize the background based on whichever scale is the biggest
		this.setBg(this.bg.getPath());
	}

	public void setLevelName(String name) {
		ltoAdapter.setLevelName(name);
	}

	/**
	 * Request output window to change the number of pixels in a meter.
	 * 
	 * @param mToPixel
	 */
	public void setMToPixel(double mToPixel) {
		this.mToPixel = mToPixel;

		// Unfortunately this requires resetting ALL of the rescaled images.
		this.plats.forEach(
				(ticket, plat) -> plat.setRescaled(resize(plat.getImage(), plat.getScaledIGW(), plat.getScaledIGH())));
		this.characters.forEach((name, player) -> player.setRescaled(resize(player.getImage(), 0.7, 1.7)));
		this.vines.forEach((ticket, vine) -> vine
				.setRescaled(resize(vine.getImage(), vine.getInGameWidth(), vine.getInGameHeight())));
		this.boulders.forEach((ticket, boulder) -> boulder
				.setRescaled(resize(boulder.getImage(), boulder.getScaledIGW(), boulder.getScaledIGH())));
		// this.npcs.forEach((name, enemy) ->
		// enemy.setRescaled(resize(enemy.getImage(), enemy.getInGameWidth(),
		// enemy.getInGameHeight())));
		this.bg.setRescaled(resize(this.bg.getImage(), this.lvwm, this.lvhm));
	}
	
	/**
	 * Sets background of level.
	 * 
	 * @param path
	 *            path to background image
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
	 * Scales an image to in-game meters size.
	 * 
	 * @param widthMeters
	 *            expected in-game meters
	 * @param original
	 *            contains image in original pixel dimensions
	 * @return
	 */
	public ImageIcon resize(BufferedImage original, double wm, double hm) {
		double expectedWidth = wm * this.mToPixel;
		double widthScale = expectedWidth / original.getWidth();

		double expectedHeight = hm * this.mToPixel;
		double heightScale = expectedHeight / original.getHeight();

		return new ImageIcon(original.getScaledInstance((int) (original.getWidth() * widthScale),
				(int) (original.getHeight() * heightScale), java.awt.Image.SCALE_SMOOTH));
	}
	
	/**
	 * After constructor is done initializing, start operations.
	 */
	public void start() {
		setBg(ASSETS_PATH + "bgSunny.png");
		this.timer.start();
	}
}
