package new_controller;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import org.json.simple.JSONObject;

import new_client.IControlToLevelAdapter;
import new_client.ILayerToLevelAdapter;
import new_client.IOutputToLevelAdapter;
import new_client.LayerWindow;
import new_client.OutputWindow;
import new_client.ControlWindow;
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


		});

		layerWindow = new LayerWindow(new ILayerToLevelAdapter() {

		});

		outputWindow = new OutputWindow(new IOutputToLevelAdapter() {

		});

		level = new LevelManager(new ILevelToControlAdapter() {

		}, new ILevelToOutputAdapter() {

		}, new ILevelToLayerAdapter() {

		});

	}

	private void start() {

		outputWindow.start();
		level.start();
		controlWindow.start();
		layerWindow.start();
	}

}
