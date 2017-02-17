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
			public void setMToPixel(double mToPixel) {
				level.setMToPixel(mToPixel);				
			}			

			@Override
			public void togglePlayer(String name, boolean status) {
				level.togglePlayer(name, status);				
			}

			@Override
			public void setLevelDimensions(double wm, double hm) {
				level.setLevelDimensions(wm, hm);				
			}
			
			@Override
			public void setViewportDimensions(double wm, double hm) {
				level.setViewportDimensions(wm, hm);				
			}
			
			@Override
			public void setBg(String path) {
				level.setBg(path);
			}

			@Override
			public void makePlatform(String path) {
				level.setActive(path);
			}

			@Override
			public void markEOL() {
				level.markEOL();				
			}
			
		});
		
		layerWindow = new LayerWindow(new ILayerToLevelAdapter() {

			@Override
			public void editPlatCenter(int ticket) {
				level.editPlatCenter(ticket);				
			}

			@Override
			public void editPlatCollisionBox(int ticket) {
				level.editPlatCollisionBox(ticket);				
			}

			@Override
			public void removePlat(int ticket) {
				level.removePlat(ticket);				
			}

			@Override
			public void changeDimPlat(int ticket, double wm, double hm) {
				level.editPlatDim(ticket, wm, hm);				
			}
			
			@Override
			public void makeEndpointPlat(int ticket) {
				level.makeEndpointPlat(ticket);
			}

			@Override
			public void toggleDisappearsPlat(int ticket, boolean selected) {
				level.toggleDisappearsPlat(ticket, selected);
				
			}

			@Override
			public void toggleMoveablePlat(int ticket, boolean selected) {
				level.toggleMoveablePlat(ticket, selected);
			}

			@Override
			public void toggleSinkablePlat(int ticket, boolean selected) {
				level.toggleSinkablePlat(ticket, selected);
			}

			@Override
			public void toggleClimbablePlat(int ticket, boolean selected) {
				level.toggleClimbablePlat(ticket, selected);
			}

			@Override
			public void setPhysicsPlat(int ticket, double scK) {
				level.setPhysicsPlat(ticket, scK);
			}

			@Override
			public void setVelocityPlat(int ticket, double velocity) {
				level.setVelocityPlat(ticket, velocity);
			}

			
		});
		
		outputWindow = new OutputWindow(new IOutputToLevelAdapter() {
			@Override
			public void render(Component panel, Graphics g) {
				level.render(panel, g);			
			}

			@Override
			public JSONObject makeJSON(String levelName, String nextName) {
				return level.makeJSON(levelName, nextName, false);
			}

			@Override
			public void readJSON(String levelPath) {
				level.readJSON(levelPath);				
			}
			
			@Override
			public void manualResize(double wp, double hp) {
				level.manualResize(wp, hp);				
			}

			@Override
			public void makePlatform(String path, double xp, double yp, double wm, double hm) {
				level.makePlatform(path, xp, yp, wm, hm, null);				
			}

			@Override
			public void setPlayerPosition(String name, double xp, double yp) {
				level.setCharacterPosition(name, xp, yp);
			}

			@Override
			public void changeOffset(double xm, double ym) {
				level.changeOffset(xm, ym);				
			}

			@Override
			public void editPlatCenter(int ticket, double xp, double yp) {
				level.editPlatCenterRes(ticket, xp, yp);
				
			}

			@Override
			public void setEOL(double xp, double yp) {
				level.setEOL(xp, yp);			
			}

			@Override
			public void setEndpointPlat(int ticket, double xp, double yp) {
				level.setEndpointPlat(ticket, xp, yp);				
			}
			
		});
		
		level = new LevelManager(new ILevelToControlAdapter() {

			@Override
			public void setMToPixel(double mToPixel) {
				controlWindow.setMToPixel(mToPixel);				
			}
			
		}, new ILevelToOutputAdapter() {

			@Override
			public void setDimensions(int wm, int hm) {
				outputWindow.setDimensions(wm, hm);				
			}

			@Override
			public void redraw() {
				outputWindow.redraw();				
			}

			@Override
			public void makePlatform(String path) {
				outputWindow.setActive(path);				
			}
			
			@Override
			public void setLevelName(String levelName) {
				outputWindow.setLevelName(levelName);				
			}

			@Override
			public void setNextName(String nextName) {
				outputWindow.setNextName(nextName);
				
			}		
			
			@Override
			public void setCharPos(String playerName) {
				outputWindow.setCharPos(playerName);				
			}

			@Override
			public void setPlatPos(int ticket) {
				outputWindow.setPlatPos(ticket);
				
			}

			@Override
			public void markEOL() {
				outputWindow.markEOL();				
			}

			@Override
			public void makeEndpointPlat(int ticket) {
				outputWindow.setEndpointPlat(ticket);
				
			}
			
		}, new ILevelToLayerAdapter() {

			@Override
			public void addEdit(int ticket, double wm, double hm) {
				layerWindow.addPlatformEdit(ticket, wm, hm);				
			}
			
			public void removeAllWindows() {
				layerWindow.removeAllWindows();
			}
			
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
