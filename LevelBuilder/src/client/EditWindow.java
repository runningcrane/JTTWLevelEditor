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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class EditWindow extends JPanel {

	private JPanel contentPane;
	private int ticket;
	private BufferedImage image;

	/**
	 * Create the frame.
	 */
	public EditWindow(int ticket, String path) {
		this.ticket = ticket;
		// Add "Thumbnail" to the image name.
		String appendedPath = path.substring(0, -4) + "Thumbnail" + path.substring(-4, -1);		
		
		try {
			this.image = ImageIO.read(new File(appendedPath));
		} catch (IOException e) {
			System.err.println("File not found: " + appendedPath);
			e.printStackTrace();
			return;
		}
		
		initGUI();

	}
	
	public void initGUI() {
		setLayout(new GridLayout(3, 1, 0, 0));
		
		JPanel pnlImageHolder = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
			}
		};
		add(pnlImageHolder);
		
		JButton btnMove = new JButton("Move");
		btnMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				adapter.editCenter(ticket);
			}
		});
		add(btnMove);
		
		JButton btnCollision = new JButton("Edit Box");
		btnCollision.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.editCollisionBox(ticket);
			}
		});
		add(btnCollision);
		
		JButton btnDelete = new JButton("Remove");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.remove(ticket);
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
