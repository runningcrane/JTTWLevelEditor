package new_controller;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import new_client.IControlToLevelAdapter;
import new_client.ILayerToLevelAdapter;
import new_client.IOutputToLevelAdapter;
import new_client.LayerWindow;
import new_client.OutputWindow;
import new_client.ControlWindow;
import new_client.EditWindow;
import new_server.LevelManager;
import new_server.ILevelToLayerAdapter;
import new_server.ILevelToOutputAdapter;
import new_server.ILevelToControlAdapter;

/**
 * Controller of this MVC system. Start the program here.
 * 
 * @author Melinda Crane
 */
public class Controller {
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
			public void makePlatform(String path) {
				level.setRequest(path, "Platform");
			}

			@Override
			public void makeVine(String path) {
				level.setRequest(path, "Vine");
			}

			@Override
			public void markEOL() {
				level.setRequest("", "EOL");
			}

			@Override
			public void setBg(String path) {
				level.setBg(path);
			}

			@Override
			public void setLevelDimensions(double wm, double hm) {
				level.setLevelDimensions(wm, hm);
			}

			@Override
			public void setMToPixel(double mToPixel) {
				level.setMToPixel(mToPixel);
			}

			@Override
			public void setViewportDimensions(double wm, double hm) {
				level.setViewportDimensions(wm, hm);
			}

			@Override
			public void togglePlayer(String name, boolean status) {
				level.togglePlayer(name, status);
			}

			@Override
			public void markRP() {
				level.setRequest("", "RP");	
			}

			@Override
			public void removeRP() {
				level.setRequest("", "RMRP");				
			}

			@Override
			public void makePeg(String path) {
				level.setRequest(path, "Peg");
			}
			
			@Override
			public void makeTrap(String path) {
				level.setRequest(path, "Trap");
			}

			@Override
			public void makeBoulder(String path) {
				level.setRequest(path, "Boulder");
			}

		});

		layerWindow = new LayerWindow(new ILayerToLevelAdapter() {

			@Override
			public void removeEntity(int number, String type) {
				level.removeEntity(number, type);
			}

		});

		outputWindow = new OutputWindow(new IOutputToLevelAdapter() {

			@Override
			public void render(Component panel, Graphics g) {
				level.render(panel, g);
			}

			@Override
			public String makeJSON(String levelName, String nextName) {
				return level.makeJSON(levelName, nextName);
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
			public void sendCoordinates(double xp, double yp) {
				level.receiveCoordinates(xp, yp);
			}

			@Override
			public void changeOffset(double xm, double ym) {
				level.changeOffset(xm, ym);
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
			public void setLevelName(String levelName) {
				outputWindow.setLevelName(levelName);
			}

			@Override
			public void setNextName(String nextName) {
				outputWindow.setNextName(nextName);
			}

		}, new ILevelToLayerAdapter() {

			@Override
			public EditWindow makeEditWindow(int number, String name) {
				return layerWindow.makeEditWindow(number, name);
			}

			@Override
			public void clear() {
				layerWindow.clean();
			}

		});

	}

	private void start() {

		outputWindow.start();
		level.start();
		controlWindow.start();
		layerWindow.start();
	}

}
