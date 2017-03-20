package new_server;

import static utils.Constants.ASSETS_PATH;
import static utils.Constants.COL_PATH;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
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

import org.json.simple.JSONObject;

import com.google.gson.Gson;

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
	private Map<Integer, NPC> npcs;
	
	public enum Request {
		NONE, 
		MAKE_PLATFORM, MAKE_VINE, MAKE_BOULDER, MAKE_NPC, MAKE_PEG,
		EDIT_OLD_PLAT, EDIT_OLD_VINE, EDIT_OLD_BOULDER, EDIT_OLD_PEG,
		EDIT_MONK, EDIT_MONKEY, EDIT_PIG, EDIT_SANDY,
		SET_PLAT_ENDPOINT, MARK_EOL, MARK_RP,
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
			player.getPropertyBook().getBoolList().put("Present", false);
			characters.put(players[i], player);
		}		

		// Initialize the NPCs.
		npcs = new HashMap<Integer, NPC>();

	}
	
	/**
	 * For rendering purposes. Offsets the real world coordinates by the
	 * viewport offset.
	 * 
	 * @param x
	 *            real world swing coordinate
	 * @param y
	 *            real world swing coordinate
	 * @return (x,y) viewport swing coordinates
	 */
	public Point2D.Double getViewportCoordinates(double x, double y) {
		return new Point2D.Double(x + this.vpOffset.getX(), y + this.vpOffset.getY());
	}
	
	/**
	 * Renders the output window.
	 * 
	 * @param panel
	 *            JPanel to render
	 * @param g
	 *            graphics of component
	 */
	public void render(Component panel, Graphics g) {
		// Draw background
		if (bg.getRescaled() != null) {
			Point2D.Double vpbg = getViewportCoordinates(0, 0);
			g.drawImage(bg.getRescaled().getImage(), (int) vpbg.getX(), (int) vpbg.getY(), null);
		}

		// Draw platforms
		plats.forEach((number, plat) -> {
			/*
			 * Unfortunately drawImage draws on the top left, not center, so
			 * adjustments are made. Also unfortunately, cocos uses a different
			 * orientation system than swing. Namely, the y is flipped in
			 * direction. Since CenterXm/CenterYm are in swing-orientation,
			 * reverse the ys to get them in cocos.
			 */
			double ulxp = (plat.getCenterXM() - plat.getScaledIGWM() / 2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - plat.getCenterYM()) - plat.getScaledIGHM() / 2.0) * this.mToPixel;
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);

			g.drawImage(plat.getRI().getImage(), (int) vpulp.getX(), (int) vpulp.getY(), null);

			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(plat.getCenterXM() * this.mToPixel,
					(this.lvhm - (plat.getCenterYM())) * this.mToPixel);
			g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);

			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(plat.getCenterXM() * this.mToPixel + 5,
					(this.lvhm - (plat.getCenterYM())) * this.mToPixel + 10);
			g.drawString(Integer.toString(number), (int) (vplbp.getX()), (int) (vplbp.getY()));

			// If moveable, show its endpoint and a line to its endpoint.
			if (plat.getPropertyBook().getBoolList().get("Moving").booleanValue()) {
				Point2D.Double endpoint = plat.getEndpoint();
				Point2D.Double vplbep = getViewportCoordinates(endpoint.getX() * this.mToPixel + 5,
						(this.lvhm - (endpoint.getY())) * this.mToPixel + 10);

				// Draw the line first.
				g.setColor(Color.MAGENTA);
				g.drawLine((int) vplp.getX(), (int) vplp.getY(), (int) vplbep.getX(), (int) vplbep.getY());

				// Then draw the endpoint.
				g.setColor(Color.RED);
				g.fillOval((int) (vplbep.getX()), (int) (vplbep.getY()), 15, 15);

				// Then label it.
				g.setColor(Color.BLACK);
				Point2D.Double vplbnep = getViewportCoordinates(endpoint.getX() * this.mToPixel + 5,
						(this.lvhm - (endpoint.getY())) * this.mToPixel + 10);
				g.drawString(Integer.toString(number) + "EP", (int) (vplbnep.getX()), (int) (vplbnep.getY()));
			}
		});
		
		// Draw boulders
		boulders.forEach((ticket, boulder) -> {
			double ulxp = (boulder.getCenterXM() - boulder.getScaledIGWM() / 2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - boulder.getCenterYM()) - boulder.getScaledIGHM() / 2.0) * this.mToPixel;
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);

			g.drawImage(boulder.getRI().getImage(), (int) vpulp.getX(), (int) vpulp.getY(), null);

			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(boulder.getCenterXM() * this.mToPixel,
					(this.lvhm - (boulder.getCenterYM())) * this.mToPixel);
			g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);

			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(boulder.getCenterXM() * this.mToPixel + 5,
					(this.lvhm - (boulder.getCenterYM())) * this.mToPixel + 10);
			g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
			
		});
		
		// Draw pegs.
		pegs.forEach((ticket, peg) -> {
			double ulxp = (peg.getCenterXM() - peg.getScaledIGWM() / 2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - peg.getCenterYM()) - peg.getScaledIGHM() / 2.0) * this.mToPixel;
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);

			g.drawImage(peg.getRI().getImage(), (int) vpulp.getX(), (int) vpulp.getY(), null);

			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(peg.getCenterXM() * this.mToPixel,
					(this.lvhm - (peg.getCenterYM())) * this.mToPixel);
			g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);

			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(peg.getCenterXM() * this.mToPixel + 5,
					(this.lvhm - (peg.getCenterYM())) * this.mToPixel + 10);
			g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
		});

		// Draw vines
		vines.forEach((ticket, vine) -> {
			double ulxp = (vine.getCenterXM() - vine.getInGameWidth() / 2.0) * this.mToPixel;
			double ulyp = ((this.lvhm - vine.getCenterYM())) * this.mToPixel;
			Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);

			g.drawImage(vine.getRI().getImage(), (int) vpulp.getX(), (int) vpulp.getY(), null);

			// Draw the label on top of it.
			g.setColor(Color.MAGENTA);
			Point2D.Double vplp = getViewportCoordinates(vine.getCenterXM() * this.mToPixel,
					(this.lvhm - (vine.getCenterYM() - vine.getInGameHeight() / 2)) * this.mToPixel);
			g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);

			// Label point
			g.setColor(Color.BLACK);
			Point2D.Double vplbp = getViewportCoordinates(vine.getCenterXM() * this.mToPixel + 5,
					(this.lvhm - (vine.getCenterYM() - vine.getInGameHeight() / 2)) * this.mToPixel + 10);
			g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
		});

		// Draw player characters
		characters.forEach((name, player) -> {
			if (player.getPropertyBook().getBoolList().get("Present").booleanValue()) {
				double ulxp = (player.getCenterXM() - player.getInGameWidth() / 2.0) * this.mToPixel;
				double ulyp = ((this.lvhm - (player.getCenterYM())) - player.getInGameHeight() / 2.0) * this.mToPixel;
				Point2D.Double vpcp = getViewportCoordinates(ulxp, ulyp);
				g.drawImage(player.getRI().getImage(), (int) vpcp.getX(), (int) vpcp.getY(), null);
			}
		});

		// Draw EOL
		g.setColor(Color.GREEN);
		Point2D.Double vpeol = getViewportCoordinates(this.eol.getX() * this.mToPixel,
				(this.lvhm - (this.eol.getY())) * this.mToPixel);
		g.fillOval((int) (vpeol.getX()), (int) (vpeol.getY()), 15, 15);

		g.setColor(Color.BLACK);
		Point2D.Double vplbeol = getViewportCoordinates(this.eol.getX() * this.mToPixel + 5,
				(this.lvhm - (this.eol.getY())) * this.mToPixel + 10);
		g.drawString("EOL", (int) (vplbeol.getX()), (int) (vplbeol.getY()));
		
		// Draw respawn points		
		respawnPoints.forEach((point) -> {
			g.setColor(Color.BLUE);
			Point2D.Double vprp = getViewportCoordinates(point.getX() * this.mToPixel,
					(this.lvhm - point.getY()) * this.mToPixel);
			g.fillOval((int) (vprp.getX()), (int) (vprp.getY()), 15, 15);

			// Label point
			g.setColor(Color.WHITE);
			Point2D.Double vprplb = getViewportCoordinates(point.getX() * this.mToPixel,
					(this.lvhm - point.getY()) * this.mToPixel + 12);
			g.drawString("RP", (int) (vprplb.getX()), (int) (vprplb.getY()));
		});
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
			this.plats.get(this.requestNum).setEndpoint(new Point2D.Double(xm, ym));
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
		
		// Get the collision, etc property book.
		Gson gson = new Gson();
		PropertyBook collisionBook;
		String path = imageName.substring(9, plat.getPath().length() - 4);
		try {			
			collisionBook = gson.fromJson(new FileReader(COL_PATH + path + ".json"), PropertyBook.class);			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + COL_PATH + path + ".json");
			// e.printStackTrace();
			// TODO: Make a default set of values?
			return;
		}
		
		// Set the default property book.
		plat.setDefaultPropertyBook(collisionBook);
		
		// Make an EditWindow.
		makePlatEditWindow(this.ticketer, plat, book);
				
		// Add it the known list of platforms.
		this.plats.put(this.ticketer, plat);
		
		// Increase the ticket count.
		this.ticketer++;
	}
	
	public void makePlatEditWindow(int ticket, Platform plat, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Platform");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				plat.updateProperties(window.getPropertyBook());
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = book.getDoubList().get("Scale");
				
				plat.setScale(scale);					
					
				// Scale the image now.
				plat.setRI(resize(plat.getBI(), plat.getScaledIGWM(), plat.getScaledIGHM()));
			}			
		});
		
		// Set up platform properties.
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
		
		window.makeStringProperty("Image path", plat.getPath());
		
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
	
	public void makeVineEditWindow(int ticket, Vine vine, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Vine");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vine.updateProperties(window.getPropertyBook());
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = book.getDoubList().get("Scale");
				
				vine.setScale(scale);					
					
				// Scale the image now.
				vine.setRI(resize(vine.getBI(), vine.getScaledIGWM(), vine.getScaledIGHM()));
			}			
		});
		
		// Set up vine properties.
		window.makeButton("New center", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.EDIT_OLD_VINE;
				requestNum = ticket;	
			}		
		});
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeStringProperty("Image path", vine.getPath());
		
		window.makeDoubleProperty("Arc Length (deg)", (book == null) 
				? 180.0
				: ((book.getDoubList().get("Arc Length (deg)") == null 
					? 180.0 
					: book.getDoubList().get("Arc Length (deg)")))
		);
		
		window.makeDoubleProperty("Velocity", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Velocity") == null 
					? 1.0  
					: book.getDoubList().get("Velocity")))
		);
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
	
	public void makeNPCEditWindow(int ticket, NPC npc, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "NPC");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				npc.updateProperties(window.getPropertyBook());	
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = book.getDoubList().get("Scale");
				
				npc.setScale(scale);					
					
				// Scale the image now.
				npc.setRI(resize(npc.getBI(), npc.getScaledIGWM(), npc.getScaledIGHM()));
			}			
		});
		
		// Set up NPC properties.
		window.makeButton("New center", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.EDIT_OLD_VINE;
				requestNum = ticket;	
			}		
		});
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeStringProperty("Image path", npc.getPath());		
		
		window.makeDoubleProperty("Velocity", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Velocity") == null 
					? 1.0  
					: book.getDoubList().get("Velocity")))
		);
		
		window.makeDoubleProperty("Range (m)", (book == null) 
				? 5.0 
				: ((book.getDoubList().get("Velocity") == null 
					? 5.0  
					: book.getDoubList().get("Velocity")))
		);
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
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = book.getDoubList().get("Scale");
				
				boulder.setScale(scale);
					
				// TODO: scaledRadius is for boulders; need to add scaledRadius in the makeBoulder() call.			
					
				// Scale the image now.
				boulder.setRI(resize(boulder.getBI(), boulder.getScaledIGWM(), boulder.getScaledIGHM()));
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
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeStringProperty("Image path", boulder.getPath());
		
		window.makeDoubleProperty("Mass", (book == null) 
				? 1000.0 
				: ((book.getDoubList().get("Mass") == null 
					? 1000.0 
					: book.getDoubList().get("Mass")))
		);
		
		window.makeDoubleProperty("Radius", (book == null) 
				? 5.0
				: ((book.getDoubList().get("Radius") == null 
					? 5.0
					: book.getDoubList().get("Radius")))
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
	
	public void makePegEditWindow(int ticket, Peg peg, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Peg");		
		
		// Set up the the submit listener.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				peg.updateProperties(window.getPropertyBook());	
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = book.getDoubList().get("Scale");
				
				peg.setScale(scale);					
					
				// Scale the image now.
				peg.setRI(resize(peg.getBI(), peg.getScaledIGWM(), peg.getScaledIGHM()));
			}			
		});
		
		// Set up peg properties.
		window.makeButton("New center", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request = Request.EDIT_OLD_PEG;
				requestNum = ticket;				
			}		
		});
		
		window.makeDoubleProperty("Scale", (book == null) 
				? 1.0 
				: ((book.getDoubList().get("Scale") == null 
					? 1.0  
					: book.getDoubList().get("Scale")))
		);
		
		window.makeStringProperty("Image path", peg.getPath());

		window.makeIntProperty("Boulder ID", (book == null) 
				? 0
				: ((book.getIntList().get("Boulder ID") == null 
					? 0
					: book.getIntList().get("Boulder ID")))
		);
		
		window.makeDoubleProperty("Rotation (rad)", (book == null) 
				? 0.0
				: ((book.getDoubList().get("Rotation (rad)") == null 
					? 0.0  
					: book.getDoubList().get("Rotation (rad)")))
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
		this.vines.forEach((ticket, vine) -> vine
				.setRI(resize(vine.getBI(), vine.getScaledIGWM(), vine.getScaledIGHM())));
		this.boulders.forEach((ticket, boulder) -> boulder
				.setRI(resize(boulder.getBI(), boulder.getScaledIGWM(), boulder.getScaledIGHM())));
		this.npcs.forEach((name, enemy) -> enemy
				.setRI(resize(enemy.getBI(), enemy.getScaledIGWM(), enemy.getScaledIGHM())));
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
