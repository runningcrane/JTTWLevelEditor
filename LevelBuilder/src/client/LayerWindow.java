package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * One of the three main windows. LayerWindow holds the "layers" of the level;
 * namely, EditWindows that can modify their respective object.
 * 
 * @author melindacrane
 */
public class LayerWindow extends JFrame {

	private JPanel contentPane;
	private JPanel pnlBack;	
	private JScrollPane scrPaneScroll;
	private ILayerToLevelAdapter ltlAdapter;
	private HashMap<Integer, EditWindow> edits;
	private double initWM;
	private double initHM;
	
	/**
	 * Create the frame.
	 */
	public LayerWindow(ILayerToLevelAdapter ltlAdapter) {
		this.ltlAdapter = ltlAdapter;
		this.edits = new HashMap<Integer, EditWindow>();
		initGUI();
	}	
	
	public void addJointsEdit(int ticket, int id1, int id2, double obx1, double oby1, double obx2, double oby2) {
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);

		EditWindow newWindow = new EditWindow(ticket) {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 8116441836763125578L;

			@Override
			public void initGUI() {	
				this.cTicket1 = id1;
				this.cTicket2 = id2;
				
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				JPanel pnlPosition = new JPanel();
				pnlPosition.setLayout(new GridLayout(5,3,0,0));
				add(pnlPosition);
								
				JLabel lblTicket = new JLabel("<html><b><u>#" + ticket + ": joints " + id1 + ", " + id2 + "</u></b></html>");
				pnlPosition.add(lblTicket);
				
				JButton btnChangeBoulder = new JButton("Change boulders");
				pnlPosition.add(btnChangeBoulder);
				
				JButton btnChangeOffset = new JButton("Change offset");
				pnlPosition.add(btnChangeOffset);
				
				JButton btnDelete = new JButton("Remove");
				pnlPosition.add(btnDelete);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removeJoint(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);	
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				
				JLabel lblB1 = new JLabel("Boulder1 #:");
				pnlPosition.add(lblB1);
				
				txtB1 = new JTextField(Integer.toString(id1));
				pnlPosition.add(txtB1);
				txtB1.setColumns(7);
				
				JLabel lblB2 = new JLabel("Boulder2 #:");
				pnlPosition.add(lblB2);
				
				txtB2 = new JTextField(Integer.toString(id2));
				pnlPosition.add(txtB2);
				txtB2.setColumns(7);							
				
				JLabel lblOBX1 = new JLabel("Boulder1X:");
				pnlPosition.add(lblOBX1);
				
				txtOBX1 = new JTextField(Double.toString(obx1));
				pnlPosition.add(txtOBX1);
				txtOBX1.setColumns(7);
				
				JLabel lblOBY1 = new JLabel("Boulder1Y:");
				pnlPosition.add(lblOBY1);
				
				txtOBY1 = new JTextField(Double.toString(oby1));
				pnlPosition.add(txtOBY1);
				txtOBY1.setColumns(7);
				
				JLabel lblOBX2 = new JLabel("Boulder2X:");
				pnlPosition.add(lblOBX2);
				
				txtOBX2 = new JTextField(Double.toString(obx2));
				pnlPosition.add(txtOBX2);
				txtOBX2.setColumns(7);
				
				JLabel lblOBY2 = new JLabel("Boulder2Y:");
				pnlPosition.add(lblOBY2);
				
				txtOBY2 = new JTextField(Double.toString(oby2));
				pnlPosition.add(txtOBY2);
				txtOBY2.setColumns(7);				
				
				btnChangeBoulder.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int b1;
						int b2;
						
					    try {
					    	b1 = Integer.parseInt(txtB1.getText());
					    } catch (NullPointerException nulle) {
					    	// Default to 2m width.
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    try {
					    	b2 = Integer.parseInt(txtB2.getText());					    	
					    } catch (NullPointerException nulle) {
					    	// Default to 2m width.
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    // Valid numbers. Go resize over in LevelManager.
					    ltlAdapter.changeBoulderJoint(ticket, b1, b2);
					    
					    // Update the new joints before change.
					    cTicket1 = b1;
					    cTicket2 = b2;
					}
				});
				
				btnChangeOffset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double obx1;
						double obx2;
						double oby1;
						double oby2;
						
					    try {
					    	obx1 = Double.parseDouble(txtOBX1.getText());
					    } catch (NullPointerException nulle) {
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    try {
					    	obx2 = Double.parseDouble(txtOBX2.getText());
					    } catch (NullPointerException nulle) {
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    try {
					    	oby1 = Double.parseDouble(txtOBY1.getText());
					    } catch (NullPointerException nulle) {
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    try {
					    	oby2 = Double.parseDouble(txtOBY2.getText());
					    } catch (NullPointerException nulle) {
							nulle.printStackTrace();
							return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
						ltlAdapter.editBJOff(ticket, obx1, oby1, obx2, oby2);
					}
				});														
			}					
			
			@Override
			public void manualRemove() {
				System.out.println("Removing #" + ticket + "...");
				removeEditWindow(ticket, jsep);
			}
			
		};
		
		pnlBack.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		pnlBack.add(jsep);
		
		// Resize the frame.
        Dimension d = new Dimension(500,300);
        this.scrPaneScroll.setPreferredSize(d);
		this.pack();
	}
	
	public void addVineEdit(int ticket, double wm, double hm, 
			double arcLength, double startVel) {
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);
		this.initWM = wm;
		this.initHM = hm;
		EditWindow newWindow = new EditWindow(ticket) {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 8116441836763125578L;

			@Override
			public void initGUI() {	
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				JPanel pnlPosition = new JPanel();
				pnlPosition.setLayout(new GridLayout(5,3,0,0));
				add(pnlPosition);
				
				
				JLabel lblTicket = new JLabel("<html><b><u>#" + this.ticket + "</u></b></html>");
				pnlPosition.add(lblTicket);
				
				JButton btnMove = new JButton("New center");
				pnlPosition.add(btnMove);
				
				JLabel lblBlank = new JLabel("");
				pnlPosition.add(lblBlank);
				
				JLabel lblArcl = new JLabel("Arclength (deg):");
				pnlPosition.add(lblArcl);
				
				txtArcl = new JTextField(Double.toString(arcLength));
				pnlPosition.add(txtArcl);
				txtArcl.setColumns(7);
				
				JButton btnArcl = new JButton("New arclength");
				pnlPosition.add(btnArcl);
				
				JLabel lblStartVel = new JLabel("Start velocity (m/s^2):");
				pnlPosition.add(lblStartVel);
				
				txtStartVel = new JTextField(Double.toString(startVel));
				pnlPosition.add(txtStartVel);
				txtStartVel.setColumns(7);
				
				JButton btnStartVel = new JButton("New start velocity");
				pnlPosition.add(btnStartVel);
				
				JLabel lblWidth = new JLabel("Width (m):");
				pnlPosition.add(lblWidth);
				
				txtWidth = new JTextField(Double.toString(wm));
				pnlPosition.add(txtWidth);
				txtWidth.setColumns(7);
				
				JButton btnDimensions = new JButton("Change dimensions");
				pnlPosition.add(btnDimensions);
				
				JLabel lblHeight = new JLabel("Height (m):");
				pnlPosition.add(lblHeight);
				
				txtHeight = new JTextField(Double.toString(hm));
				pnlPosition.add(txtHeight);
				txtHeight.setColumns(7);
				
				JButton btnDelete = new JButton("Remove");
				pnlPosition.add(btnDelete);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removeVine(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);	
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				
				btnDimensions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double wm;
						double hm;
						
					    try {
					    	wm = Double.parseDouble(txtWidth.getText());
					    } catch (NullPointerException nulle) {
					    	// Default to 2m width.
					    	wm = 2;
							nulle.printStackTrace();
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	wm = 2;
					    	numbe.printStackTrace();
					    }
					    
					    try {
					    	hm = Double.parseDouble(txtHeight.getText());
					    } catch (NullPointerException nulle) {
					    	// Default to 2m width.
					    	hm = 2;
							nulle.printStackTrace();
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	hm = 2;
					    	numbe.printStackTrace();
					    }
					    
					    // Valid numbers. Go resize over in LevelManager.
					    ltlAdapter.changeDimVine(ticket, wm, hm);
					}
				});
				
				btnArcl.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.editVineArcl(ticket, Double.parseDouble(txtArcl.getText()));
					}
				});
				
				btnStartVel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.editVineStartVel(ticket, Double.parseDouble(txtStartVel.getText()));
					}
				});
				
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ltlAdapter.editVineCenter(ticket);
					}
				});												
			}					
			
			@Override
			public void manualRemove() {
				System.out.println("Removing #" + ticket + "...");
				removeEditWindow(ticket, jsep);
			}
			
		};
		
		pnlBack.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		pnlBack.add(jsep);
		
		// Resize the frame.
        Dimension d = new Dimension(500,300);
        this.scrPaneScroll.setPreferredSize(d);
		this.pack();
	}
	
	public void addBoulderEdit(int ticket, double radius, double mass, double scale) {
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);
		EditWindow newWindow = new EditWindow(ticket) {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 8116441836763125578L;

			@Override
			public void initGUI() {	
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				JPanel pnlPosition = new JPanel();
				pnlPosition.setLayout(new GridLayout(3,3,0,0));
				add(pnlPosition);
				
				
				JLabel lblTicket = new JLabel("<html><b><u>#" + this.ticket + "</u></b></html>");
				pnlPosition.add(lblTicket);
				
				JButton btnMove = new JButton("New center");
				pnlPosition.add(btnMove);
				
				JButton btnDelete = new JButton("Remove");
				pnlPosition.add(btnDelete);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removeBoulder(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);	
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				
				JLabel lblMass = new JLabel("Mass:");
				pnlPosition.add(lblMass);
				
				txtMass = new JTextField(Double.toString(mass));
				pnlPosition.add(txtMass);
				txtMass.setColumns(7);
				
				JButton btnMass = new JButton("Change mass");
				pnlPosition.add(btnMass);	
				
				JLabel lblScale = new JLabel("Scale:");
				pnlPosition.add(lblScale);
				
				txtScale = new JTextField(Double.toString(scale));
				pnlPosition.add(txtScale);
				txtScale.setColumns(7);
				
				JButton btnDimensions = new JButton("Change scale");
				pnlPosition.add(btnDimensions);													
				
				JPanel pnlPolygonal = new JPanel();
				add(pnlPolygonal);
				
				chckbxPolygon = new JCheckBox("Polygon collision");
				chckbxPolygon.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.togglePolygonBoulder(ticket, selected);
				      }
				});
				pnlPolygonal.add(chckbxPolygon);								
				
				btnDimensions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double scale;
						
					    try {
					    	scale = Double.parseDouble(txtScale.getText());
					    } catch (NullPointerException nulle) {					    	
					    	nulle.printStackTrace();
					    	return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    // Valid numbers. Go resize over in LevelManager.
					    ltlAdapter.editBoulderScale(ticket, scale);
					}
				});			
				
				btnMass.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double mass;
						
					    try {
					    	mass = Double.parseDouble(txtMass.getText());
					    } catch (NullPointerException nulle) {					    	
					    	nulle.printStackTrace();
					    	return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    
					    // Valid numbers. Go resize over in LevelManager.
					    ltlAdapter.editBoulderMass(ticket, mass);
					}
				});			
				
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ltlAdapter.editBoulderCenter(ticket);
					}
				});												
			}					
			
			@Override
			public void manualRemove() {
				System.out.println("Removing #" + ticket + "...");
				removeEditWindow(ticket, jsep);
			}
		};
	
		pnlBack.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		pnlBack.add(jsep);
		
		// Resize the frame.
        Dimension d = new Dimension(500,300);
        this.scrPaneScroll.setPreferredSize(d);
		this.pack();
	}
	
	public void addPlatformEdit(int ticket, double wm, double hm, double scale) {
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);
		this.initWM = wm;
		this.initHM = hm;
		EditWindow newWindow = new EditWindow(ticket) {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 8116441836763125578L;

			@Override
			public void initGUI() {	
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				JPanel pnlPosition = new JPanel();
				pnlPosition.setLayout(new GridLayout(3,3,0,0));
				add(pnlPosition);
				
				
				JLabel lblTicket = new JLabel("<html><b><u>#" + this.ticket + "</u></b></html>");
				pnlPosition.add(lblTicket);
				
				JButton btnMove = new JButton("New center");
				pnlPosition.add(btnMove);
				
				JButton btnDelete = new JButton("Remove");
				pnlPosition.add(btnDelete);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removePlat(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);	
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				
				JLabel lblScale = new JLabel("Scale:");
				pnlPosition.add(lblScale);
				
				txtScale = new JTextField(Double.toString(scale));
				pnlPosition.add(txtScale);
				txtScale.setColumns(7);
				
				JButton btnDimensions = new JButton("Change scale");
				pnlPosition.add(btnDimensions);			
				
				slider = new JSlider();
				slider.setValue((int)(scale * 100 - 50));
				slider.setMaximum(350);
				slider.addChangeListener(new ChangeListener() {
				      public void stateChanged(ChangeEvent event) {
				        int value = slider.getValue();					      
				        ltlAdapter.editPlatScale(ticket, (value + 50) / 100.0);		       
				        txtScale.setText(Double.toString((value + 50)/100.0));		        
				      }
				    });
				pnlPosition.add(slider);				
				
				JPanel pnlDisappears = new JPanel();
				add(pnlDisappears);
				
				chckbxDisappears = new JCheckBox("Disappears");
				chckbxDisappears.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.toggleDisappearsPlat(ticket, selected);
				      }
				});
				pnlDisappears.add(chckbxDisappears);
				
				JPanel pnlMoveable = new JPanel();
				pnlMoveable.setLayout(new GridLayout(3,2));
				add(pnlMoveable);
				
				chckbxMoveable = new JCheckBox("Moving");
				chckbxMoveable.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.toggleMoveablePlat(ticket, selected);
				      }
				});
				pnlMoveable.add(chckbxMoveable);
				
				JButton btnEndPoint = new JButton("Set endpoint");
				btnEndPoint.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						// Make the endpoint of the path this platform travels.
						ltlAdapter.makeEndpointPlat(ticket);
					}
				});
				pnlMoveable.add(btnEndPoint);
				
				JLabel lblVelocity = new JLabel("Velocity (m/s):");
				pnlMoveable.add(lblVelocity);
				
				txtVelocity = new JTextField();
				txtVelocity.setText("0.5");
				pnlMoveable.add(txtVelocity);
				txtVelocity.setColumns(7);
				
				JButton btnUpdateVelocity = new JButton("Set velocity");
				btnUpdateVelocity.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double velocity = Double.parseDouble(txtVelocity.getText());
						ltlAdapter.setVelocityPlat(ticket, velocity);
					}
				});
				pnlMoveable.add(btnUpdateVelocity);
				
				JPanel pnlSinkable = new JPanel();
				add(pnlSinkable);
				
				chckbxSinkable = new JCheckBox("Sinkable");
				chckbxSinkable.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.toggleSinkablePlat(ticket, selected);
				      }
				});
				pnlSinkable.add(chckbxSinkable);
				
				JLabel lblscK = new JLabel("Spring constant K:");
				pnlSinkable.add(lblscK);
				
				txtSCK = new JTextField();
				txtSCK.setText("1.0");
				pnlSinkable.add(txtSCK);
				txtSCK.setColumns(7);
				
				JButton btnPhysics = new JButton("Set physics");
				btnPhysics.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double sck = Double.parseDouble(txtSCK.getText());
						ltlAdapter.setPhysicsPlat(ticket, sck);
					}
				});
				pnlSinkable.add(btnPhysics);
				
				JPanel pnlClimbable = new JPanel();
				add(pnlClimbable);
				
				chckbxClimbable = new JCheckBox("Climbable");
				chckbxClimbable.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.toggleClimbablePlat(ticket, selected);
				      }
				});
				pnlClimbable.add(chckbxClimbable);
				
				JPanel pnlCollidable = new JPanel();
				add(pnlCollidable);
				
				chckbxCollidable = new JCheckBox("Collidable");
				chckbxCollidable.addActionListener(new ActionListener() {
				      public void actionPerformed(ActionEvent actionEvent) {
				        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				        boolean selected = abstractButton.getModel().isSelected();
				        
				        // Go update that platform
				        ltlAdapter.toggleCollidablePlat(ticket, selected);
				      }
				});
				pnlCollidable.add(chckbxCollidable);
				
				btnDimensions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double scale;
						
					    try {
					    	scale = Double.parseDouble(txtScale.getText());
					    } catch (NullPointerException nulle) {					    	
					    	nulle.printStackTrace();
					    	return;
					    } catch (NumberFormatException numbe) {
					    	System.out.println("Not a valid number.");
					    	numbe.printStackTrace();
					    	return;
					    }
					    if (scale <= 0) {
					    	slider.setValue(0);
					    } else if (scale >= 4) {
					    	slider.setValue(350);
					    } else {
					    	slider.setValue((int)(scale * 100 - 50));
					    }
					    
					    // Valid numbers. Go resize over in LevelManager.
					    ltlAdapter.editPlatScale(ticket, scale);
					}
				});				
				
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ltlAdapter.editPlatCenter(ticket);
					}
				});												
			}					
			
			@Override
			public void manualRemove() {
				System.out.println("Removing #" + ticket + "...");
				removeEditWindow(ticket, jsep);
			}
			
		};
		
		pnlBack.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		pnlBack.add(jsep);
		
		// Resize the frame.
        Dimension d = new Dimension(500,300);
        this.scrPaneScroll.setPreferredSize(d);
		this.pack();
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(950, 50, 100, 100);
		contentPane = new JPanel();
		pnlBack = new JPanel();
		scrPaneScroll = new JScrollPane(pnlBack);
		scrPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		pnlBack.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBack.setLayout(new BoxLayout(pnlBack, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		contentPane.add(scrPaneScroll);		
	}
	
	public void start() {
		setTitle("Edit Objects");
		setSize(200, 550);
		setVisible(true);
	}
	
	public void removeEditWindow(int ticket, JSeparator jsep) {
		pnlBack.remove(this.edits.get(ticket));
		pnlBack.remove(jsep);
		
		// Remove from the edits list		
		edits.remove(ticket);
		
		// Refresh the layer.
		contentPane.repaint();
	}
	
	public void removeAllWindows() {
		// Call upon the edits to remove themselves and separators
		Integer[] keys = this.edits.keySet().toArray(new Integer[this.edits.size()]);
		for (int i = 0; i < keys.length; i++) {
			EditWindow toRemove = this.edits.get(keys[i]);
			toRemove.manualRemove();
		}
		
		// ...Just to be sure, clear the list. It should be cleared, but just in case. 
		this.edits.clear();
		
		// Refresh the layer.
		contentPane.repaint();
	}

	public void setDisappears(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxDisappears.setSelected(selected);
	}
	
	public void setMoveable(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxMoveable.setSelected(selected);
	}
	
	public void setClimbable(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxClimbable.setSelected(selected);
	}
	
	public void setCollidable(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxCollidable.setSelected(selected);
	}
	
	public void setPolygonBoulder(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxPolygon.setSelected(selected);
	}
	
	public void setSinkable(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxSinkable.setSelected(selected);
	}
	
	public void setVelocity(int ticket, double velocity) {
		this.edits.get(ticket).txtVelocity.setText(Double.toString(velocity));
	}
	
	public void setSCK(int ticket, double scK) {
		this.edits.get(ticket).txtSCK.setText(Double.toString(scK));
	}
	
	public void setDimensions(int ticket, double scale) {
		JTextField txtScale = this.edits.get(ticket).txtScale;		
		if (txtScale != null) {
			txtScale.setText(Double.toString(scale));
		}		
		
		JSlider slide = this.edits.get(ticket).slider;
		if (slide != null) {
			slide.setValue((int)(scale * 100 - 50));
		}
	}
}
