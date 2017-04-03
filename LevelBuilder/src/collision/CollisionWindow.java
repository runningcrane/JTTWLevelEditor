package collision;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.GsonBuilder;

import interactable.PropertyBook;

import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JLabel;

import static utils.Constants.*;

/**
 * Helper window for setting a collision box for an image.
 * 
 * @author melindacrane
 */
public class CollisionWindow extends JFrame {

	private static final long serialVersionUID = -1482793564160863642L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtMass;
	private JTextField txtRadius;	
	private JLabel lblMass;
	private JLabel lblRadius;
	private JLabel lblWidth;
	private JLabel lblHeight;
	private JLabel lblFriction;
	private JLabel lblFrictionVal;
	private JButton btnPlatDim;
	private JButton btnBoulderDim;
	private JPanel pnlContent;
		
	String name;
	private double imgWidth;
	private double imgHeight;
	private double zoomLevel;
	private double mass;
	private double radius;
	private double currentFriction = 0.0;
	private ArrayList<Point2D.Double> points = new ArrayList<>();
	private ArrayList<Double> edgeFrics = new ArrayList<>();
	
	private BufferedImage image;
	private ImageIcon rescaledImage;
	
	/**
	 * Viewport offset (px).
	 */
	private Point2D.Double vpOffset;
	
	private double offset;
	private double mToPixel;
	private JTextField txtHeight;
	private JTextField txtWidth;
	
	public CollisionWindow() {
		this.mToPixel = 100;
		this.name = "";
		this.imgWidth = 3;
		this.imgHeight = 3;
		this.zoomLevel = 1;

		// Viewport stuff
		this.vpOffset = new Point2D.Double(0, 0);
		this.offset = 1.5;				
		
		initGUI();
	}
	
	/**
	 * Makes a default collision file for a new assets.
	 * @param name The asset name (without extension).
	 * @return 0 if new default was created, -1 on error.
	 */
	public int makeDefault(String name) {
		// Check if the image exists.	
		try {
			this.image = ImageIO.read(new File(ASSETS_PATH + name + ".png"));
		} catch (IOException e) {
			System.err.println("Image not found in assets folder: " + name);
			e.printStackTrace();
			return -1;
		}
		
		// If the image exists, then make a JSON file for it.
		this.name = name;
		this.outputJSON();

		return 0;
	}
	
	public void clear() {
		this.zoomLevel = 1;
		this.points.clear();
		this.vpOffset = new Point2D.Double(0, 0);
		this.repaint();
	}
	
	public void readJSON() {
		// Clear everything first.
		clear();
		
		// Try finding the file to parse.
		Gson gson = new Gson();
		PropertyBook book;
		
		String path = txtName.getText();
		try {
			book = gson.fromJson(new FileReader(COL_PATH + path + ".json"), PropertyBook.class);
			System.out.println("Found path");
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + COL_PATH + path + ".json");
			if (makeDefault(path) == 0) {
			    // Make default worked, so Call readJSON again. 
			    readJSON();
			}
			return;
		}
		
		// Make the image.		
		try {
			this.image = ImageIO.read(new File(ASSETS_PATH + path + ".png"));
		} catch (IOException e) {
			System.err.println("File not found: " + ASSETS_PATH + path + ".png");
			e.printStackTrace();
			return;
		}
				
		String name = book.getStringList().get("Name");
		// Check if null.
		if (name == null) {
			name = path;
		}				
		
		Double zoomLevelD = book.getDoubList().get("zoomLevel");
		double zoomLevel;
		// Check if null.
		if (zoomLevelD == null) {
			zoomLevel = 10;
		} else {
			zoomLevel = zoomLevelD.doubleValue();
		}
		
		Double widthD = book.getDoubList().get("widthm");
		double width;
		// Check if null.
		if (widthD == null) {
			width = this.image.getWidth() / this.mToPixel;
		} else {
			width = widthD.doubleValue();
			txtWidth.setText(Double.toString(width));
		}
			
		Double heightD = book.getDoubList().get("heightm");
		double height;
		// Check if null.
		if (heightD == null) {
			height = this.image.getHeight() / this.mToPixel;
		} else {
			height = heightD.doubleValue();
			txtHeight.setText(Double.toString(height));
		}
				
		ArrayList<Point2D.Double> points = new ArrayList<>();
		// Check if null.		
		for (Point2D.Double point : book.getCollPoints()) {											
			points.add(point);
		}
		
		for (Double d : book.getEdgeFrictions()) {
		    this.edgeFrics.add(d);
		}
		
		// Set shared fields.
		this.name = name;	
		this.zoomLevel = zoomLevel;
		this.points = cocosToSwing(points); // Translate the points back to swing coordinates. 
		this.imgWidth = width;
		this.imgHeight = height;
		
		// Show type-specific options.
		if (path.toLowerCase().contains("boulder")) {
			// Set the boulder-related objects visible.
			this.txtMass.setEnabled(true);
			this.txtRadius.setEnabled(true);
			this.btnBoulderDim.setEnabled(true);
			
			Double massD = book.getDoubList().get("mass");
			double mass;
			// Check if null.
			if (massD == null) {
				mass = 1000;
			} else {
				mass = massD.doubleValue();
				txtMass.setText(Double.toString(mass));
			}
			
			Double radiusD = book.getDoubList().get("radius");
			double radius;
			// Check if null.
			if (radiusD == null) {
				radius = 3;
			} else {
				radius = radiusD.doubleValue();
				txtRadius.setText(Double.toString(radius));
			}
			
			// Set boulder-specific values.
			this.mass = mass;
			this.radius = radius;
			
			// Draw the rescaled boulder.
			rescaleBoulder();
			
		} else {
			// Set the plat-related objects visible.
			this.txtMass.setEnabled(false);
			this.txtRadius.setEnabled(false);
			this.btnBoulderDim.setEnabled(false);							
			
			// Resize it to match the zoom level and given default width, heights.
			rescalePlat();
		}										
		
		// Update screen.
		this.repaint();
	}
	
	public void rescalePlat() {		
		// Rescale the loaded image.
		if (this.image != null) {
			System.out.println(this.mToPixel + ", " + this.zoomLevel + ", " + this.imgWidth);
			this.rescaledImage = new ImageIcon(this.image.getScaledInstance((int)(this.imgWidth * this.mToPixel * this.zoomLevel), 
					(int)(this.imgHeight * this.mToPixel * this.zoomLevel), java.awt.Image.SCALE_SMOOTH));
		}
		
		// Redraw.
		this.repaint();
	}
	
	public void rescaleBoulder() {
		if (this.image != null) {
			this.rescaledImage = new ImageIcon(this.image.getScaledInstance((int)(this.imgWidth * this.mToPixel * this.zoomLevel), 
					(int)(this.imgHeight * this.mToPixel * this.zoomLevel), java.awt.Image.SCALE_SMOOTH));
		}
		
		// Redraw.
		this.repaint();
	}
	
	public void outputJSON() {
		PropertyBook book = new PropertyBook();
		
		FileWriter file;
		book.getDoubList().put("widthm", this.imgWidth);
		book.getDoubList().put("heightm", this.imgHeight);
		book.getDoubList().put("zoomLevel", this.zoomLevel);
		
		// Change the points to cocos points
		ArrayList<Point2D.Double> cocosPoints = swingToCocos();
		
		cocosPoints.forEach((cocosP) -> {
			book.getCollPoints().add(cocosP);
		});
		
		this.edgeFrics.forEach((f) -> book.getEdgeFrictions().add(f));
		// Make sure the last friction (between point n and point 1) is the current friction.
		book.getEdgeFrictions().add(currentFriction);
		
		if (this.name.toLowerCase().contains("boulder")) {
			book.getDoubList().put("radius", this.radius);
			book.getDoubList().put("mass", this.mass);
		}
				
		try {
			// Make the GSON writer.
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			// Try writing it out.
			file = new FileWriter(COL_PATH + this.name + ".json");
			String output = gson.toJson(book);
			file.write(output);
			file.flush();
			file.close();
			System.out.println("Output JSON written to " + COL_PATH);
		} catch (IOException e1) {					
			e1.printStackTrace();
		}						
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// Control panel
		JPanel pnlControls = new JPanel();
		pnlControls.setLayout(new GridLayout(9,2));
		pnlControls.setBackground(UIManager.getColor("inactiveCaption"));
		contentPane.add(pnlControls, BorderLayout.NORTH);
		
		
		KeyEventDispatcher d = new KeyEventDispatcher() {
            @Override
			public boolean dispatchKeyEvent(KeyEvent e) {
            	if (!e.isControlDown()) {
            		return false;
            	}
				switch (e.getKeyCode()) {
				case '1':
					currentFriction = 1.0;
					break;
					
				case '2':
					currentFriction = 0.2;
					break;
					
				case '3':
					currentFriction = 0.3;
					break;
					
				case '4':
					currentFriction = 0.4;
					break;
					
				case '5':
					currentFriction = 0.5;
					break;
					
				case '6':
					currentFriction = 0.6;
					break;
					
				case '7':
					currentFriction = 0.7;
					break;
					
				case '8':
					currentFriction = 0.8;
					break;
					
				case '9':
					currentFriction = 0.9;
					break;
					
				case '0':
					currentFriction = 0.0;
					break;
					
				default:
					return false;
				}
				lblFrictionVal.setText(Double.toString(currentFriction));
				pnlControls.repaint();
				return true;
			}
		};
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(d);
		
		txtName = new JTextField();
		txtName.setText("Name");
		pnlControls.add(txtName);
		txtName.setColumns(10);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				readJSON();
			}
		});
		pnlControls.add(btnLoad);
		
		JSlider slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setValue(100);
		slider.setMajorTickSpacing(25);
		slider.setMaximum(200);
		slider.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		        int value = slider.getValue();			        
		        zoomLevel = (value == 0) ? 0.05 : value / 100.0;		       
		        rescalePlat();		        
		      }
		    });
		pnlControls.add(slider);
		
		btnPlatDim = new JButton("Change dimensions");
		btnPlatDim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Double pwidth = Double.parseDouble(txtWidth.getText());
				if (pwidth != null) {
					imgWidth = pwidth.doubleValue();
				}
				
				Double pheight = Double.parseDouble(txtHeight.getText());
				if (pheight != null) {
					imgHeight = pheight.doubleValue();
				}
				
				rescalePlat();
				
			}
		});
		pnlControls.add(btnPlatDim);
		
		lblWidth = new JLabel("Width:");
		pnlControls.add(lblWidth);
		
		txtWidth = new JTextField();
		txtWidth.setText("3");
		pnlControls.add(txtWidth);
		txtWidth.setColumns(10);
		
		lblHeight = new JLabel("Height:");
		pnlControls.add(lblHeight);
		
		txtHeight = new JTextField();
		txtHeight.setText("3");
		pnlControls.add(txtHeight);
		txtHeight.setColumns(10);
		
		lblFriction = new JLabel("Friciton");
		lblFrictionVal = new JLabel("0");
		pnlControls.add(lblFriction);
		pnlControls.add(lblFrictionVal);
		
		lblMass = new JLabel("Mass:");
		pnlControls.add(lblMass);
		
		txtMass = new JTextField();
		txtMass.setText("1000");
		pnlControls.add(txtMass);
		txtMass.setColumns(10);
		txtMass.setEnabled(false);
		
		lblRadius = new JLabel("Radius:");
		pnlControls.add(lblRadius);
		
		txtRadius = new JTextField();		
		txtRadius.setText("3");
		pnlControls.add(txtRadius);
		txtRadius.setColumns(10);		
		txtRadius.setEnabled(false);
		
		JButton btnOutput = new JButton("Change!");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputJSON();
			}
		});
		
		JButton btnClear = new JButton("Clear Points");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove all made points and edge values.
				points.clear();
				edgeFrics.clear();
				pnlContent.repaint();
			}
		});
		
		btnBoulderDim = new JButton("Change properties");
		btnBoulderDim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Double pmass = Double.parseDouble(txtMass.getText());
				if (pmass != null) {
					mass = pmass.doubleValue();
				}
				
				Double pradius = Double.parseDouble(txtRadius.getText());
				if (pradius != null) {
					radius = pradius.doubleValue();
				}
				
				rescaleBoulder();
				
			}
		});
		pnlControls.add(btnBoulderDim);
		pnlControls.add(btnClear);
		pnlControls.add(btnOutput);
		btnBoulderDim.setEnabled(false);
		
		JButton btnCenter = new JButton("Center");
		btnCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vpOffset = new Point2D.Double(0,0);
				changeOffset(0,0);
			}
		});
		pnlControls.add(btnCenter);
		
		// Display the image to make points of
		JPanel pnlDisplay = new JPanel();	
		pnlDisplay.setBackground(UIManager.getColor("Button.background"));
		pnlDisplay.setLayout(new BorderLayout(0,0));
		contentPane.add(pnlDisplay, BorderLayout.CENTER);		
		
		JPanel pnlNorth = new JPanel();
		pnlDisplay.add(pnlNorth, BorderLayout.NORTH);
		
		JButton btnNorth = new JButton("");
		btnNorth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Go up in swing meters
				changeOffset(0, offset);
			}
		});
		ImageIcon iiNorth = new ImageIcon(ASSETS_PATH + "arNorth.png");
		btnNorth.setIcon(iiNorth);
		pnlNorth.add(btnNorth);
		
		JPanel pnlSouth = new JPanel();
		pnlDisplay.add(pnlSouth, BorderLayout.SOUTH);
		
		JButton btnSouth = new JButton("");
		btnSouth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go down in swing meters
				changeOffset(0, -1 * offset);
			}
		});
		ImageIcon iiSouth = new ImageIcon(ASSETS_PATH + "arSouth.png");
		btnSouth.setIcon(iiSouth);
		pnlSouth.add(btnSouth);
		
		JPanel pnlWest = new JPanel();
		pnlDisplay.add(pnlWest, BorderLayout.WEST);
		
		JButton btnWest = new JButton("");
		btnWest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go left in swing meters
				changeOffset(offset, 0);
			}
		});
		ImageIcon iiWest = new ImageIcon(ASSETS_PATH + "arWest.png");
		btnWest.setIcon(iiWest);
		pnlWest.add(btnWest);
		
		JPanel pnlEast = new JPanel();
		pnlDisplay.add(pnlEast, BorderLayout.EAST);
		
		JButton btnEast = new JButton("");
		btnEast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go right in swing meters
				changeOffset(-1 * offset, 0);
			}
		});
		ImageIcon iiEast = new ImageIcon(ASSETS_PATH + "arEast.png");
		btnEast.setIcon(iiEast);
		pnlEast.add(btnEast);
		
		pnlContent = new JPanel() {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 242174451501027598L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				// Draw image on the screen
				if (rescaledImage != null) {
					Point2D.Double vpc = getViewportCoordinates(0,0);					
					g.drawImage(rescaledImage.getImage(), (int)vpc.x, (int)vpc.y, null);
				}
				
				// Draw points on screen
				int[] iter = {1};
				points.forEach((point) -> {
					// Translate point into vpPoint.					
					Point2D.Double vpPoint = new Point2D.Double(point.getX() * zoomLevel + vpOffset.x,
							point.getY() * zoomLevel + vpOffset.y);
					
					int circleDiameter = 15;
					// Draw point
					g.setColor(Color.MAGENTA);
					// First two coordinates at the upper left of the oval (circle really).
					g.fillOval((int)vpPoint.getX() - circleDiameter/2, (int)vpPoint.getY() - circleDiameter/2, 
							circleDiameter, circleDiameter);
					// Label point
					g.setColor(Color.BLACK);
					g.drawString(Integer.toString(iter[0]), (int)vpPoint.getX() - 3, (int)vpPoint.getY() + 5);
					iter[0]++;
				});
			}
		};		
		pnlDisplay.add(pnlContent, BorderLayout.CENTER);
		
		pnlContent.addMouseListener(new MouseListener() {
		    @Override
			public void mouseClicked(MouseEvent e) {
			    int xp = e.getX();
			    int yp = e.getY();
			    points.add(new Point2D.Double((xp - vpOffset.x)/zoomLevel, (yp - vpOffset.y)/zoomLevel));
			    if (points.size() > 1) {
			    	edgeFrics.add(currentFriction);
			    }
			    pnlContent.repaint();
		    }

			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
	}	
	
	/**
	 * For rendering purposes. Offsets the real world coordinates by the viewport offset.
	 * @param x real world swing coordinate
	 * @param y real world swing coordinate
	 * @return (x,y) viewport swing coordinates
	 */
	public Point2D.Double getViewportCoordinates(double x, double y) {
		return new Point2D.Double(x + this.vpOffset.getX(), y + this.vpOffset.getY());
	}
	

	public ArrayList<Point2D.Double> cocosToSwing(ArrayList<Point2D.Double> cocosPoints ) {		
		ArrayList<Point2D.Double> correctedPoints = new ArrayList<Point2D.Double>();
		Point2D.Double center = new Point2D.Double(this.imgWidth * this.mToPixel/2, this.imgHeight * this.mToPixel/2);
		cocosPoints.forEach((cocosP) -> {
			double sx = (center.x + cocosP.x * this.mToPixel);
			double sy = (center.y - cocosP.y * this.mToPixel);
			correctedPoints.add(new Point2D.Double(sx, sy));
		});
		return correctedPoints;
	}
	
	public ArrayList<Point2D.Double> swingToCocos() {
		ArrayList<Point2D.Double> correctedPoints = new ArrayList<Point2D.Double>();
		
		Point2D.Double center = new Point2D.Double(this.imgWidth * this.mToPixel/2, this.imgHeight * this.mToPixel/2);
		System.out.println("Center point at " + center.x + ", " + center.y);
		
		// The center of the image is treated as (0,0). Do in terms of mToPixel.		
		this.points.forEach((point) -> {
			System.out.println("Swing point: " + point.x + ", " + point.y);
			double cX = (point.x - center.x)/this.mToPixel;
			double cY = (center.y - point.y)/this.mToPixel;
			System.out.println("Cocos point: " + cX + ", " + cY);
			correctedPoints.add(new Point2D.Double(cX, cY));
		});
		return correctedPoints;
	}
	
	public void changeOffset(double xm, double ym) {		
		this.vpOffset = new Point2D.Double(this.vpOffset.getX() + xm * this.mToPixel, 
				this.vpOffset.getY() + ym * this.mToPixel);
		this.repaint();
	}
		
	public void start() {
		setVisible(true);
	}
	
	/**
	 * Run the collision editor.
	 * @param args arguments to the program
	 */
	public static void main(String[] args) {
		// Java specs say that the system must be constructed on the GUI event thread.
		EventQueue.invokeLater(new Runnable() { 
			public void run() {
				try {
					CollisionWindow window = new CollisionWindow();
					window.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
