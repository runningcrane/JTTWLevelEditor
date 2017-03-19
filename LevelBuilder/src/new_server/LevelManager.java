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

import new_client.EditWindow;
import new_interactable.Boulder;
import new_interactable.NPC;
import new_interactable.Peg;
import new_interactable.Platform;
import new_interactable.Player;
import new_interactable.PropertyBook;
import new_interactable.Vine;
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
	 * Peg list. These are connected to boulder joints.
	 */
	private Map<Integer, Peg> pegs;

	/**
	 * NPC array.
	 */
	private Map<String, NPC> npcs;
	
	public enum Request {
		NONE, 
		MAKE_PLATFORM, MAKE_VINE, MAKE_BOULDER, MAKE_NPC, MAKE_PEG,
		EDIT_OLD_PLAT, EDIT_OLD_VINE, EDIT_OLD_BOULDER, EDIT_OLD_PEG,
		EDIT_MONK, EDIT_MONKEY, EDIT_PIG, EDIT_SANDY,
		SET_PLAYER_START_POS, SET_PLAT_ENDPOINT, 
		MARK_EOL, MARK_RP,
		REMOVE_RP
	}
	private Request request;
	private String requestPath;
	
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
		this.requestPath = "";
		
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
		this.pegs = new HashMap<Integer, Peg>();

		// Set up the player characters.
		characters = new HashMap<String, Player>();

		String[] players = {"Monkey", "Monk", "Piggy", "Sandy"};
		
		for (int i = 0; i < 4; i++) {
			Player player = new Player(ASSETS_PATH + players[i] + ".png");
			BufferedImage playerBI;
			try {
				playerBI = ImageIO.read(new File(ASSETS_PATH + players[i] + ".png"));
			} catch (IOException e) {
				System.err.println("File not found: " + ASSETS_PATH + players[i] + ".png");
				e.printStackTrace();
				return;
			}
			player.setBI(playerBI);
			player.setRI(resize(playerBI, 0.7, 1.7));
			player.setCenter(0, 0);
			player.getPropertyBook().getBoolList().put("present", false);
			characters.put(players[i], player);
		}		

		// Initialize the NPCs.
		npcs = new HashMap<String, NPC>();

	}
	
	/**
	 * Request a new entity to be made.
	 * @param path image path name
	 * @param requestType type of request
	 */
	public void setRequest(String path, String requestType) {
		this.requestPath = path;			
		
		switch(requestType) {
		case "Platform" : {
			this.request = Request.MAKE_PLATFORM;
		}
		case "Vine" : {
			this.request = Request.MAKE_VINE;
		}
		case "NPC" : {
			this.request = Request.MAKE_NPC;
		}
		case "Boulder" : {
			this.request = Request.MAKE_BOULDER;
		}
		case "Peg" : {
			this.request = Request.MAKE_PEG;
		}
		case "Monk" : {
			this.request = Request.EDIT_MONK;
		}
		case "Monkey" : {
			this.request = Request.EDIT_MONKEY;
		}
		case "Pig" : {
			this.request = Request.EDIT_PIG;
		}
		case "Sandy" : {
			this.request = Request.EDIT_SANDY;
		}
		case "EOL" : {
			this.request = Request.MARK_EOL;
		}
		case "RP" : {
			this.request = Request.MARK_RP;
		}
		case "RMRP" : {
			this.request = Request.REMOVE_RP;
		}
		default : {
			this.request = Request.NONE;
		}
		}
	}
	
	/**
	 * Receive coordinates from the OuputWindow. Do various actions depending on the request type.
	 * @param xp
	 * @param yp
	 */
	public void receiveCoordinates(double xp, double yp) {
		double xm = (xp - this.vpOffset.getX()) / this.mToPixel;
		double ym = this.lvhm - (yp - this.vpOffset.getY()) / this.mToPixel;
		
		switch (request) {
		case MAKE_PLATFORM: {
			makePlatform(this.requestPath, null, xm, ym);
		}
		case MAKE_VINE: {
			makeVine(this.requestPath, null, xm, ym);
		}
		case MAKE_BOULDER: {
			makeBoulder(this.requestPath, null, xm, ym);
		}
		case MAKE_NPC: {
			makeNPC(this.requestPath, null, xm, ym);
		}
		case MAKE_PEG: {
			makePeg(this.requestPath, null, xm, ym);
		}
		case EDIT_OLD_PLAT: {
			Platform target = this.plats.get(requestNum);
			if (target != null) {
				// Set the center.
				target.setCenter(xm, ym);
			}
		}
		case EDIT_OLD_VINE: {
			Vine target = this.vines.get(requestNum);
			if (target != null) {
				// Set the center.
				target.setCenter(xm, ym);
			}
		}
		case EDIT_OLD_BOULDER: {
			Boulder target = this.boulders.get(requestNum);
			if (target != null) {
				// Set the center.
				target.setCenter(xm, ym);
			}
		}
		case EDIT_OLD_PEG: {
			Peg target = this.pegs.get(requestNum);
			if (target != null) {
				// Set the center.
				target.setCenter(xm, ym);
			}
		}
		case EDIT_MONK: {
			this.characters.get("Monk").setCenter(xm, ym);
		}
		case EDIT_MONKEY: {
			this.characters.get("Monkey").setCenter(xm, ym);
		}
		case EDIT_PIG: {
			this.characters.get("Piggy").setCenter(xm, ym);
		}
		case EDIT_SANDY: {
			this.characters.get("Sandy").setCenter(xm, ym);
		}
		case SET_PLAT_ENDPOINT: {
			// TODO
		}
		case MARK_EOL: {
			this.eol = new Point2D.Double(xm, ym);
		}
		case MARK_RP: {
			this.respawnPoints.add(new Point2D.Double(xm, ym));
		}
		case REMOVE_RP: {
			Point2D.Double[] closest = new Point2D.Double[]{new Point2D.Double(-1, -1)};
			double[] distance = new double[]{Double.MAX_VALUE};
			
			this.respawnPoints.forEach((point) -> {
				// Calculate distance from the point to the clicked point.
				double xDiff = Math.abs(point.x - xm);
				double yDiff = Math.abs(point.y - ym);
				double tempDist = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
				if (tempDist < distance[0]) {
					// Closer point found. Update.
					distance[0] = tempDist;
					closest[0] = point;
				}
			});
			
			// Don't remove if nothing was found.
			if (closest[0].x != -1) {
				// Don't remove unless the click was close enough to the label.
				if (distance[0] < 2) {
					this.respawnPoints.remove(closest[0]);
				}
			}
		}
		case NONE: {
			
		}
		}
		
		this.request = Request.NONE;
		this.requestNum = 0;
		this.requestPath = "";
	}
	
	public void makePlatform (String imageName, PropertyBook book, double xm, double ym) {
		Platform plat = new Platform(this.ticketer, imageName);
		
		// Make an EditWindow.
		makePlatEditWindow(this.ticketer, plat, book);
				
		// Add it the known list of platforms.
		this.plats.put(this.ticketer, plat);
		
		// Increase the ticket count.
		this.ticketer++;
	}
	
	/**
	 * Make a boulder's edit window.
	 * @param ticket ticket number of boulder
	 * @param plat actual boulder object
	 * @param book property book for the boulder, if it had pre-existing properties
	 */
	public void makePlatformEditWindow(int ticket, Platform plat, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Platform");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				plat.updateProperties(window.getPropertyBook());				
			}			
		});
		
		// Set up boulder properties.
		window.makeButton("New center", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.EDIT_OLD_PLAT;
				requestNum = ticket;	
			}		
		});
		
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeBooleanProperty("Disappears", (book == null) 
				? false 
				: ((book.getBoolList().get("Disappears") == null 
					? false
					: book.getBoolList().get("Disappears")))
		);
		
		window.makeBooleanProperty("Moving", (book == null) 
				? false 
				: ((book.getBoolList().get("Moving") == null 
					? false
					: book.getBoolList().get("Moving")))
		);
		
		window.makeButton("Set Endpoint", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.SET_PLAT_ENDPOINT;
				requestNum = ticket;
			}			
		});
		
		window.makeDoubleProperty("Velocity", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Velocity") == null 
					? 1.0  
					: book.getDoubList().get("Velocity")))
		);
		
		window.makeBooleanProperty("Sinkable", (book == null) 
				? false 
				: ((book.getBoolList().get("Sinkable") == null 
					? false
					: book.getBoolList().get("Sinkable")))
		);
		
		window.makeDoubleProperty("Spring Constant K", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Spring Constant K") == null 
					? 1.0  
					: book.getDoubList().get("Spring Constant K")))
		);
		
		window.makeBooleanProperty("Climbable", (book == null) 
				? false 
				: ((book.getBoolList().get("Climbable") == null 
					? false
					: book.getBoolList().get("Climbable")))
		);
		
		window.makeBooleanProperty("Collidable", (book == null) 
				? false 
				: ((book.getBoolList().get("Collidable") == null 
					? false
					: book.getBoolList().get("Collidable")))
		);
		
		window.makeBooleanProperty("Polygon collision", (book == null) 
				? false
				: ((book.getBoolList().get("Polygon collision") == null 
					? false
					: book.getBoolList().get("Polygon collision")))
		);

	}
	
	public void makeVine (String imageName, PropertyBook book, double xm, double ym) {
		Vine vine = new Vine(this.ticketer, imageName);
		
		// Make an EditWindow.
		makeVineEditWindow(this.ticketer, vine, book);
				
		// Add it the known list of vines.
		this.vines.put(this.ticketer, vine);
		
		// Increase the ticket count.
		this.ticketer++;
	}
	
	
	public void makeNPC (String imageName, PropertyBook book, double xm, double ym) {
		NPC npc = new NPC(this.ticketer, imageName);
		
		// Make an EditWindow.
		makeNPCEditWindow(this.ticketer, npc, book);
				
		// Add it the known list of NPCs.
		this.npcs.put(this.ticketer, npc);
		
		// Increase the ticket count.
		this.ticketer++;
	}
	
	public void makeBoulder(String imageName, PropertyBook book, double xm, double ym) {
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
	
	public void makePeg (String imageName, PropertyBook book, double xm, double ym) {
		Peg peg = new Peg(this.ticketer, imageName);
		
		// Make an EditWindow.
		makePegEditWindow(this.ticketer, peg, book);
				
		// Add it the known list of NPCs.
		this.pegs.put(this.ticketer, peg);
		
		// Increase the ticket count.
		this.ticketer++;
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
				(ticket, plat) -> plat.setRI(resize(plat.getBI(), plat.getScaledIGWM(), plat.getScaledIGHM())));
		this.characters.forEach((name, player) -> player.setRI(resize(player.getBI(), 0.7, 1.7)));
		// TODO: Why are vines using IGW instead of IGWM?
		this.vines.forEach((ticket, vine) -> vine
				.setRI(resize(vine.getBI(), vine.getInGameWidth(), vine.getInGameHeight())));
		this.boulders.forEach((ticket, boulder) -> boulder
				.setRI(resize(boulder.getBI(), boulder.getScaledIGWM(), boulder.getScaledIGHM())));
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
