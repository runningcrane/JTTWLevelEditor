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
		setLayout(new GridLayout(3, 1, 0, 0));
				
		JButton btnMove = new JButton("New center");
		btnMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		JLabel lblTicket = new JLabel("#" + this.ticket);
		add(lblTicket);
		add(btnMove);
		
		JButton btnCollision = new JButton("Edit collision");
		btnCollision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});		
		add(btnCollision);
		
		JLabel lblWidth = new JLabel("Width (m):");
		add(lblWidth);
		
		JTextField txtWidth = new JTextField();
		JTextField txtHeight = new JTextField();
		add(txtWidth);
		txtWidth.setColumns(10);
		
		JButton btnDimensions = new JButton("Change dimensions");
		btnDimensions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		add(btnDimensions);
		
		JLabel lblNewLabel = new JLabel("Height (m):");
		add(lblNewLabel);
				
		add(txtHeight);
		txtHeight.setColumns(10);
		
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
