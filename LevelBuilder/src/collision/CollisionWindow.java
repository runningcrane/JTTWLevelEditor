package collision;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JLabel;

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
	private JButton btnPlatDim;
	private JButton btnBoulderDim;
		
	String name;
	private double imgWidth;
	private double imgHeight;
	private double zoomLevel;
	private double mass;
	private double radius;
	private ArrayList<Point2D.Double> points;
	
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
		this.points = new ArrayList<Point2D.Double>();
		
		// Viewport stuff
		this.vpOffset = new Point2D.Double(0, 0);
		this.offset = 1.5;				
		
		initGUI();
	}
	
	public void makeDefault(String path) {
		// Check if the image exists.	
		try {
			this.image = ImageIO.read(new File("assets/" + path + ".png"));
		} catch (IOException e) {
			System.err.println("Image not found in assets folder: " + path);
			e.printStackTrace();
			return;
		}
		
		// If the image exists, then make a JSON file for it.
		this.name = path;
		this.outputJSON();
		
		System.out.println("Default file made. Please try again.");
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
		JSONParser parser = new JSONParser();
		Object obj;
		
		String path = txtName.getText();
		try {
			obj = parser.parse(new FileReader("../src/collision/" + path + ".json"));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + "collision/" + path + ".json");
			// e.printStackTrace();
			makeDefault(path);
			return;
		} catch (IOException e) {
			System.out.println("Illegal path: " + "collision/" + path + ".json");
			e.printStackTrace();
			makeDefault(path);
			return;
		} catch (ParseException e) {
			System.out.println("Cannot parse JSON at: " + "collision/" + path + ".json");
			e.printStackTrace();
			makeDefault(path);
			return;
		}
		
		JSONObject object = (JSONObject) obj;
		String name = (String) object.get("name");
		// Check if null.
		if (name == null) {
			name = path;
		}				
		
		Double zoomLevelD = (Double) object.get("zoomLevel");
		double zoomLevel;
		// Check if null.
		if (zoomLevelD == null) {
			zoomLevel = 10;
		} else {
			zoomLevel = zoomLevelD.doubleValue();
		}
		
		Double widthD = (Double) object.get("width");
		double width;
		// Check if null.
		if (widthD == null) {
			width = 3;
		} else {
			width = widthD.doubleValue();
			txtWidth.setText(Double.toString(width));
		}
			
		Double heightD = (Double) object.get("height");
		double height;
		// Check if null.
		if (heightD == null) {
			height = 3;
		} else {
			height = heightD.doubleValue();
			txtHeight.setText(Double.toString(height));
		}
				
		JSONArray jsonPoints = (JSONArray) object.get("points");
		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		// Check if null.
		if (jsonPoints != null) {
			for (Object objPoint : jsonPoints) {			
				JSONObject jsonPoint = (JSONObject) objPoint;
				
				Double xpointD = (Double) jsonPoint.get("x");
				double xpoint;
				// Check if null.
				if (xpointD == null) {
					xpoint = 0;
				} else {
					xpoint = xpointD.doubleValue();
				}
				
				Double ypointD = (Double) jsonPoint.get("y");
				double ypoint;
				// Check if null.
				if (ypointD == null) {
					ypoint = 0;
				} else {
					ypoint = ypointD.doubleValue();
				}								
				
				points.add(new Point2D.Double(xpoint,  ypoint));
			}
		}				
		
		// Set shared fields.
		this.name = name;	
		this.zoomLevel = zoomLevel;
		this.points = cocosToSwing(points); // Translate the points back to swing coordinates. 
		this.imgWidth = width;
		this.imgHeight = height;
		
		// Make the image.		
		try {
			this.image = ImageIO.read(new File("assets/" + path + ".png"));
		} catch (IOException e) {
			System.err.println("File not found: " + "assets/" + path + ".png");
			e.printStackTrace();
			return;
		}
		
		// Show type-specific options.
		if (path.toLowerCase().contains("boulder")) {
			// Set the boulder-related objects visible.
			this.txtMass.setEnabled(true);
			this.txtRadius.setEnabled(true);
			this.btnBoulderDim.setEnabled(true);
			
			Double massD = (Double) object.get("mass");
			double mass;
			// Check if null.
			if (massD == null) {
				mass = 1000;
			} else {
				mass = massD.doubleValue();
				txtMass.setText(Double.toString(mass));
			}
			
			Double radiusD = (Double) object.get("radius");
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
		FileWriter file;
		JSONObject edited = new JSONObject();
		edited.put("width", this.imgWidth);
		edited.put("height", this.imgHeight);
		edited.put("zoomLevel", this.zoomLevel);
		
		// Change the points to cocos points
		ArrayList<Point2D.Double> cocosPoints = swingToCocos();
		
		JSONArray jsonCP = new JSONArray();
		cocosPoints.forEach((cocosP) -> {
			JSONObject indivP = new JSONObject();
			indivP.put("x", cocosP.x);
			indivP.put("y", cocosP.y);
			jsonCP.add(indivP);
		});
		
		edited.put("points", jsonCP);
		
		if (this.name.toLowerCase().contains("boulder")) {
			edited.put("radius", this.radius);
			edited.put("mass", this.mass);
		}
		
		try {
			file = new FileWriter("../src/collision/" + this.name + ".json");
			file.write(edited.toJSONString());
			file.flush();
			file.close();
			System.out.println("Output JSON written to src/collision/");
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
		pnlControls.setLayout(new GridLayout(8,2));
		pnlControls.setBackground(UIManager.getColor("inactiveCaption"));
		contentPane.add(pnlControls, BorderLayout.NORTH);
		
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
				// Remove all made points.
				points.clear();
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
		ImageIcon iiNorth = new ImageIcon("assets/arNorth.png");
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
		ImageIcon iiSouth = new ImageIcon("assets/arSouth.png");
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
		ImageIcon iiWest = new ImageIcon("assets/arWest.png");
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
		ImageIcon iiEast = new ImageIcon("assets/arEast.png");
		btnEast.setIcon(iiEast);
		pnlEast.add(btnEast);
		
		JPanel pnlContent = new JPanel() {
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
			    pnlContent.repaint();
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
