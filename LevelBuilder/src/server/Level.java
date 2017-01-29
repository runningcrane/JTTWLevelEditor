package server;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import interactable.Platform;
import noninteractable.Background;
import noninteractable.INonInteractable;

public class Level {
	private double mToPixel;
	private Background bg;
	private INonInteractable fg;
	private Platform plats;
	
	private ILevelToBuilderAdapter ltbAdapter;
	
	public Level(ILevelToBuilderAdapter ltbAdapter) {
		this.ltbAdapter = ltbAdapter;
	}
	
	public void start() {
		mToPixel = 100;		 		
	}
	
	public void setBg(Component panel) {
		Image bgImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("assets/bg/bgSunny.png"));
		MediaTracker mt = new MediaTracker(panel);

		// Wait for image to load
		mt.addImage(bgImage, 1);
		try {
			mt.waitForAll();
		} catch (Exception e) {
			System.out.println("ImagePaintStrategy.init(): Error waiting for image.  Exception = " + e);
		}
		// bg = new Background(resize(10,bgIcon));
		bg = new Background(bgImage);
	}
	
	public void render(Component panel, Graphics g) {
		System.out.println("drawing background");	
		if (bg != null)
			g.drawImage(bg.getImage(), 300, 100, null);
	}
	
	public ImageIcon resize(double widthMeters, ImageIcon original) {
		double expectedWidth = widthMeters * this.mToPixel;
		double scale = expectedWidth/original.getIconWidth(); 
		return new ImageIcon(original.getImage().getScaledInstance((int)(original.getIconWidth() * scale), 
				(int)(original.getIconWidth() * scale),
				java.awt.Image.SCALE_SMOOTH));
	}

}
