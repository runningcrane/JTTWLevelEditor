package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

import org.json.simple.JSONObject;

//import org.json.simple.JSONObject;

public class OutputWindow extends JFrame {

	private JPanel contentPane;
	private JPanel pnlContent;
	private IOutputToLevelAdapter otlAdapter;
	private JTextField txtJsonOutputPath;
	private JTextField txtOutputPath;
	private JLabel lblCurLevel;
	
	private boolean makeNew;
	private boolean editOld;
	private boolean setPlayerStartPos;
	private String newPath;
	private JTextField txtInputPath;
	
	private double offset;

	/**
	 * Create the frame.
	 */
	public OutputWindow(IOutputToLevelAdapter otlAdapter) {
		this.offset = 1.5;
		this.makeNew = false;
		this.editOld = false;
		this.setPlayerStartPos = false;
		this.otlAdapter = otlAdapter;			
		initGUI();
		
		// Listen for manual resizing of the frame by the user
		this.addComponentListener(new ComponentAdapter() 
		{  
				// Called upon mouse release			
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            otlAdapter.manualResize(c.getSize().getWidth(), c.getSize().getHeight());		            
		        }
		});
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 300);
		getContentPane().setLayout(new BorderLayout());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnlControls = new JPanel();
		getContentPane().add(pnlControls, BorderLayout.NORTH);
		
		JLabel lblOutputPath = new JLabel("JSON Output Path");
		pnlControls.add(lblOutputPath);
		
		txtOutputPath = new JTextField();
		txtOutputPath.setText("level.json");
		pnlControls.add(txtOutputPath);
		txtOutputPath.setColumns(10);
		
		JButton btnMakeJSON = new JButton("Output JSON");
		btnMakeJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Try to write out the JSON file.
				 */
				FileWriter file;
				try {
					file = new FileWriter(txtOutputPath.getText());
					file.write(otlAdapter.makeJSON(txtOutputPath.getText().substring(0, txtOutputPath.getText().length() - 5)).toJSONString());
					file.flush();
					file.close();
					System.out.println("Output JSON written to bin root");
				} catch (IOException e1) {					
					e1.printStackTrace();
				}			
			}
		});
		pnlControls.add(btnMakeJSON);
		
		lblCurLevel = new JLabel("<html><b><u>New Level</u></b></html>");
		pnlControls.add(lblCurLevel);
		
		JLabel lblLoadPath = new JLabel("Level Path");
		pnlControls.add(lblLoadPath);
		
		txtInputPath = new JTextField();
		txtInputPath.setText("level.json");
		pnlControls.add(txtInputPath);
		txtInputPath.setColumns(10);
		JButton btnReadJSON = new JButton("Load Level");
		btnReadJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				otlAdapter.readJSON(txtInputPath.getText());
			}
		});
				
		pnlControls.add(btnReadJSON);
		
		JPanel pnlOutput = new JPanel(new BorderLayout());
		getContentPane().add(pnlOutput, BorderLayout.CENTER);
		
		JPanel pnlWest = new JPanel();
		pnlOutput.add(pnlWest, BorderLayout.WEST);
		
		JButton btnWest = new JButton("");
		btnWest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go left in swing meters
				otlAdapter.changeOffset(offset, 0);
			}
		});
		ImageIcon iiWest = new ImageIcon("assets/arWest.png");
		btnWest.setIcon(iiWest);
		pnlWest.add(btnWest);
		
		JPanel pnlEast = new JPanel();
		pnlOutput.add(pnlEast, BorderLayout.EAST);
		
		JButton btnEast = new JButton("");
		btnEast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go right in swing meters
				otlAdapter.changeOffset(-1 * offset, 0);
			}
		});
		ImageIcon iiEast = new ImageIcon("assets/arEast.png");
		btnEast.setIcon(iiEast);
		pnlEast.add(btnEast);
		
		JPanel pnlNorth = new JPanel();
		pnlOutput.add(pnlNorth, BorderLayout.NORTH);
		
		JButton btnNorth = new JButton("");
		btnNorth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Go up in swing meters
				otlAdapter.changeOffset(0, offset);
			}
		});
		ImageIcon iiNorth = new ImageIcon("assets/arNorth.png");
		btnNorth.setIcon(iiNorth);
		pnlNorth.add(btnNorth);
		
		JPanel pnlSouth = new JPanel();
		pnlOutput.add(pnlSouth, BorderLayout.SOUTH);
		
		JButton btnSouth = new JButton("");
		btnSouth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Go down in swing meters
				otlAdapter.changeOffset(0, -1 * offset);
			}
		});
		ImageIcon iiSouth = new ImageIcon("assets/arSouth.png");
		btnSouth.setIcon(iiSouth);
		pnlSouth.add(btnSouth);
		
		pnlContent = new JPanel() {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 242174451501027598L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				otlAdapter.render(this, g);
			}
		};		
		pnlOutput.add(pnlContent, BorderLayout.CENTER);

		pnlContent.addMouseListener(new MouseListener() {
		    @Override
			public void mouseClicked(MouseEvent e) {
				if (makeNew) {
				    int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
				    
				    // Pop up dialog here to get the expected width in meters
				    String width = JOptionPane.showInputDialog(null, "Input width of the image (meters)");
				    double wm;
				    try {
				    	wm = Double.parseDouble(width);
				    } catch (NullPointerException nulle) {
				    	// Default to 2m width.
				    	wm = 2;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	wm = 2;
				    	numbe.printStackTrace();
				    }
				    
				    // Pop up dialog here to get the expected size in meters
				    String height = JOptionPane.showInputDialog(null, "Input height of the image (meters)");
				    double hm;
				    try {
				    	hm = Double.parseDouble(height);
				    } catch (NullPointerException nulle) {
				    	// Default to 2m width.
				    	hm = 2;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	hm = 2;
				    	numbe.printStackTrace();
				    }				    
				    				   
				    // Pop up dialog box to make collision box.				    
				    otlAdapter.makePlatform(newPath, xp, yp, wm, hm);
				    
				    // No longer need to make a new platform
				    makeNew = false;
				    
				} else if (editOld) {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
				    // TODO: Put back in when LayerWindow is up and working.
					// otlAdapter.editCenter(xp, yp);
				    editOld = false;
				    
				} else if (setPlayerStartPos) {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting starting point " + xp + ", " + yp + "; pixels.");
				    
				    // Send position to level manager
				    otlAdapter.setPlayerPosition(newPath, xp, yp);
				    
					// No longer need to set position
					setPlayerStartPos = false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}		    
		});
	}
	/**
	 * After setting up the frame, make it visible to the user.
	 */
	public void start() {		
		setTitle("GameView");
		setVisible(true);
	}
	
	/**
	 * Resize this panel.
	 * @param wm width in pixels
	 * @param hm height in pixels
	 */
	public void setDimensions(int wm, int hm) {		
		this.setSize(wm, hm);		
	}
		
	
	/**
	 * Redraws the panel with any new/deleted art.
	 */
	public void redraw() {
		pnlContent.repaint();
	}	
	
	public void setActive(String path) {
		this.makeNew = true;
		this.editOld = false;
		this.setPlayerStartPos = false;
		this.newPath = path;
	}
	
	public void setCharPos(String name) {		
		// Ask for the position of the character.
		System.out.println("Requesting new position for " + name);
		this.setPlayerStartPos = true;
		this.makeNew = false;
		this.editOld = false;	
		this.newPath = name;
		
	}
	
	public void setLevelName(String name) {
		lblCurLevel.setText(name);
	}

}