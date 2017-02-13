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

public class EditWindow extends JPanel {

	private JPanel contentPane;
	private int ticket;
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
		setLayout(new GridLayout(3, 1, 0, 0));
				
		JButton btnMove = new JButton("Move");
		btnMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		JLabel lblTicket = new JLabel("Ticket #");
		add(lblTicket);
		add(btnMove);
		
		JButton btnCollision = new JButton("Edit Box");
		btnCollision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(btnCollision);
		
		JButton btnDelete = new JButton("Remove");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnDelete.setBackground(Color.RED);
		btnDelete.setForeground(Color.WHITE);
		add(btnDelete);		
	}

	public void start() {
		setVisible(true);
	}	
}
