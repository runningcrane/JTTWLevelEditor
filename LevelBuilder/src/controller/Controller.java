package controller;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import client.IControlToLevelAdapter;
import client.ILayerToLevelAdapter;
import client.IOutputToLevelAdapter;
import client.LayerWindow;
import client.OutputWindow;
import client.ControlWindow;
import server.LevelManager;
import server.ILevelToLayerAdapter;
import server.ILevelToOutputAdapter;
import server.ILevelToControlAdapter;

/**
 * Controller of this MVC system. Start the program here.
 * @author Melinda Crane
 */
public class Controller {
	/**
	 * Control panel gives level-editing/placement options.
	 */
	ControlWindow controlWindow;
	
	/**
	 * Layer panel informs which objects are on the screen and allows postedits.
	 */
	LayerWindow layerWindow;
	
	/**
	 * Panel on which the mock level is displayed.
	 */
	OutputWindow outputWindow;
	
	/**
	 * Model that manages the various parts of the Level.
	 */
	LevelManager level;
	
	/**
	 * Constructor for the Controller.
	 */
	public Controller() {
		controlWindow = new ControlWindow(new IControlToLevelAdapter() {

			@Override
			public void togglePlayer(String name, boolean status) {
				level.togglePlayer(name, status);				
			}

			@Override
			public void setLevelDimensions(double wm, double hm) {
				level.setLevelDimensions(wm, hm);				
			}
			
			@Override
			public void setBg(String path) {
				level.setBg(path);
			}
			
		});
		
		layerWindow = new LayerWindow(new ILayerToLevelAdapter() {

			
		});
		
		outputWindow = new OutputWindow(new IOutputToLevelAdapter() {
			@Override
			public void render(Component panel, Graphics g) {
				level.render(panel, g);			
			}

			@Override
			public JSONObject makeJSON() {
				return level.makeJSON();
			}

			@Override
			public void manualResize(double wp, double hp) {
				level.manualResize(wp, hp);				
			}
			
		});
		
		level = new LevelManager(new ILevelToControlAdapter() {
			
		}, new ILevelToOutputAdapter() {

			@Override
			public void setDimensions(int wm, int hm) {
				outputWindow.setDimensions(wm, hm);				
			}

			@Override
			public void redraw() {
				outputWindow.redraw();				
			}		
			
		}, new ILevelToLayerAdapter() {
			
		});
		
	}
	
	private void start() {

		outputWindow.start();
		level.start();
		controlWindow.start();
		layerWindow.start();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() { // Java specs say that the
													// system must be constructed on
												// the GUI event thread.
			public void run() {
				try {
					Controller controller = new Controller(); // instantiate
														// system
					controller.start(); // start the system
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
