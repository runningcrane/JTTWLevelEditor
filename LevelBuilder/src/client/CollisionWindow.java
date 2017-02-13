package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.Controller;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CollisionWindow extends JFrame {

	private JPanel contentPane;
	private ArrayList<Point2D.Double> points;
	private double centerXp;
	private double centerYp;
	private double trueCenterXm;
	private double trueCenterYm;
	private double scale = 100; // pxs per meter.
	private BufferedImage image;
	private ImageIcon rescaledImage;
	private boolean set = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() { // Java specs say that the
													// system must be constructed on
												// the GUI event thread.
			public void run() {
				try {
					CollisionWindow window = new CollisionWindow("assets/Rock2.png", 3.0, 5.0, 10.0, 15.0);
					window.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the frame.
	 */
	public CollisionWindow(String path, double wm, double hm, 
			double cxm, double cym) {
		initGUI();
		points = new ArrayList<Point2D.Double>();
		this.centerXp = wm * this.scale * 0.5;
		this.centerYp = hm * this.scale * 0.5;
		this.trueCenterXm = cxm;
		this.trueCenterYm = cym;	
		
		try {
			this.image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("File not found: " + path);
			e.printStackTrace();
			return;
		}
		
		this.rescaledImage = new ImageIcon(
				this.image.getScaledInstance((int)(this.image.getWidth() * this.scale * wm /this.image.getWidth()), 
				(int)(this.image.getHeight() * this.scale * hm /this.image.getHeight()),
				java.awt.Image.SCALE_SMOOTH));
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// Control panel
		JPanel pnlControls = new JPanel();
		pnlControls.setBackground(UIManager.getColor("inactiveCaption"));
		contentPane.add(pnlControls, BorderLayout.NORTH);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!set) {
					JOptionPane.showMessageDialog(null, "No boundary set");
				}
			}
		});
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setBackground(Color.RED);
		pnlControls.add(btnCancel);
		
		JButton btnClear = new JButton("Clear Points");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove all made points.
				points.clear();
			}
		});
		pnlControls.add(btnClear);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				set = true;
				returnPoints();
			}
		});
		pnlControls.add(btnFinish);
		
		// Display the image to make points of
		
		JPanel pnlDisplay = new JPanel() {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 242174451501027598L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				// Draw image on the screen
				g.drawImage(rescaledImage.getImage(), 0, 0, null);
				
				// Draw points on screen
				int[] iter = {1};
				points.forEach((point) -> {
					// Draw point
					g.setColor(Color.MAGENTA);
					g.fillOval((int)point.getX(), (int)point.getY(), 15, 15);
					// Label point
					g.setColor(Color.BLACK);
					g.drawString(Integer.toString(iter[0]), (int)point.getX() + 5, (int)point.getY() + 10);
					iter[0]++;
				});
				// g.fillOval((int)centerXp, (int)centerYp, 10, 10);
				

			}
		};	
		pnlDisplay.setBackground(UIManager.getColor("Button.background"));
		pnlDisplay.setLayout(new BorderLayout(0,0));
		contentPane.add(pnlDisplay, BorderLayout.CENTER);
		// Based on the expected width & height, change the size of the frame
		pnlDisplay.setSize((int)(this.centerXp * 2), (int)(this.centerYp * 2));
		
		pnlDisplay.addMouseListener(new MouseListener() {
		    @Override
			public void mouseClicked(MouseEvent e) {
			    int xp = e.getX();
			    int yp = e.getY();
			    points.add(new Point2D.Double(xp,  yp));
			    System.out.println("added point " + xp + ", " + yp);
			    pnlDisplay.repaint();
		    }

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void start() {
		setVisible(true);
	}
	
	public void setNewCenter(double newCxm, double newCym) {
		this.trueCenterXm = newCxm;
		this.trueCenterYm = newCym;
	}

	public ArrayList<Point2D.Double> returnPoints() {
		/*
		 * Convert all chosen points to their actual locations on the screen,
		 * then into in-game meters.
		 * Cocos2dx's origin is in the *bottom left*, not *top left*.
		 * Thus each point's ym must be subtracted from the height.
		 */
		ArrayList<Point2D.Double> correctedPoints = new ArrayList<Point2D.Double>(); 
		double xOffsetm = this.trueCenterXm - (this.centerXp / this.scale);
		double yOffsetm = this.trueCenterYm - (this.centerYp / this.scale);
		
		double hm = this.centerYp * 2 / this.scale;
			
		points.forEach((point) -> {
					correctedPoints.add(new Point2D.Double(point.getX()/this.scale + xOffsetm,
							(hm - point.getY()/this.scale) + yOffsetm));
				});
		this.setVisible(false);
		return correctedPoints;
	}
	
	public void startWithPoints(ArrayList<Point2D.Double> points) {
		this.points = points;
	}
}
