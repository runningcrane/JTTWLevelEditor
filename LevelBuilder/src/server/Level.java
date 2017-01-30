package server;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	
	public void setBg(String path) {
		BufferedImage bgImage;
		try {
			bgImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("File not found: " + path);
			e.printStackTrace();
			return;
		}
		
		bg = new Background(bgImage);
	}
	
	public void render(Component panel, Graphics g) {			
		if (bg != null)
			g.drawImage(bg.getImage(), 0, 0, null);
	}
	
	public ImageIcon resize(double widthMeters, ImageIcon original) {
		double expectedWidth = widthMeters * this.mToPixel;
		double scale = expectedWidth/original.getIconWidth(); 
		return new ImageIcon(original.getImage().getScaledInstance((int)(original.getIconWidth() * scale), 
				(int)(original.getIconWidth() * scale),
				java.awt.Image.SCALE_SMOOTH));
	}

}
