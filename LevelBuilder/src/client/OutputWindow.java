package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;

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

/**
 * One of the three main windows; OutputWindow is the window on which the level is drawn.
 * It also has some level-saving/level-outputting logic and lets one look around the level.
 * 
 * @author melindacrane
 */
public class OutputWindow extends JFrame {

	private JPanel contentPane;
	private JPanel pnlContent;
	private IOutputToLevelAdapter otlAdapter;
	private JTextField txtJsonOutputPath;
	private JTextField txtOutputPath;
	private JLabel lblCurLevel;
	
	public enum Request {
		NONE, 
		MAKE_PLATFORM, MAKE_VINE, MAKE_BOULDER, MAKE_NPC, MAKE_BOULDER_JOINT, MAKE_PEG,
		EDIT_OLD_PLAT, EDIT_OLD_VINE, EDIT_OLD_BOULDER, EDIT_OLD_PEG,
		SET_PLAYER_START_POS, SET_PLAT_ENDPOINT, 
		MARK_EOL, MARK_RP,
		REMOVE_RP
	}
	private Request request;
	
	private String newPath;
	private int ticket;
	private JTextField txtInputPath;
	
	private double offset;
	private JTextField txtNextLevel;

	/**
	 * Create the frame.
	 */
	public OutputWindow(IOutputToLevelAdapter otlAdapter) {
		this.offset = 1.5;
		this.ticket = 0;
		this.request = Request.NONE;
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
		setBounds(350, 50, 600, 600);
		getContentPane().setLayout(new BorderLayout());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		lblCurLevel = new JLabel("<html><b><u>New Level</u></b></html>");
		contentPane.add(lblCurLevel, BorderLayout.SOUTH);
		
		JPanel pnlControls = new JPanel();
		pnlControls.setLayout(new GridLayout(3,3));
		getContentPane().add(pnlControls, BorderLayout.NORTH);
		
		JLabel lblOutputPath = new JLabel("LevelName");
		pnlControls.add(lblOutputPath);
		
		txtOutputPath = new JTextField();
		txtOutputPath.setText("level");
		pnlControls.add(txtOutputPath);
		txtOutputPath.setColumns(10);
		
		JButton btnMakeJSON = new JButton("Make");
		btnMakeJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Try to write out the JSON file.
				 */
				String fullPath;
				String levelName;
				if (txtOutputPath.getText().contains(".")) {
					// They added their own extension, don't add json to the end.
					fullPath = "../levelFiles/" + txtOutputPath.getText();
					levelName = txtOutputPath.getText().substring(0, txtOutputPath.getText().indexOf('.'));
				} else {
					fullPath = "../levelFiles/" + txtOutputPath.getText() + ".json";
					levelName = txtOutputPath.getText();
				}
				FileWriter file;
				try {
					file = new FileWriter(fullPath);
					// Send over the name of the level, so everything before the '.' in the extension.
					file.write(otlAdapter.makeJSON(
					        levelName,
							txtNextLevel.getText()).toJSONString());
					file.flush();
					file.close();
					System.out.println("Output JSON written to levelFiles folder.");
				} catch (IOException e1) {					
					e1.printStackTrace();
				}			
			}
		});
		pnlControls.add(btnMakeJSON);
		
		JLabel lblNextName = new JLabel("NextLevelName");
		pnlControls.add(lblNextName);
		
		txtNextLevel = new JTextField();
		txtNextLevel.setText("nextLevel");
		pnlControls.add(txtNextLevel);
		txtNextLevel.setColumns(10);
		
		JLabel lblBlank = new JLabel("");
		pnlControls.add(lblBlank);
		
		JLabel lblLoadPath = new JLabel("Load LevelName");
		pnlControls.add(lblLoadPath);
		
		txtInputPath = new JTextField();
		txtInputPath.setText("nextLevel");
		pnlControls.add(txtInputPath);
		txtInputPath.setColumns(10);
		JButton btnReadJSON = new JButton("Load Level");
		btnReadJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fullPath;
				String levelName;
				if (txtOutputPath.getText().contains(".")) {
					// They added their own extension, don't add json to the end.
					fullPath = "../levelFiles/" + txtInputPath.getText();
				} else {
					fullPath = "../levelFiles/" + txtInputPath.getText() + ".json";
				}
				otlAdapter.readJSON(fullPath);
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
		ImageIcon iiWest = new ImageIcon("../assets/arWest.png");
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
		ImageIcon iiEast = new ImageIcon("../assets/arEast.png");
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
		ImageIcon iiNorth = new ImageIcon("../assets/arNorth.png");
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
		ImageIcon iiSouth = new ImageIcon("../assets/arSouth.png");
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
		    	switch (request) {
		    	case MAKE_PLATFORM: { 
				    int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; swing vp pixels.");
				    
				    // Pop up dialog here to get the expected width in meters
				    String arString = JOptionPane.showInputDialog(null, "Input custom scale ratio. Default will be used otherwise.");
				    double arDouble;
				    if (arString.isEmpty()) {
				    	arDouble = 1.0;
				    } else {
				        try {
				    	    arDouble = Double.parseDouble(arString);
				        } catch (NullPointerException nulle) {
				    	    // Default to 1:1.
				    	    arDouble = 1;
				        } catch (NumberFormatException numberEx) {
				    	    System.out.println("Not a valid number.");
				    	    arDouble = 1;
				    	    numberEx.printStackTrace();
				        }	
				    }
				    				   
				    // Pop up dialog box to make collision box.				    
				    otlAdapter.makePlatform(newPath, xp, yp, arDouble);
				    
				    request = Request.NONE;
					break;
				    
				}
		    	case MAKE_VINE: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; swing vp pixels.");
				    
				    // Pop up dialog here to get the expected width in meters
				    String width = JOptionPane.showInputDialog(null, "Input width of the vine (meters)");
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
				    String height = JOptionPane.showInputDialog(null, "Input height of the vine (meters)");
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
				    
				    String arcl = JOptionPane.showInputDialog(null, "Input arclength (degrees)");
				    double arcld;
				    try {
				    	arcld = Double.parseDouble(arcl);
				    } catch (NullPointerException nulle) {
				    	// Default to 2m width.
				    	arcld = 180;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	arcld = 180;
				    	numbe.printStackTrace();
				    }				    
				    
				    String startVell = JOptionPane.showInputDialog(null, "Input starting velocity");
				    double startVel;
				    try {
				    	startVel = Double.parseDouble(startVell);
				    } catch (NullPointerException nulle) {				    	
				    	startVel = 0;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	startVel = 0;
				    	numbe.printStackTrace();
				    }				
				    
				    // Pop up dialog box to make collision box.				    
				    otlAdapter.makeVine(newPath, xp, yp, wm, hm, arcld, startVel);
		    		
		    		request = Request.NONE;
					break;
		    	}
		    	case MAKE_PEG: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; swing vp pixels.");
				    
				    // Pop up dialog here to get the expected width in meters
				    String arString = JOptionPane.showInputDialog(null, "Input custom scale ratio. Default will be used otherwise.");
				    double arDouble;
				    if (arString.isEmpty()) {
				    	arDouble = 1.0;
				    } else {
				        try {
				    	    arDouble = Double.parseDouble(arString);
				        } catch (NullPointerException nulle) {
				    	    // Default to 1:1.
				    	    arDouble = 1;
				        } catch (NumberFormatException numbe) {
				    	    System.out.println("Not a valid number.");
				    	    arDouble = 1;
				    	    numbe.printStackTrace();
				        }
				    }
				    
				    String radString = JOptionPane.showInputDialog(null, "Input rotation in radians.");
				    double radDouble;
				    if (radString.isEmpty()) {
				    	radDouble = 0.0;
				    } else {
				        try {
				        	radDouble = Double.parseDouble(radString);
				        } catch (NullPointerException nulle) {
				        	radDouble = 0;
				        } catch (NumberFormatException numbe) {
				    	    System.out.println("Not a valid number.");
				    	    radDouble = 0;
				    	    numbe.printStackTrace();
				        }
				    }
				    
				    String jidString = JOptionPane.showInputDialog(null, "Input jointID.");
				    int jid;
				    if (jidString.isEmpty()) {
				    	jid = 0;
				    } else {
				        try {
				        	jid = Integer.parseInt(jidString);
				        } catch (NullPointerException nulle) {
				        	jid = 0;
				        } catch (NumberFormatException numbe) {
				    	    System.out.println("Not a valid number.");
				    	    jid = 0;
				    	    numbe.printStackTrace();
				        }
				    }
				    				   
				    // Pop up dialog box to make collision box.				    
				    otlAdapter.makePeg(newPath, xp, yp, radDouble, jid, arDouble);
				    
				    request = Request.NONE;
					break;
		    	}
		    	case MAKE_BOULDER: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; swing vp pixels.");
				    
				    // Pop up dialog here to get the expected width in meters
				    String arString = JOptionPane.showInputDialog(null, "Input custom scale ratio. Default will be used otherwise.");
				    double arDouble;
				    if (arString.isEmpty()) {
				    	arDouble = 1.0;
				    } else {
				        try {
				    	    arDouble = Double.parseDouble(arString);
				        } catch (NullPointerException nulle) {
				    	    // Default to 1:1.
				    	    arDouble = 1;
				        } catch (NumberFormatException numbe) {
				    	    System.out.println("Not a valid number.");
				    	    arDouble = 1;
				    	    numbe.printStackTrace();
				        }
				    }
				    				   
				    // Pop up dialog box to make collision box.				    
				    otlAdapter.makeBoulder(newPath, xp, yp, arDouble, -1);
				    
				    request = Request.NONE;
					break;
		    	}
		    	case MAKE_BOULDER_JOINT: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting joint point " + xp + ", " + yp + "; swing vp pixels.");
				    
				    // Ask for the first boulder #.	   
				    
			    	String boulder1 = JOptionPane.showInputDialog(null, "Input ticket # of boulder 1.");
				    int ticket1;	 
				    try {
				    	ticket1 = Integer.parseInt(boulder1);
				    } catch (NullPointerException nulle) {
				    	// Default to 2m width.
				    	ticket1 = -1;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	ticket1 = -1;
				    	numbe.printStackTrace();
				    }				    
				    
				    // Ask for the second boulder #.
				    int ticket2 = -1;
			    	String boulder2 = JOptionPane.showInputDialog(null, "Input ticket # of boulder 2.");
			    	 
				    try {
				    	ticket2 = Integer.parseInt(boulder2);
				    } catch (NullPointerException nulle) {
				    	// Default to 2m width.
				    	ticket2 = -1;
						nulle.printStackTrace();
				    } catch (NumberFormatException numbe) {
				    	System.out.println("Not a valid number.");
				    	ticket2 = -1;
				    	numbe.printStackTrace();
				    }			
				    
				    otlAdapter.makeBoulderJoint(ticket1, ticket2, xp, yp);
				    
				    request = Request.NONE;
					break;
		    	}
		    	case EDIT_OLD_PLAT: {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
					otlAdapter.editPlatCenter(ticket, xp, yp);
					
					request = Request.NONE;
					break;
				    
				} 
		    	case EDIT_OLD_VINE: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
					otlAdapter.editVineCenter(ticket, xp, yp);
					
					request = Request.NONE;
					break;				    
		    	}
		    	case EDIT_OLD_PEG: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
					otlAdapter.editPegCenter(ticket, xp, yp);
					
					request = Request.NONE;
					break;
		    	}
		    	case EDIT_OLD_BOULDER: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
					otlAdapter.editBoulderCenter(ticket, xp, yp);
					
					request = Request.NONE;
					break;
		    	}
		    	case SET_PLAYER_START_POS: {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting starting point " + xp + ", " + yp + "; pixels.");
				    
				    // Send position to level manager
				    otlAdapter.setPlayerPosition(newPath, xp, yp);
				    
				    request = Request.NONE;
					break;
					
				} 
		    	case MARK_EOL: {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("EOL point to be at " + xp + ", " + yp + "; pixels.");
				    
				    // Send position to level manager
				    otlAdapter.setEOL(xp, yp);
				    
				    request = Request.NONE;
					break;
				} 
		    	case MARK_RP: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("RP point to be at " + xp + ", " + yp + "; pixels.");
				    
				    // Send position to level manager
				    otlAdapter.setRP(xp, yp);
				    
				    request = Request.NONE;
					break;
		    	}
		    	case REMOVE_RP: {
		    		int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Estimated removal point at " + xp + ", " + yp + "; pixels.");
				    
				    // Send position to level manager
				    otlAdapter.removeRP(xp, yp);
				    
				    request = Request.NONE;
					break;
		    	}
		    	case SET_PLAT_ENDPOINT: {
					int xp = e.getX();
				    int yp = e.getY();				    
				    
				    // Send position to level manager
				    otlAdapter.setEndpointPlat(ticket, xp, yp);
				    
				    request = Request.NONE;
					break;
				}
		    	default:
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
		request = Request.MAKE_PLATFORM;
		this.newPath = path;
	}
	
	public void setCharPos(String name) {		
		// Ask for the position of the character.
		System.out.println("Requesting new position for " + name);		
		request = Request.SET_PLAYER_START_POS;
		this.newPath = name;
		
	}
	
	public void setLevelName(String name) {
		lblCurLevel.setText(name);
		txtOutputPath.setText(name);
	}
	
	public void setNextName(String name) {
		txtNextLevel.setText(name);
		txtInputPath.setText(name);
	}
	
	/**
	 * LevelManager requests ticket to have a new position.
	 * @param ticket identifier of the platform
	 */
	public void setPlatPos(int ticket) {
		this.ticket = ticket;		
		request = Request.EDIT_OLD_PLAT;		
	}
	
	public void setVinePos(int ticket) {
		this.ticket = ticket;		
		request = Request.EDIT_OLD_VINE;		
	}
	
	public void setBoulderPos(int ticket) {
		this.ticket = ticket;
		request = Request.EDIT_OLD_BOULDER;
	}
	
	public void setPegPos(int ticket) {
		this.ticket = ticket;
		request = Request.EDIT_OLD_PEG;
	}

	public void markEOL() {
		request = Request.MARK_EOL;
	}
	
	public void markRP() {
		request = Request.MARK_RP;
	}
	
	public void makeVine(String path) {
		request = Request.MAKE_VINE;
		this.newPath = path;
	}
	
	public void makePeg(String path) {
		request = Request.MAKE_PEG;
		this.newPath = path;
	}
	
	public void makeBoulder(String path) {
		request =  Request.MAKE_BOULDER;
		this.newPath = path;
	}
	
	public void makeNPC(String path) {
		request = Request.MAKE_NPC;
		this.newPath = path;
	}
	
	/**
	 * Request an endpoint for a moving platform.
	 * @param ticket
	 */
	public void setEndpointPlat(int ticket) {
		this.ticket = ticket;
		request = Request.SET_PLAT_ENDPOINT;
	}
	
	public void removeRP() {
		request = Request.REMOVE_RP;
	}
	
	public void makeBoulderJoint() {
		request = Request.MAKE_BOULDER_JOINT;
	}
}
