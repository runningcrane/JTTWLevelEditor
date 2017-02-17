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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
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
	
	public void addPlatformEdit(int ticket, double wm, double hm) {
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
				
				JButton btnCollision = new JButton("Edit collision");
				pnlPosition.add(btnCollision);
				
				JLabel lblWidth = new JLabel("Width (m):");
				pnlPosition.add(lblWidth);
				
				txtWidth = new JTextField(Double.toString(wm));
				pnlPosition.add(txtWidth);
				txtWidth.setColumns(10);
				
				JButton btnDimensions = new JButton("Change dimensions");
				pnlPosition.add(btnDimensions);
				
				JLabel lblHeight = new JLabel("Height (m):");
				pnlPosition.add(lblHeight);
				
				txtHeight = new JTextField(Double.toString(hm));
				pnlPosition.add(txtHeight);
				txtHeight.setColumns(10);
				
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
				txtVelocity.setColumns(10);
				
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
				txtSCK.setColumns(10);
				
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
					    ltlAdapter.changeDimPlat(ticket, wm, hm);
					}
				});
				
				btnCollision.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.editPlatCollisionBox(ticket);
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
	
	public void setSinkable(int ticket, boolean selected) {
		this.edits.get(ticket).chckbxSinkable.setSelected(selected);
	}
	
	public void setVelocity(int ticket, double velocity) {
		this.edits.get(ticket).txtVelocity.setText(Double.toString(velocity));
	}
	
	public void setSCK(int ticket, double scK) {
		this.edits.get(ticket).txtSCK.setText(Double.toString(scK));
	}
	
	public void setDimensions(int ticket, double wm, double hm) {
		this.edits.get(ticket).txtWidth.setText(Double.toString(wm));
		this.edits.get(ticket).txtHeight.setText(Double.toString(hm));
	}
}
