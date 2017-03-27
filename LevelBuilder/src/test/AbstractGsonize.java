package test;

import junit.framework.*;
import new_interactable.AInteractable;
import new_interactable.Player;
import utils.RuntimeTypeAdapterFactory;

import static utils.Constants.ASSETS_PATH;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AbstractGsonize {
	AInteractable player;
	Gson gson;
	
	@Before 
	public void initialize() {
		System.out.println("\nSetup\n---------");
		player = new Player(ASSETS_PATH + "Monkey" + ".png");
		BufferedImage playerBI;
		try {
			playerBI = ImageIO.read(new File(ASSETS_PATH + "Monkey" + ".png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + "Monkey" + ".png");
			e.printStackTrace();
			return;
		}
		player.setBI(playerBI);
		player.setRI(resize(playerBI, 0.7, 1.7));
		player.setCenter(0, 0);
		player.getPropertyBook().getBoolList().put("Present", false);
		
		// Set up gson		
		gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	}
	   
	@Test
	public void gsonPlayerFields1Test() throws Exception {		
		String output = gson.toJson(player.getPropertyBook());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields2Test() throws Exception {		
		String output = gson.toJson(player.getDefaultPropertyBook());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields3Test() throws Exception {		
		String output = gson.toJson(player.getBI());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields4Test() throws Exception {		
		String output = gson.toJson(player.getRI());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields5Test() throws Exception {		
		String output = gson.toJson(player.getScale());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields6Test() throws Exception {		
		String output = gson.toJson(player.getScaledIGWM());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields7Test() throws Exception {		
		System.out.println(player.getCenterXM());
		String output = gson.toJson(player.getCenterXM());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields8Test() throws Exception {		
		System.out.println(player.getCenterYM());
		String output = gson.toJson(player.getCenterYM());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields9Test() throws Exception {		
		System.out.println(player.getPath());
		String output = gson.toJson(player.getPath());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields10Test() throws Exception {		
		System.out.println(player.getTicket());
		String output = gson.toJson(player.getTicket());
		System.out.println(output);
	}
	
	@Test
	public void gsonPlayerFields11Test() throws Exception {		
		String output = gson.toJson(player.getScaledIGHM());
		System.out.println(output);
	}
	
	@Test
    public void gsonPlayerTest() throws Exception {			
		/*
		Gson gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapterFactory(RuntimeTypeAdapterFactory
						.of(AInteractable.class)
						.registerSubtype(AInteractable.class)
						.registerSubtype(Player.class)).create();
		*/
		String output = gson.toJson(player);
		System.out.println(output);
        
    }
	
	/* Pulled from LevelManager. */
	public ImageIcon resize(BufferedImage original, double wm, double hm) {
		double expectedWidth = wm * 100;
		double widthScale = expectedWidth / original.getWidth();

		double expectedHeight = hm * 100;
		double heightScale = expectedHeight / original.getHeight();

		return new ImageIcon(original.getScaledInstance((int) (original.getWidth() * widthScale),
				(int) (original.getHeight() * heightScale), java.awt.Image.SCALE_SMOOTH));
	}
}
