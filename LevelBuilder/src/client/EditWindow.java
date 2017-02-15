package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EditWindow extends JPanel {

	private JPanel contentPane;
	protected int ticket;
	private BufferedImage image;
	
	public EditWindow getThis() {
		return this;
	}

	/**
	 * Create the frame.
	 */
	public EditWindow(int ticket) {
		this.ticket = ticket;
		
		initGUI();

	}
	
	
	
	public void initGUI() {
		// To be filled in by LayerWindow.
	}

	public void start() {
		setVisible(true);
	}	
	
	/**
	 * Used when loading in a JSON file. We need a way to get rid of this window AND its JSON separator.
	 */
	public void manualRemove() {
		// To be filled in in LayerWindow
	}
}
