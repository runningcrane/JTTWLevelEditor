package new_server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

import org.json.simple.JSONObject;

import interactable.BoulderJoint;
import interactable.Enemy;
import interactable.GoldenPeg;
import interactable.Platform;
import interactable.Player;
import interactable.Vine;
import new_client.EditWindow;
import new_client.ILevelToLayerAdapter;
import new_client.ILevelToOutputAdapter;
import new_interactable.Boulder;
import new_interactable.PropertyBook;
import noninteractable.Background;
import noninteractable.INonInteractable;
import server.ILevelToControlAdapter;

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
}
