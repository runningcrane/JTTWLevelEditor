package controller;

import java.awt.EventQueue;
import java.awt.Graphics;

import client.IBuilderToLevelAdapter;
import client.Window;
import server.Level;
import server.ILevelToBuilderAdapter;

public class Controller {
	Window window;
	Level level;
	
	public Controller() {
		window = new Window(new IBuilderToLevelAdapter() {

			@Override
			public void render(Graphics g) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		level = new Level(new ILevelToBuilderAdapter() {
			
		});
		
	}
	
	private void start() {
		window.start();
		level.start();
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
