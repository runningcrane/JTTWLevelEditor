package server;

import static utils.Constants.ASSETS_PATH;
import static utils.Constants.COL_PATH;
import static utils.Constants.LEVELS_PATH;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.EditWindow;
import interactable.AInteractable;
import interactable.Boulder;
import interactable.NPC;
import interactable.Peg;
import interactable.Platform;
import interactable.Player;
import interactable.PropertyBook;
import interactable.TextTip;
import interactable.Trap;
import interactable.Vine;
import interactable.Zone;
import noninteractable.Background;
import noninteractable.ANonInteractable;
import utils.AnnotationExclusionStrategy;
import utils.annotations.Exclude;

public class LevelManager {
	// Unused, but printed to JSON and used in the game.
	@SuppressWarnings("unused")
	private final int VERSION = 2;
	
	/**
	 * Name of the currently-loaded level file (something.json).
	 */
	private String levelFileName;
	
	/**
	 * Name of the currently-loaded level. (Dawn of Something).
	 * Can be more elaborate and contain spaces (which file names shouldn't).
	 */
	private String levelName;
	
	/**
	 * The number of this level.
	 * Used to sort the levels by progression in game.
	 */
	private int levelNumber;
	
	/**
	 * The text to display after this level is finished.
	 */
	private String endQuote;
	
	/**
	 * Name of the level after this level.
	 */
	private String nextLevelName;
	
	/**
	 * Meter-to-pixel ratio. For example, 80 means 80 pixels : 1 m
	 */
	private double mToPixel;

	/**
	 * Background object.
	 */
	private Background bg;
	
	/**
	 * EOL location.
	 */
	private Point2D.Double eolPoint;
	
	/**
	 * EOL direction: numbered by quadrants:   2 | 1
	 * Level doesn't end until players go      -----
	 * in that direction.                      3 | 4
	 */
	private int eolQuadrant; 
	
	/**
	 * Level width (meters).
	 */
	private double levelWidthM;

	/**
	 * Level height (meters).
	 */
	private double levelHeightM;
	
	private Zone viewportZone;
	
	/**
	 * List of respawn points for the level.
	 */
	private ArrayList<Point2D.Double> respawnPoints = new ArrayList<Point2D.Double>(); 

	/**
	 * Ticket items, all stored in separate maps for easy access.
	 */
	private Map<Integer, Request> groupSelect = new HashMap<>();
	private Map<Integer, Platform> plats = new HashMap<>();
	private Map<String, Player> characters = new HashMap<>();
	private Map<Integer, Vine> vines = new HashMap<>();
	private Map<Integer, Boulder> boulders = new HashMap<>();
	private Map<Integer, Peg> pegs = new HashMap<>();
	private Map<Integer, NPC> npcs = new HashMap<>();
	private Map<Integer, TextTip> textTips =  new HashMap<>();
    private Map<Integer, Zone> attackZones = new HashMap<>();
	private Map<Integer, Trap> traps = new HashMap<>();
	private Map<Integer, Zone> quicksand = new HashMap<>();
    
	public enum Request {
		NONE, 
		MAKE_PLATFORM, MAKE_VINE, MAKE_BOULDER, MAKE_NPC, MAKE_PEG, MAKE_TIP, 
		    MAKE_TRAP, MAKE_ATTACK_ZONE, MAKE_QUICKSAND,
		EDIT_OLD_PLAT, EDIT_OLD_VINE, EDIT_OLD_BOULDER, EDIT_OLD_PEG, 
		    EDIT_OLD_TIP, EDIT_OLD_TRAP, EDIT_OLD_ATTACK_ZONE, EDIT_OLD_NPC, EDIT_GROUP,
		EDIT_MONK, EDIT_MONKEY, EDIT_PIG, EDIT_SANDY, EDIT_OLD_QUICKSAND,
		SET_PLAT_ENDPOINT, MARK_EOL, MARK_EOL_QUADRANT, MARK_RP,
			REMOVE_RP
	}
	
	//////// Members that aren't serialized into the level JSON file. ////////////
	
	/**
	 * Viewport offset (px).
	 */
	@Exclude
	private Point2D.Double vpOffset;
	
	/**
	 * Viewport width (meters).
	 */
	@Exclude
	private double vpwm;

	/**
	 * Viewport height (meters).
	 */
	@Exclude
	private double vphm;
	
	/**
	 * Foreground array.
	 */
	@Exclude
	private ArrayList<ANonInteractable> fg = new ArrayList<>();
	
	@Exclude
	private Request request;
	
	@Exclude
	private String requestPath;
	
	@Exclude
    private EditWindow callingWindow;
	
	/**
	 * Ticket number of the requesting object.
	 */
	@Exclude
	private int requestNum;		

	/**
	 * Gives out ticket values.
	 */
	@Exclude
	private int ticketer;
	
	/**
	 * Adapter from the LevelManager to a LayerWIndow.
	 */
	@Exclude
	private ILevelToLayerAdapter ltlAdapter;
	
	/**
	 * Adapter from the LevelManager to the OuputWindow.
	 */
	@Exclude
	private ILevelToOutputAdapter ltoAdapter;
	
	/**
	 * Adapter to communicate with the controls panel.
	 */
	@Exclude
	private ILevelToControlAdapter ltcAdapter;
	
	/**
	 * Time to update the level view.
	 */
	@Exclude
	private Timer timer;

	/**
	 * How often the timer should be called.
	 */
	@Exclude
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
		this.levelFileName = "default";
		this.levelName = "Communism will rise again";
		this.nextLevelName  = "";
		this.levelNumber = 0;
		this.viewportZone = new Zone(ticketer, null);
		this.viewportZone.setLimits(0.0, 0.0, 0.0, 0.0);
		
		ticketer++;
		
		// No real reason to put it to 5,8 other than just to initialize it.
		this.eolPoint = new Point2D.Double(5, 8);
		this.eolQuadrant = 1; // top right, most levels will keep this.
		
		this.mToPixel = 50;
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
		this.levelWidthM = 20;
		this.levelHeightM = 15;

		// Set up the player characters.
		String[] players = {"Monkey", "Monk", "Piggy", "Sandy"};
		
		for (int i = 0; i < 4; i++) {
			String path = players[i] + ".png";
			Player player = new Player(path);
			BufferedImage playerBI;
			try {
				playerBI = ImageIO.read(new File(ASSETS_PATH + path));
			} catch (IOException e) {
				System.err.println("File not found: " + ASSETS_PATH + path);
				e.printStackTrace();
				return;
			}
			player.getDefaultPropertyBook().getDoubList().put("widthm", 0.7);
			player.getDefaultPropertyBook().getDoubList().put("heightm", 1.7);
			player.setBI(playerBI);
			player.setRI(resize(playerBI, 0.7, 1.7));
			player.getDefaultPropertyBook().getDoubList().put("widthm", 0.7);
			player.getDefaultPropertyBook().getDoubList().put("heightm",  1.7);
			player.setCenter(0, 0);
			player.getPropertyBook().getBoolList().put("Present", false);
			characters.put(players[i], player);
		}		
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
			g.drawImage(bg.getRescaled(), (int) vpbg.getX(), (int) vpbg.getY(), null);
		}

		// Draw platforms
		plats.entrySet().stream().sorted(new Comparator<Map.Entry<Integer, Platform>>() {
			@Override
			public int compare(Entry<Integer, Platform> o1, Entry<Integer, Platform> o2) {
				Platform p1 = o1.getValue();
				Platform p2 = o2.getValue();
			    return p1.getZorder() - p2.getZorder(); 
			}
		}).forEach((e) -> {
			int number = e.getKey();
			Platform plat = e.getValue();
			// Don't draw until the plat's image has been made.
			if (plat.getRI() != null) {				
				/*
				 * Unfortunately drawImage draws on the top left, not center, so
				 * adjustments are made. Also unfortunately, cocos uses a different
				 * orientation system than swing. Namely, the y is flipped in
				 * direction. Since CenterXm/CenterYm are in swing-orientation,
				 * reverse the ys to get them in cocos.
				 */
				double ulxp = (plat.getCenterXM() - plat.getScaledIGWM() / 2.0) * this.mToPixel;
				double ulyp = ((this.levelHeightM - plat.getCenterYM()) - plat.getScaledIGHM() / 2.0) * this.mToPixel;
				Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);
		
				g.drawImage(plat.getRI(), (int) vpulp.getX(), (int) vpulp.getY(), null);
				
				// If part of the group selection, draw a notifying 1m x 1m red box.
				Point2D.Double vplp = getViewportCoordinates(plat.getCenterXM() * this.mToPixel,
						(this.levelHeightM - (plat.getCenterYM())) * this.mToPixel);
				if (this.groupSelect.containsKey(number)){
					g.setColor(new Color(255, 40, 40, 200));				
					g.fillRect((int) (vplp.getX() - this.mToPixel/2), (int) (vplp.getY() - this.mToPixel/2), 
							(int)this.mToPixel, (int)this.mToPixel);
				}
		
				// Draw the label on top of it. In the center, maybe?
				g.setColor(Color.MAGENTA);				
				g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);
		
				// Label point
				g.setColor(Color.BLACK);
				Point2D.Double vplbp = getViewportCoordinates(plat.getCenterXM() * this.mToPixel + 5,
						(this.levelHeightM - (plat.getCenterYM())) * this.mToPixel + 10);
				g.drawString(Integer.toString(number), (int) (vplbp.getX()), (int) (vplbp.getY()));
		
				// If moveable, show its endpoint and a line to its endpoint.
				Platform.Type type = Platform.Type.valueOf(plat.getPropertyBook().getStringList().get(Platform.Type.class.getName()));
				if (type == Platform.Type.MOVING || type == Platform.Type.HITTING) {
					Point2D.Double endpoint = plat.getEndpoint();
					Point2D.Double vplbep = getViewportCoordinates(endpoint.getX() * this.mToPixel + 5,
							(this.levelHeightM - (endpoint.getY())) * this.mToPixel + 10);
		
					// Draw the line first.
					g.setColor(Color.MAGENTA);
					g.drawLine((int) vplp.getX() + 5, (int) vplp.getY() + 10, (int) vplbep.getX(), (int) vplbep.getY());
		
					// Then draw the endpoint.
					g.setColor(Color.RED);
					g.fillOval((int) (vplbep.getX()), (int) (vplbep.getY()), 15, 15);
		
					// Then label it.
					g.setColor(Color.BLACK);
					Point2D.Double vplbnep = getViewportCoordinates(endpoint.getX() * this.mToPixel + 5,
							(this.levelHeightM - (endpoint.getY())) * this.mToPixel + 10);
					g.drawString(Integer.toString(number) + "EP", (int) (vplbnep.getX()), (int) (vplbnep.getY()));
				}
			}
		});
		
		// Draw boulders
		boulders.forEach((ticket, boulder) -> {
			
			// Don't draw until the image has been made.
			if (boulder.getRI() != null) {
				double ulxp = (boulder.getCenterXM() - boulder.getScaledIGWM() / 2.0) * this.mToPixel;
				double ulyp = ((this.levelHeightM - boulder.getCenterYM()) - boulder.getScaledIGHM() / 2.0) * this.mToPixel;
				Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);
	
				g.drawImage(boulder.getRI(), (int) vpulp.getX(), (int) vpulp.getY(), null);
	
				// If part of the group selection, draw a notifying 1m x 1m red box.
				Point2D.Double vplp = getViewportCoordinates(boulder.getCenterXM() * this.mToPixel,
						(this.levelHeightM - (boulder.getCenterYM())) * this.mToPixel);
				if (this.groupSelect.containsKey(ticket)){
					g.setColor(new Color(255, 40, 40, 200));				
					g.fillRect((int) (vplp.getX() - this.mToPixel/2), (int) (vplp.getY() - this.mToPixel/2), 
							(int)this.mToPixel, (int)this.mToPixel);
				}
				
				// Draw the label on top of it. In the center, maybe?
				g.setColor(Color.MAGENTA);
				g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);
	
				// Label point
				g.setColor(Color.BLACK);
				Point2D.Double vplbp = getViewportCoordinates(boulder.getCenterXM() * this.mToPixel + 5,
						(this.levelHeightM - (boulder.getCenterYM())) * this.mToPixel + 10);
				g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
			}
		});
		
		// Draw pegs.
		pegs.forEach((ticket, peg) -> {
			// Don't draw until the image has been made.
			if (peg.getRI() != null) {
				double ulxp = (peg.getCenterXM() - peg.getScaledIGWM() / 2.0) * this.mToPixel;
				double ulyp = ((this.levelHeightM - peg.getCenterYM()) - peg.getScaledIGHM() / 2.0) * this.mToPixel;
				Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);
	
				double rot =  Math.toRadians(peg.getPropertyBook().getDoubList().get("Rotation (deg)"));
				AffineTransform tx = AffineTransform.getRotateInstance(-rot, peg.getRI().getWidth()/2, peg.getRI().getHeight()/2);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				g.drawImage(op.filter(peg.getRI(), null), (int) vpulp.getX(), (int) vpulp.getY(), null);
	
				// If part of the group selection, draw a notifying 1m x 1m red box.
				Point2D.Double vplp = getViewportCoordinates(peg.getCenterXM() * this.mToPixel,
						(this.levelHeightM - (peg.getCenterYM())) * this.mToPixel);
				if (this.groupSelect.containsKey(ticket)){
					g.setColor(new Color(255, 40, 40, 200));				
					g.fillRect((int) (vplp.getX() - this.mToPixel/2), (int) (vplp.getY() - this.mToPixel/2), 
							(int)this.mToPixel, (int)this.mToPixel);
				}
								
				// Draw the label on top of it. In the center, maybe?
				g.setColor(Color.MAGENTA);
				g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);
	
				// Label point
				g.setColor(Color.BLACK);
				Point2D.Double vplbp = getViewportCoordinates(peg.getCenterXM() * this.mToPixel + 5,
						(this.levelHeightM - (peg.getCenterYM())) * this.mToPixel + 10);
				g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
			}
		});

		// Draw vines
		vines.forEach((ticket, vine) -> {
			// Don't draw until the image has been made.
			if (vine.getRI() != null) {				
				double ulxp = (vine.getCenterXM() - vine.getInGameWidth() / 2.0) * this.mToPixel;
				double ulyp = ((this.levelHeightM - vine.getCenterYM())) * this.mToPixel;
				Point2D.Double vpulp = getViewportCoordinates(ulxp, ulyp);			
				
				g.drawImage(vine.getRI(), (int) vpulp.getX(), (int) vpulp.getY(), null);
	
				// If part of the group selection, draw a notifying 1m x 1m red box.
				Point2D.Double vplp = getViewportCoordinates(vine.getCenterXM() * this.mToPixel,
						(this.levelHeightM - (vine.getCenterYM() - vine.getInGameHeight() / 2)) * this.mToPixel);
				if (this.groupSelect.containsKey(ticket)){
					g.setColor(new Color(255, 40, 40, 200));				
					g.fillRect((int) (vplp.getX() - this.mToPixel/2), (int) (vplp.getY() - this.mToPixel/2), 
							(int)this.mToPixel, (int)this.mToPixel);
				}
										
				// Draw the label on top of it.
				g.setColor(Color.MAGENTA);
				g.fillOval((int) (vplp.getX()), (int) (vplp.getY()), 15, 15);
	
				// Label point
				g.setColor(Color.BLACK);
				Point2D.Double vplbp = getViewportCoordinates(vine.getCenterXM() * this.mToPixel + 5,
						(this.levelHeightM - (vine.getCenterYM() - vine.getInGameHeight() / 2)) * this.mToPixel + 10);
				g.drawString(Integer.toString(ticket), (int) (vplbp.getX()), (int) (vplbp.getY()));
			}
		});

		// Draw player characters
		characters.forEach((name, player) -> {
			if (player.getPropertyBook().getBoolList().get("Present").booleanValue()) {
				double ulxp = (player.getCenterXM() - player.getInGameWidth() / 2.0) * this.mToPixel;
				double ulyp = ((this.levelHeightM - (player.getCenterYM())) - player.getInGameHeight() / 2.0) * this.mToPixel;
				Point2D.Double vpcp = getViewportCoordinates(ulxp, ulyp);
				g.drawImage(player.getRI(), (int) vpcp.getX(), (int) vpcp.getY(), null);
			}
		});
		
		traps.forEach((num, trap) -> {
			Point2D.Double c = getViewportCoordinates(((trap.getCenterXM() - trap.getScaledIGWM() / 2.0) * this.mToPixel), (((this.levelHeightM - trap.getCenterYM()) - trap.getScaledIGHM() / 2.0) * this.mToPixel));
			g.drawImage(trap.getRI(), (int)c.x,  (int)c.y, null);

			Point2D.Double vplbp = getViewportCoordinates(trap.getCenterXM() * this.mToPixel + 5,
					(this.levelHeightM - (trap.getCenterYM() - trap.getInGameHeight() / 2)) * this.mToPixel + 10);
			
			// If part of the group selection, draw a notifying 1m x 1m red box.
			if (this.groupSelect.containsKey(num)){
				g.setColor(new Color(255, 40, 40, 200));				
				g.fillRect((int) (vplbp.getX() - this.mToPixel/2), (int) (vplbp.getY() - this.mToPixel/2), 
						(int)this.mToPixel, (int)this.mToPixel);
			}					
			
			// Label point.
			g.setColor(Color.BLACK);									
			g.drawString(Integer.toString(num), (int) (vplbp.getX()), (int) (vplbp.getY()));
		});
		
		attackZones.forEach((num, zone) -> {
			double ulxp = (zone.getPropertyBook().getDoubList().get("xmin")) * this.mToPixel;
			double ulyp = (this.levelHeightM - zone.getPropertyBook().getDoubList().get("ymin")) * this.mToPixel;
			Point2D.Double ul = getViewportCoordinates(ulxp, ulyp);
			double width = zone.getWidth() * this.mToPixel;
			double height = zone.getHeight() * this.mToPixel;
			g.setColor(Color.RED);
			g.drawRect((int) ul.x, (int)ul.y - (int) height, (int)width, (int)height);
			
			Point2D.Double c = getViewportCoordinates((zone.getCenterXM() * this.mToPixel), ((this.levelHeightM - zone.getCenterYM()) * this.mToPixel));
			g.drawImage(zone.getRI(), (int)(c.x - zone.getRI().getWidth() / 2.0),  (int)(c.y - zone.getRI().getHeight() / 2.0), null);

			// If part of the group selection, draw a notifying 1m x 1m red box.
			Point2D.Double vplbp = getViewportCoordinates(zone.getCenterXM() * this.mToPixel + 5,
					(this.levelHeightM - (zone.getCenterYM() - zone.getInGameHeight() / 2)) * this.mToPixel + 10);
			if (this.groupSelect.containsKey(num)){
				g.setColor(new Color(255, 40, 40, 200));				
				g.fillRect((int) (vplbp.getX() - this.mToPixel/2), (int) (vplbp.getY() - this.mToPixel/2), 
						(int)this.mToPixel, (int)this.mToPixel);
			}					
		});
		
		quicksand.forEach((num, zone) -> {
			Point2D.Double c = getViewportCoordinates((zone.getCenterXM() * this.mToPixel), ((this.levelHeightM - zone.getCenterYM()) * this.mToPixel));
			g.drawImage(zone.getRI(), (int)(c.x - zone.getRI().getWidth() / 2.0),  (int)(c.y - zone.getRI().getHeight() / 2.0), null);

			// Draw the label on top of it. In the center, maybe?
			g.setColor(Color.MAGENTA);				
			g.fillOval((int) (c.x - 7), (int) (c.y + 7), 15, 15);
	
			// If part of the group selection, draw a notifying 1m x 1m red box.
			Point2D.Double vplbp = getViewportCoordinates(zone.getCenterXM() * this.mToPixel + 5,
					(this.levelHeightM - (zone.getCenterYM() - zone.getInGameHeight() / 2)) * this.mToPixel + 10);
			if (this.groupSelect.containsKey(num)){
				g.setColor(new Color(255, 40, 40, 200));				
				g.fillRect((int) (vplbp.getX() - this.mToPixel/2), (int) (vplbp.getY() - this.mToPixel/2), 
						(int)this.mToPixel, (int)this.mToPixel);
			}					
		});
		
		// Draw viewport Zone.
		double ulxp = (viewportZone.getPropertyBook().getDoubList().get("xmin")) * this.mToPixel;
		double ulyp = (this.levelHeightM - viewportZone.getPropertyBook().getDoubList().get("ymin")) * this.mToPixel;
		Point2D.Double ul = getViewportCoordinates(ulxp, ulyp);
		double width = viewportZone.getWidth() * this.mToPixel;
		double height = viewportZone.getHeight() * this.mToPixel;
		g.setColor(Color.BLACK);
		g.drawRect((int) ul.x, (int)ul.y - (int) height, (int)width, (int)height);
		
		// Draw EOL
		g.setColor(Color.GREEN);
		Point2D.Double vpeol = getViewportCoordinates(this.eolPoint.getX() * this.mToPixel,
				(this.levelHeightM - (this.eolPoint.getY())) * this.mToPixel);
		g.fillOval((int) (vpeol.getX()), (int) (vpeol.getY()), 15, 15);

		g.setColor(Color.BLACK);
		Point2D.Double vplbeol = getViewportCoordinates(this.eolPoint.getX() * this.mToPixel + 5,
				(this.levelHeightM - (this.eolPoint.getY())) * this.mToPixel + 10);
		g.drawString("EOL", (int) (vplbeol.getX()), (int) (vplbeol.getY()));
		
		// Draw EOL arrow.
		int x =  (int) (vplbeol.getX()), y = (int) (vplbeol.getY()), x2, y2;
		switch (eolQuadrant) {
		case 1:
			x2 = x + 20;
			y2 = y - 20;
			break;
		
		case 2:
			x2 = x - 20;
			y2 = y - 20;
			break;
			
		case 3:
			x2 = x - 20;
			y2 = y + 20;
			break;
			
		case 4: 
			x2 = x + 20;
			y2 = y + 20;
			break;
		default:
			x2 = x + 20;
			y2 = y;
			break;
		}
		g.setColor(Color.GREEN);
		((Graphics2D) g).setStroke(new BasicStroke(4));
		g.drawLine(x, y, x2, y2);
		
		// Draw respawn points		
		respawnPoints.forEach((point) -> {
			g.setColor(Color.BLUE);
			Point2D.Double vprp = getViewportCoordinates(point.getX() * this.mToPixel,
					(this.levelHeightM - point.getY()) * this.mToPixel);
			g.fillOval((int) (vprp.getX()), (int) (vprp.getY()), 15, 15);

			// Label point
			g.setColor(Color.WHITE);
			Point2D.Double vprplb = getViewportCoordinates(point.getX() * this.mToPixel,
					(this.levelHeightM - point.getY()) * this.mToPixel + 12);
			g.drawString("RP", (int) (vprplb.getX()), (int) (vprplb.getY()));
		});
		
		Font old = g.getFont(); 
		textTips.forEach((ticket, textTip) -> {
				// Don't draw until the font size has been decided.
			if (textTip.getPropertyBook().getIntList().containsKey("fontSize")) {
				Point2D.Double point = getViewportCoordinates(textTip.getCenterXM() * this.mToPixel,
						(this.levelHeightM - textTip.getCenterYM()) * this.mToPixel);
				
				g.setColor(Color.BLACK);
				Font f = new Font(Font.DIALOG, Font.PLAIN, 15);
				int textWidth = g.getFontMetrics(f).stringWidth(textTip.getString());
				int textHeight = g.getFontMetrics(f).getHeight();
				g.setFont(f);
				g.drawString(textTip.getString(), (int)point.x - textWidth / 2, (int)point.y - textHeight / 2);
			}
		});
		g.setFont(old); 
	}
	
	/**
	 * Request a new entity to be made.
	 * @param path image path name
	 * @param requestType type of request
	 */
	public void setRequest(String path, Request requestType) {
		this.requestPath = path;			
		this.request = requestType;
	}
	
	/**
	 * Receive coordinates from the OuputWindow. Do various actions depending on the request type.
	 * @param xp
	 * @param yp
	 */
	public void receiveCoordinates(double xp, double yp) {
		double xm = (xp - this.vpOffset.getX()) / this.mToPixel;
		double ym = this.levelHeightM - (yp - this.vpOffset.getY()) / this.mToPixel;
		
		System.out.println("Request type: " + request + ".Received swing coordinates " + xp + ", " + yp + ";\ntranslated to " +
				xm + ", " + ym + " cocos coordinates");
		
		switch (request) {
		case MAKE_PLATFORM: {
			makeInteractable(this.requestPath, null, xm, ym, "Platform");
			break;
		}
		case MAKE_VINE: {
			makeInteractable(this.requestPath, null, xm, ym, "Vine");
			break;
		}
		case MAKE_BOULDER: {
			makeBoulder(this.requestPath, null, xm, ym, 0);
			break;
		}
		case MAKE_NPC: {
			makeInteractable(this.requestPath, null, xm, ym, "NPC");
			break;
		}
		case MAKE_PEG: {
			makeInteractable(this.requestPath, null, xm, ym, "Peg");
			break;
		}
		case MAKE_TRAP: {
			makeInteractable(this.requestPath, null, xm, ym, "Trap");
			break;
		}
		case MAKE_TIP: {
			makeInteractable(this.requestPath, null, xm, ym, "TextTip");
			break;
		}
		case MAKE_ATTACK_ZONE: {
			makeInteractable(this.requestPath, null, xm, ym, "AttackZone");
			break;
		}
		case MAKE_QUICKSAND: {
			makeInteractable(this.requestPath, null, xm, ym, "Quicksand");
			break;
		}
		case EDIT_GROUP: {
			// Moves relative to the lowest ticket number.
			int[] lowestTicket = {Integer.MAX_VALUE};
			double[] offset = {0,0};
			
			// First, get the offset of the lowest ticket number.
			this.groupSelect.forEach((ticketNum, requestType) -> {
				if (ticketNum < lowestTicket[0]) {
					lowestTicket[0] = ticketNum;
					AInteractable target;
					switch (requestType) {
					case EDIT_OLD_PLAT: {
						target = this.plats.get(ticketNum);
						break;
					}
					case EDIT_OLD_VINE: {
						target = this.vines.get(ticketNum);
						break;
					}
					case EDIT_OLD_BOULDER: {
						target = this.boulders.get(ticketNum);
						break;
					}
					case EDIT_OLD_PEG: {
						target = this.pegs.get(ticketNum);
						break;
					}
					case EDIT_OLD_TIP: {
						target = this.textTips.get(ticketNum);
						break;
					}
					case EDIT_OLD_TRAP: {
						target = this.traps.get(ticketNum);
						break;
					}
					case EDIT_OLD_ATTACK_ZONE: {
						target = this.attackZones.get(ticketNum);
						break;
					}
					case EDIT_OLD_QUICKSAND: {
						target = this.quicksand.get(ticketNum);
						break;
					}
					case EDIT_OLD_NPC: {
						target = this.npcs.get(ticketNum);
						break;
					}
					default: {
						return;
					}
					}
					if (target != null) {
						offset[0] = xm - target.getCenterXM();
						offset[1] = ym - target.getCenterYM();
					}
				}
			});
			
			// Now that we have the offset, go offset each.
			this.groupSelect.forEach((ticketNum, requestType) -> {
				this.request = requestType;
				this.requestNum = ticketNum;
				
				AInteractable target;
				// In order to offset each, we need to (unfortunately) get their personal center values.
				switch (requestType) {
				case EDIT_OLD_PLAT: {
					target = this.plats.get(requestNum);
					break;
				}
				case EDIT_OLD_VINE: {
					target = this.vines.get(requestNum);
					break;
				}
				case EDIT_OLD_BOULDER: {
					target = this.boulders.get(requestNum);
					break;
				}
				case EDIT_OLD_PEG: {
					target = this.pegs.get(requestNum);
					break;
				}
				case EDIT_OLD_TIP: {
					target = this.textTips.get(requestNum);
					break;
				}
				case EDIT_OLD_TRAP: {
                    target = this.traps.get(requestNum);
					break;
				}
				case EDIT_OLD_ATTACK_ZONE: {
					target = this.attackZones.get(requestNum);
					break;
				}
				case EDIT_OLD_NPC: {
					target = this.npcs.get(requestNum);
					break;
				}
				case EDIT_OLD_QUICKSAND: {
					target = this.quicksand.get(ticketNum);
					break;
				}
				default: {
					return;
				}
				}
				double[] centerVal = {0, 0};
				if (target != null) {
					centerVal[0] = target.getCenterXM();
					centerVal[1] = target.getCenterYM();
				}
				
				double newXP = ((centerVal[0] + offset[0]) * this.mToPixel) + this.vpOffset.getX();				
				double newYP = (this.levelHeightM - (centerVal[1] + offset[1])) * this.mToPixel + this.vpOffset.getY();
				receiveCoordinates(newXP, newYP);
			});			
			break;
		}
		case EDIT_OLD_PLAT: {
			Platform target = this.plats.get(requestNum);
			if (target != null) {
				target.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_VINE: {
			Vine target = this.vines.get(requestNum);
			if (target != null) {
				target.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_BOULDER: {
			Boulder target = this.boulders.get(requestNum);
			if (target != null) {
				target.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_PEG: {
			Peg target = this.pegs.get(requestNum);
			if (target != null) {
				target.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_TRAP: {
			Trap t = this.traps.get(requestNum);
			if (t != null) {
				t.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_ATTACK_ZONE: {
			Zone zone = this.attackZones.get(requestNum);
			if (zone != null) {
				zone.setCenter(xm, ym);
			}
			if (callingWindow != null) {
			    callingWindow.updateProperties(zone.getPropertyBook());
			}
			break;
		}
		case EDIT_OLD_TIP :{
			TextTip t = this.textTips.get(requestNum);
			if (t != null) {
				t.setCenter(xm, ym);
			}
			break;
		}
			
		case EDIT_OLD_NPC: {
			NPC npc = this.npcs.get(requestNum);
			if (npc != null) {
				npc.setCenter(xm, ym);
			}
			break;
		}
		case EDIT_OLD_QUICKSAND: {
			Zone z = this.quicksand.get(requestNum);
			if (z != null) {
				z.setCenter(xm, ym);
			}
			break;
		}
		
		case EDIT_MONK: {
			this.characters.get("Monk").setCenter(xm, ym);
			break;
		}
		case EDIT_MONKEY: {
			this.characters.get("Monkey").setCenter(xm, ym);
			break;
		}
		case EDIT_PIG: {
			this.characters.get("Piggy").setCenter(xm, ym);
			break;
		}
		case EDIT_SANDY: {
			this.characters.get("Sandy").setCenter(xm, ym);
			break;
		}
		case SET_PLAT_ENDPOINT: {
			this.plats.get(this.requestNum).setEndpoint(new Point2D.Double(xm, ym));
			break;
		}
		case MARK_EOL: {
			this.eolPoint = new Point2D.Double(xm, ym);
			request = Request.MARK_EOL_QUADRANT;
			return;
		}
		case MARK_EOL_QUADRANT: {
			if (ym > eolPoint.y) {
				if (xm > eolPoint.x) {
					this.eolQuadrant = 1;
				} else {
					this.eolQuadrant = 2;
				}
			} else {
				if (xm < eolPoint.x) {
					this.eolQuadrant = 3;
				} else {
					this.eolQuadrant = 4;
				}
			}
			break;
		}
		case MARK_RP: {
			this.respawnPoints.add(new Point2D.Double(xm, ym));
			break;
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
			break;
		}
		case NONE: {}
		default: {}
		}
		
		this.request = Request.NONE;
		this.requestNum = 0;
		this.requestPath = "";
	}
	
	public PropertyBook getCollisionBook(String truncatedName) {	
		System.out.println("Name of object: " + truncatedName);
		Gson gson = new Gson();
		PropertyBook collisionBook;		
		try {			
			collisionBook = gson.fromJson(new FileReader(COL_PATH + truncatedName + ".json"), PropertyBook.class);			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + COL_PATH + truncatedName + ".json");
			PropertyBook defaultBook = new PropertyBook();
			defaultBook.getDoubList().put("zoomLevel", 1.0);
			defaultBook.getDoubList().put("widthm", 3.0);
			defaultBook.getDoubList().put("heightm", 3.0);
			return defaultBook;
		}
		
		return collisionBook;
	}
	
	public void setImage(AInteractable object, String path){
		BufferedImage image;
		try {
			image = ImageIO.read(new File(ASSETS_PATH + path + ".png"));
		} catch (IOException e) {
			System.err.println("Image file not found: " + ASSETS_PATH + path + ".png");
			e.printStackTrace();
			return;
		}		

		object.setBI(image);
		
		double scale = object.getPropertyBook().getDoubList().get("Scale");		
		object.setScale(scale);	
		
		System.out.println("Width: " + object.getInGameWidth() + " Height: " + object.getInGameHeight() + 
				" Scale: " + scale + " ScaledIGWM: " + object.getScaledIGWM() + " ScaledIGHM: " + object.getScaledIGHM());
		
		object.setRI(resize(image, object.getScaledIGWM(), object.getScaledIGHM()));
	}
	
	/* Returns ticket.s */
	public int makeInteractable (String imageName, PropertyBook book, double xm, double ym, String type) {
		AInteractable obj;
		switch (type) {
		case "Platform": {
			obj = new Platform(this.ticketer, imageName);
			break;
		}
		case "Vine" : {
			obj = new Vine(this.ticketer, imageName);
			break;
		}
		case "NPC" : {
			obj = new NPC(this.ticketer, imageName);
			break;
		}
		case "Boulder" : {
			System.err.println("Please call makeBoulder() instead.");
			return -1;
		}
		case "Peg" : {
			obj = new Peg(this.ticketer, imageName);
			break;
		}
		case "Trap" : {
			obj = new Trap(this.ticketer, imageName);
			break;
		}
		case "TextTip" : {
			obj = new TextTip(this.ticketer);
			break;
		}
		case "AttackZone" : {
			obj = new Zone(this.ticketer, imageName);
			break;
		}
		case "Quicksand": {
			obj = new Zone(this.ticketer, imageName);
			break;
		}
		default:
			System.err.println("Tag, " + type + ", does not match any cases");
			return -1;
		}
		
		// Set the center location.
		obj.setCenter(xm, ym);
		
		// Set the default property book if there is a valid path.
		String path = "";
		if (!obj.getPath().equals("")) {
			path = imageName.substring(10, obj.getPath().length() - 4);
			obj.setDefaultPropertyBook(getCollisionBook(path));
		}
		
		// Make an EditWindow.
		switch (type) {
		case "Platform": {
			makePlatEditWindow(this.ticketer, (Platform)obj, book);
			setImage(obj, path);
			this.plats.put(this.ticketer, (Platform)obj);
			break;
		}
		case "Vine" : {
			makeVineEditWindow(this.ticketer, (Vine)obj, book);
			setImage(obj, path);
			this.vines.put(this.ticketer, (Vine)obj);
			break;
		}
		case "NPC" : {
			makeNPCEditWindow(this.ticketer, (NPC)obj, book);
			setImage(obj, path);
			this.npcs.put(this.ticketer, (NPC)obj);
			break;
		}
		case "Boulder" : {
			makeBoulderEditWindow(this.ticketer, (Boulder)obj, book);
			setImage(obj, path);
			this.boulders.put(this.ticketer, (Boulder)obj);
			break;
		}
		case "Peg" : {
			makePegEditWindow(this.ticketer, (Peg)obj, book);
			setImage(obj, path);
			this.pegs.put(this.ticketer, (Peg)obj);
			break;
		}
		case "Trap" : {
			makeTrapEditWindow(this.ticketer, (Trap)obj, book);
			setImage(obj, path);
			this.traps.put(this.ticketer, (Trap)obj);
			break;
		}
		case "TextTip" : {
			makeTextEditWindow(this.ticketer, (TextTip)obj, book);
			this.textTips.put(this.ticketer, (TextTip)obj);
			break;
		}
		case "AttackZone" : {
			makeAttackZoneWindow(this.ticketer, (Zone)obj, book);
			setImage(obj, path);
			this.attackZones.put(this.ticketer, (Zone)obj);
			break;
		}
		case "Quicksand" : {
			makeQuicksandWindow(this.ticketer, (Zone)obj, book);
			setImage(obj, path);
			this.quicksand.put(this.ticketer, (Zone)obj);
			break;
		}
		default: 
			System.err.println("Tag, " + type + ", does not match any cases");
			break;
		}									
		
		// Increase the ticket count.
		int tick = this.ticketer++;
		return tick;
	}
	
	private void makeQuicksandWindow(int ticket, Zone zone, PropertyBook book) {
        EditWindow window = ltlAdapter.makeEditWindow(ticket, "Quicksand");
		
		// Set up the listeners.
		window.setSubmitListener((arg0) -> {
			zone.updateProperties(window.getPropertyBook());
			// Next, update the relevant parts when scaling. Use resize()!
			double scale = window.getPropertyBook().getDoubList().get("Scale");
			
			zone.setScale(scale);					
				
			// Scale the image now.
			zone.setRI(resize(zone.getBI(), zone.getScaledIGWM(), zone.getScaledIGHM()));
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_QUICKSAND;
			requestNum = ticket;
			callingWindow = window;
		});
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_QUICKSAND);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up the other properties.
		window.makeDoubleProperty("Scale", 1.0, book);
		window.makeStringProperty("soundName", "", book);
		window.makeDoubleProperty("sinkVel", 0.0,  book);
		window.makeDoubleProperty("recoveryVel", 0.0,  book);
        window.makeDoubleProperty("Z-order", 3, book);
		
		zone.updateProperties(window.getPropertyBook());
	}

	private void makeTrapEditWindow(int ticket, Trap trap, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Trap");
		
		// Set up the needed listeners.
		window.setSubmitListener((arg0) -> {
			trap.updateProperties(window.getPropertyBook());
			
			double scale = window.getPropertyBook().getDoubList().get("Scale");
			trap.setScale(scale);					
			
			// Scale the image now.
		    trap.setRI(resize(trap.getBI(), trap.getScaledIGWM(), trap.getScaledIGHM()));
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_TRAP;
			requestNum = ticket;
			callingWindow = window;
		});
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_TRAP);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Make the other properties.
		window.makeDoubleProperty("Scale",  1.0,  book);
		window.makeDoubleProperty("density", 1.0,  book);
		window.makeDoubleProperty("bounciness", 1.0, book);
		window.makeDoubleProperty("friction", 1.0, book);
		window.makeDoubleProperty("wallThickness", 0.1,  book);
		window.makeDoubleProperty("offset",  -.5,  book);
		window.makeDoubleProperty("trapWidth", 1,  book);
		window.makeDoubleProperty("trapHeight", 1,  book);
		
		trap.updateProperties(window.getPropertyBook());
	}

	private void makeAttackZoneWindow(int ticket, Zone zone, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "AttackZone");
		
		// Set up the listeners.
		window.setSubmitListener((arg0) -> {
			zone.updateProperties(window.getPropertyBook());
			// Next, update the relevant parts when scaling. Use resize()!
			double scale = window.getPropertyBook().getDoubList().get("Scale");
			
			zone.setScale(scale);					
				
			// Scale the image now.
			zone.setRI(resize(zone.getBI(), zone.getScaledIGWM(), zone.getScaledIGHM()));
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_ATTACK_ZONE;
			requestNum = ticket;
			callingWindow = window;
		});
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_ATTACK_ZONE);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up the other properties.
		window.makeDoubleProperty("Scale", 1.0, book);
		window.makeStringProperty("soundName", "", book);
		window.makeBooleanProperty("dynamic", true, book);
		window.makeStringProperty("FireType", "ABSOLUTE", book); 
		window.makeBooleanProperty("Deadly", true, book);
		window.makeDoubleProperty("xmin", 0.0, book); 
		window.makeDoubleProperty("xmax", 0.0,  book);
		window.makeDoubleProperty("ymin",  0.0,  book);
		window.makeDoubleProperty("ymax",  0.0,  book);
		window.makeDoubleProperty("xVelMin", 0.0,  book);
		window.makeDoubleProperty("xVelMax", 0.0,  book);
		window.makeDoubleProperty("yVelMin",  0.0,  book);
		window.makeDoubleProperty("yVelMax",  0.0,  book);
		window.makeDoubleProperty("xOffsetMin", 0.0, book);
		window.makeDoubleProperty("xOffsetMax", 0.0, book);
		window.makeDoubleProperty("yOffsetMin",  0.0,  book);
		window.makeDoubleProperty("yOffsetMax", 0.0,  book);
		
		zone.updateProperties(window.getPropertyBook());
	}

	public void makePlatEditWindow(int ticket, Platform plat, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Platform");		
		
		// Set up the the submit listener.
		window.setSubmitListener((arg0) -> {
				plat.updateProperties(window.getPropertyBook());
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = window.getPropertyBook().getDoubList().get("Scale");
				
				plat.setScale(scale);					
					
				// Scale the image now.
				plat.setRI(resize(plat.getBI(), plat.getScaledIGWM(), plat.getScaledIGHM()));	
		});

		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_PLAT;
			requestNum = ticket;	
	    });
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_PLAT);
					System.out.println("Added ticket " + ticket + " to group");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
			
		// Set up platform properties.
		window.makeDoubleProperty("Scale", 1.0, book);
		window.makeStringProperty("Image path", plat.getPath(), null);		
		window.makeBooleanProperty("Disappears", false, book); 		
		Platform.Type legacyDefault; 
		if (book != null && book.getBoolList().get("Moving") != null && book.getBoolList().get("Moving").booleanValue()) {
			legacyDefault = Platform.Type.MOVING;
		} else {
			legacyDefault = Platform.Type.STATIC;
		}
		window.makeEnumProperty(Platform.Type.class, legacyDefault, book);
		window.makeButton("Set Endpoint", (e) -> {
				request = Request.SET_PLAT_ENDPOINT;
				requestNum = ticket;		
		});
		
		// Make the default endpoint.
		plat.setEndpoint(new Point2D.Double(plat.getCenterXM(), plat.getCenterYM()));
		
		window.makeDoubleProperty("Velocity", 1.0, book); 	
		window.makeDoubleProperty("Quick Velocity", 0.0, book);
		window.makeDoubleProperty("Pause time", 0.0, book);
		window.makeBooleanProperty("Sinkable", false, book); 		
		window.makeDoubleProperty("Spring Constant K", 1.0, book);
		window.makeBooleanProperty("Climbable", false, book); 
		window.makeBooleanProperty("Collidable", true, book); 
		window.makeBooleanProperty("Polygon collision", true, book); 
		window.makeIntProperty("Z-order", 4, book);

		plat.updateProperties(window.getPropertyBook());
	}
	
	public void makeVineEditWindow(int ticket, Vine vine, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Vine");		
		
		// Set up the the submit listener.
		window.setSubmitListener((arg0) -> {
				vine.updateProperties(window.getPropertyBook());
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = window.getPropertyBook().getDoubList().get("Scale");
				vine.setScale(scale);					
				
				// Scale the image now.
				vine.setRI(resize(vine.getBI(), vine.getPropertyBook().getDoubList().get("Width"), vine.getPropertyBook().getDoubList().get("Length")));		
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_VINE;
			requestNum = ticket;		
	    });
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_VINE);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up vine properties.		
		window.makeDoubleProperty("Scale", 1.0, book); 
		window.makeStringProperty("Image path", vine.getPath(), null);
		window.makeDoubleProperty("Arc Length (deg)", 180, book);
		window.makeDoubleProperty("Velocity", 1.0, book);
		window.makeDoubleProperty("Width", 1.0, book);
		window.makeDoubleProperty("Length", 5.0,  book);
		
		vine.updateProperties(window.getPropertyBook());
	}	
	
	public void makeNPCEditWindow(int ticket, NPC npc, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "NPC");		
		
		// Set up the the submit listener.
		window.setSubmitListener((arg0) -> {
				npc.updateProperties(window.getPropertyBook());	
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = window.getPropertyBook().getDoubList().get("Scale");
				
				npc.setScale(scale);					
					
				// Scale the image now.
				npc.setRI(resize(npc.getBI(), npc.getScaledIGWM(), npc.getScaledIGHM()));		
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_NPC;
			requestNum = ticket;		
     	});
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_NPC);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up NPC properties.		
		window.makeDoubleProperty("Scale", 1.0, book); 
		window.makeStringProperty("Image path", npc.getPath(), null);		
		window.makeDoubleProperty("Velocity", 1.0, book); 
		window.makeDoubleProperty("Range (m)", 5.0, book); 
		
		npc.updateProperties(window.getPropertyBook());
	}
	
	public void makeBoulder(String imageName, PropertyBook book, double xm, double ym, int oldTicket) {
		Boulder newBoulder = new Boulder(this.ticketer, imageName);
		if (oldTicket == 0) {
			newBoulder.setOldTicket(this.ticketer);
		} else {
			newBoulder.setOldTicket(oldTicket);
		}
		
		// Set the center.
		newBoulder.setCenter(xm, ym);
		
		// Set the default property book.
		String path = imageName.substring(10, newBoulder.getPath().length() - 4);
		newBoulder.setDefaultPropertyBook(getCollisionBook(path));
		
		// Make an EditWindow.
		makeBoulderEditWindow(this.ticketer, newBoulder, book);
		
		// Make its image.
		setImage(newBoulder, path);
		
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
		
		// Set up the the relevant listeners.
		window.setSubmitListener((arg0) -> {
				boulder.updateProperties(window.getPropertyBook());	
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = window.getPropertyBook().getDoubList().get("Scale");
				
				boulder.setScale(scale);
				boulder.scaleRadius(scale);
					
				// Scale the image now.
				boulder.setRI(resize(boulder.getBI(), boulder.getScaledIGWM(), boulder.getScaledIGHM()));
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_BOULDER;
			requestNum = ticket;		
	    });
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_BOULDER);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {	}
		});
		
		// Set up boulder properties.		
		window.makeDoubleProperty("Scale", 1.0, book); 
		window.makeBooleanProperty("StartFixed", true, book);
		window.makeStringProperty("Image path", boulder.getPath(), null);
		window.makeDoubleProperty("Mass", 1000.0, book); 
		window.makeDoubleProperty("Radius", 5.0, book); 
		window.makeBooleanProperty("Polygon collision", false, book); 

		boulder.updateProperties(window.getPropertyBook());
	}	
	
	public void makePegEditWindow(int ticket, Peg peg, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "Peg");		
		
		// Set up the needed listeners.
		window.setSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				peg.updateProperties(window.getPropertyBook());	
				
				// Next, update the relevant parts when scaling. Use resize()!
				double scale = window.getPropertyBook().getDoubList().get("Scale");
				
				peg.setScale(scale);					
					
				// Scale the image now.
				peg.setRI(resize(peg.getBI(), peg.getScaledIGWM(), peg.getScaledIGHM()));
			}			
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_PEG;
			requestNum = ticket;					
		});
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_PEG);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up peg properties.
		window.makeDoubleProperty("Scale", 1.0, book); 
		window.makeStringProperty("Image path", peg.getPath(), null);

		// Boulder ID requires some extra checking, as it may have changed.
		int counter[] = {0};
		if (book != null && book.getIntList().values() != null) {
			/*
			 * Look through the boulder IDs for the one that matches the old ID
			 * and update the peg.
			 */
			book.getIntList().values().forEach((oldBoulderID) -> {
				this.boulders.forEach((number, boulder) -> {
					if (boulder.getOldTicket() == oldBoulderID) {
						System.out.println("oldBoudlerID:" + oldBoulderID + " new ID: " + number);
						window.getPropertyBook().getIntList().put("old boulder " + oldBoulderID, number);
						counter[0]++;
					}
				});
			});
			peg.updateProperties(window.getPropertyBook());	
		}
		
		JLabel affectedBoulders = new JLabel();
		window.addComponentInstance("Affected boulders:", affectedBoulders);
		// If loading a peg, show its current values.
		String[] text = {""};
		window.getPropertyBook().getIntList().values().forEach((value) -> {
			text[0] += Integer.toString(value) + " ";
		});
		affectedBoulders.setText(text[0]);
		
		window.makeButton("Add affected boulder", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Request boulder number from user. Then, add it to int properties.
				String idString = JOptionPane.showInputDialog(null, "Enter boulder ticket #");
				int id = Integer.parseInt(idString);
				
				window.getPropertyBook().getIntList().put("boulder" + counter[0], id);
				counter[0]++;
				
				// Let the JLabel show any currently affected boulders.
				String[] text = {""};
				window.getPropertyBook().getIntList().values().forEach((value) -> {
					text[0] += Integer.toString(value) + " ";
				});
				affectedBoulders.setText(text[0]);
				
				peg.updateProperties(window.getPropertyBook());	
			}
		});
		
		window.makeButton("Remove affected boulder", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Request boulder number from user. Then, remove it from int properties.
				String idString = JOptionPane.showInputDialog(null, "Enter boulder ticket #");
				int id = Integer.parseInt(idString);
				
				window.getPropertyBook().getIntList().remove("boulder" + id);	
				
				// Let the JLabel show any currently affected boulders.
				String[] text = {""};
				window.getPropertyBook().getIntList().values().forEach((value) -> {
					text[0] += Integer.toString(value) + " ";
				});
				affectedBoulders.setText(text[0]);
				
				peg.updateProperties(window.getPropertyBook());	
			}		
		});
		
		window.makeDoubleProperty("Rotation (deg)", 0.0, book); 
		
		peg.updateProperties(window.getPropertyBook());
	}
	
	public void makeTextEditWindow(int ticket, TextTip tip, PropertyBook book) {
		EditWindow window = ltlAdapter.makeEditWindow(ticket, "TextTip");		
		
		// Set up the relevant listeners.
		window.setSubmitListener((arg0) -> {
				tip.updateProperties(window.getPropertyBook());	
				// Text uses font size, not scale, so nothing else needs to be done.		
		});
		
		window.setCenterListener((e) -> {
			request = Request.EDIT_OLD_TIP;
			requestNum = ticket;				
	    });
		
		window.setGroupSelectListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (groupSelect.containsKey(ticket)) {
					// Remove from the group selection list.
					groupSelect.remove(ticket);
					
				} else {
					// Add to the group selection list.
					groupSelect.put(ticket, Request.EDIT_OLD_TIP);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				
			}
		});
		
		// Set up text tip properties.		
		window.makeStringProperty("text", "", book); 
		window.makeIntProperty("fontSize", 15, book); 
		window.makeIntProperty("Z-order",  5,  book);
		
		tip.updateProperties(window.getPropertyBook());
	}
	
	/**
	 * Request everything in the selected group be moved.
	 */
	public void groupMove() {
		this.request = Request.EDIT_GROUP;
		System.out.println("Set request to EDIT_GROUP");
	}
	
	/**
	 * Remove everything from the group select group.
	 */
	public void deselectAll() {
		this.groupSelect.clear();
	}
	
	/**
	 * Select all present editable objects and put them into the group select group.
	 */
	public void selectAll() {
		// Add each list.
		this.groupSelect.clear();
		this.plats.forEach((ticket, plat) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_PLAT);
		});
		
		this.vines.forEach((ticket, vine) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_VINE);
		});
		
		this.boulders.forEach((ticket, boulder) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_BOULDER);
		});
		this.pegs.forEach((ticket, pegs) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_PEG);
		});
		this.npcs.forEach((ticket, npc) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_NPC);
		});
		this.textTips.forEach((ticket, tip) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_TIP);
		});
		this.attackZones.forEach((ticket, zone) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_ATTACK_ZONE);
		});
		this.quicksand.forEach((ticket, zone) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_QUICKSAND);
		});
		this.traps.forEach((ticket, trap) -> {
			this.groupSelect.put(ticket, Request.EDIT_OLD_TRAP);
		});
		
	}
	
	/**
	 * Make the character visible and request for their position to be set.
	 * @param name
	 * @req requesttype
	 */
	public void togglePlayer(String name, Request req) {
		this.characters.get(name).setPresent(true);
		setRequest("", req);
	}
	
	public void changeOffset(double xm, double ym) {
		this.vpOffset = new Point2D.Double(this.vpOffset.getX() + xm * this.mToPixel,
				this.vpOffset.getY() + ym * this.mToPixel);
	}

	public void clearAndSet() {
		// Reset the NPCs.
		this.npcs.clear();

		// Clear all of current lists.
		this.plats.clear();
		this.vines.clear();
		this.boulders.clear();		
		this.respawnPoints.clear();
		this.textTips.clear();
		this.pegs.clear();
		this.attackZones.clear();
		this.traps.clear();
		this.quicksand.clear();
		
		// By the way, they all need to be removed from the LayerWindow as well.
		this.ltlAdapter.clear();

		// Reset the ticketer.
		this.ticketer = 1;

		// Reset the viewpoint starting point.
		this.vpOffset = new Point2D.Double(0, 0);
	}
	
	public void removeEntity(int number, String type) {
		switch(type) {
		case "Peg": {
			this.pegs.remove(number);
			break;
		} 
		case "Boulder": {
			this.boulders.remove(number);
			break;
		}
		case "Vine": {
			this.vines.remove(number);
			break;
		}
		case "Platform": {
			this.plats.remove(number);
			break;
		}
		case "NPC": {
			this.npcs.remove(number);
			break;
		}
		case "TextTip": {
			this.textTips.remove(number);
			break;
		}
		case "AttackZone": {
			this.attackZones.remove(number);
			break;
		}
		case "Quicksand": {
			this.quicksand.remove(number);
			break;
		}
		}
	}
	
	/**
	 * Manual call to reset the level dimensions.
	 * 
	 * @param wp
	 *            width in pixels
	 * @param hp
	 *            height in pixels
	 */
	public void manualResize(double wp, double hp) {
		this.setViewportDimensions(wp / this.mToPixel, hp / this.mToPixel);
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
		this.levelWidthM = wm;
		this.levelHeightM = hm;

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
		
		this.bg.setRescaled(resize(this.bg.getImage(), this.levelWidthM, this.levelHeightM));
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

		this.bg = new Background(bgImage, path, this.levelWidthM, this.levelHeightM);
		this.bg.setRescaled(resize(bgImage, this.levelWidthM, this.levelHeightM));
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
	public BufferedImage resize(BufferedImage original, double wm, double hm) {
		double expectedWidth = wm * this.mToPixel;
		double widthScale = expectedWidth / original.getWidth();

		double expectedHeight = hm * this.mToPixel;
		double heightScale = expectedHeight / original.getHeight();
		
		BufferedImage after = new BufferedImage((int)expectedWidth, (int)expectedHeight, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(widthScale, heightScale);
		AffineTransformOp scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(original, after);
		return after;
	}
	
	/**
	 * After constructor is done initializing, start operations.
	 */
	public void start() {
		setBg(ASSETS_PATH + "bgSunny.png");
		this.timer.start();
	}
	
	/**
	 * Reads in a level from its GSON.
	 * 
	 * @param levelName
	 *            name of the level JSON file
	 */
	public void readJSON(String levelName) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
		LevelManager old;
		
		String fullPath;
		if (levelName.contains(".")) {
			// They added their own extension, don't add JSON to the end.
			fullPath = LEVELS_PATH + levelName;
		} else {
			fullPath = LEVELS_PATH + levelName + ".json";
		}
		
		try {
			old = gson.fromJson(new FileReader(fullPath), LevelManager.class);	
			System.out.println("Found path");
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + fullPath);
			// e.printStackTrace();		
			return;			
		}
		
		// Post processing on the characters.
		old.characters.forEach((name, character) ->  character.postDeserialization());
		
		// Do some preliminary cleaning.		
		clearAndSet();
		
		// Load in the level.
		this.setBg(old.bg.getPath());
		this.eolPoint = old.eolPoint;
		this.eolQuadrant = old.eolQuadrant;
		this.levelHeightM = old.levelHeightM;
		this.levelWidthM = old.levelWidthM;
		
		// Set up the characters
		old.characters.forEach((name, player) -> {
			this.characters.get(name).updateProperties(player.getPropertyBook());
			this.characters.get(name).setCenter(player.getCenterXM(), player.getCenterYM());
		});				
		
		this.respawnPoints = old.respawnPoints;
		old.boulders.forEach((ticket, boulder) -> {
			makeBoulder(boulder.getPath(), boulder.getPropertyBook(), 
					boulder.getCenterXM(), boulder.getCenterYM(), boulder.getOldTicket());
		});
		old.pegs.forEach((ticket, peg) -> {
			makeInteractable(peg.getPath(), peg.getPropertyBook(), peg.getCenterXM(), peg.getCenterYM(), "Peg");
		});		
		old.npcs.forEach((ticket, npc) -> {
			makeInteractable(npc.getPath(), npc.getPropertyBook(), npc.getCenterXM(), npc.getCenterYM(), "NPC");
		});
		old.plats.forEach((ticket, plat) -> {
			int tick = makeInteractable(plat.getPath(), plat.getPropertyBook(), plat.getCenterXM(), plat.getCenterYM(), "Platform");
			this.plats.get(tick).setEndpoint(plat.getEndpoint());
		});		
		old.vines.forEach((ticket, vine) -> {
			makeInteractable(vine.getPath(), vine.getPropertyBook(), vine.getCenterXM(), vine.getCenterYM(), "Vine");
		});
		if (old.traps != null) {
			old.traps.forEach((ticket, trap) -> {
				makeInteractable(trap.getPath(), trap.getPropertyBook(), trap.getCenterXM(), trap.getCenterYM(), "Trap");
			});
		}
		if (old.textTips != null) {
		    old.textTips.forEach((ticket, tip) -> {
		    	makeInteractable("", tip.getPropertyBook(), tip.getCenterXM(), tip.getCenterYM(), "TextTip");
		    });
		}
		if (old.attackZones != null) {
		    old.attackZones.forEach((ticket, zone) -> {
			    makeInteractable(zone.getPath(), zone.getPropertyBook(), zone.getCenterXM(), zone.getCenterYM(), "AttackZone");
	    	});
		}
		if (old.quicksand != null) {
			old.quicksand.forEach((ticket, zone) -> {
				makeInteractable(zone.getPath(), zone.getPropertyBook(), zone.getCenterXM(), zone.getCenterYM(), "Quicksand");
			});
		}
		
		// Update the boulders to have their old tickets match their new.
		this.boulders.forEach((number, boulder) -> {
			boulder.updateTicket();
		});
		
		// Tell output window what the level names are, etc.
		this.levelFileName = old.levelFileName;
		this.nextLevelName = old.nextLevelName;
		this.endQuote = old.endQuote;
		this.levelName = old.levelName;
		this.levelNumber = old.levelNumber;
		if (old.viewportZone != null) {
		    this.viewportZone = old.viewportZone;
		}
		Point2D.Double min = viewportZone.getMinLimit();
		Point2D.Double max = viewportZone.getMaxLimit();
		ltcAdapter.setNewCameraLimits(min.x, max.x, min.y, max.y);
		ltoAdapter.setLevelName(old.levelName);
		ltoAdapter.setNextName(old.nextLevelName);
		ltoAdapter.setEndQuote(old.endQuote);
		ltoAdapter.setLevelNumber(old.levelNumber);
		ltoAdapter.setLevelFile(old.levelFileName);
		
		// Resize as necessary.
		setMToPixel(old.mToPixel);
	}
	
	/**
	 * Outputs a JSON file.
	 * 
	 * @param levelName
	 *            name of the level
	 * @param nextName name of the next level
	 * @param endQuote 
	 */
	public void makeJSON(String levelFile, String levelName, String nextName, String endQuote, int levelNumber) {
		this.levelFileName = levelFile;
		this.levelName = levelName;
		this.endQuote = endQuote;
		this.nextLevelName = nextName;	
		this.levelNumber = levelNumber;
		
		// Try to write out the JSON file.
		String fullPath;
		if (levelName.contains(".")) {
			// They added their own extension, don't add JSON to the end.
			fullPath = LEVELS_PATH + levelFile;
			levelName = levelName.substring(0, levelName.indexOf('.'));
		} else {
			fullPath = LEVELS_PATH + levelFile + ".json";
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new AnnotationExclusionStrategy()).serializeNulls().create();
		
		String output = gson.toJson(this);
		
		FileWriter file;
		try {
			file = new FileWriter(fullPath);
			// Send over the name of the level, so everything before the '.' in the extension.
			file.write(output);
			file.flush();
			file.close();
			System.out.println("Output JSON written to " + LEVELS_PATH);
		} catch (IOException e1) {			
			e1.printStackTrace();
		}		
		return;	
	}	
	
	public void setEOL(Point2D.Double eolM) {
		this.eolPoint = eolM;
	}
	
	public void addRP(Point2D.Double rp) {
	    this.respawnPoints.add(rp);
	}
	
	public void setEndPoint(Point2D.Double endPoint, int ticket) {
	    this.plats.get(ticket).setEndpoint(endPoint);	
	}
	
	public int getNewBoulderTicketFromOld(int oldTicket) {
	    for (Map.Entry<Integer, Boulder> b : boulders.entrySet()) {
	    	if (b.getValue().getOldTicket() == oldTicket) {
	    		return b.getKey();
	    	}
	    }
	    return -1;
	}
	
	public Player getCharacter(String name) {
		return characters.get(name);
	}

	public void setViewportLimits(double xmin, double xmax, double ymin, double ymax) {
		this.viewportZone.setLimits(xmin, xmax, ymin, ymax);		
	}
}
