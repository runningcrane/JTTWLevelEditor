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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class LayerWindow extends JFrame {

	private JPanel contentPane;
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
				setLayout(new GridLayout(3, 1, 0, 0));
				
				JButton btnMove = new JButton("New center");
				btnMove.setPreferredSize(new Dimension(100, 25));
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ltlAdapter.editPlatCenter(ticket);
					}
				});
				
				JLabel lblTicket = new JLabel("Ticket #");
				add(lblTicket);
				add(btnMove);
				
				JButton btnCollision = new JButton("Edit collision");
				btnCollision.setPreferredSize(new Dimension(100, 25));
				btnCollision.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.editPlatCollisionBox(ticket);
					}
				});		
				add(btnCollision);
				
				JLabel lblWidth = new JLabel("Width (m):");
				add(lblWidth);
				
				JTextField txtWidth = new JTextField();
				txtWidth.setText(Double.toString(initWM));
				JTextField txtHeight = new JTextField();
				txtHeight.setText(Double.toString(initHM));
				add(txtWidth);
				txtWidth.setColumns(10);
				
				JButton btnDimensions = new JButton("Change dimensions");
				btnDimensions.setPreferredSize(new Dimension(100, 25));
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
				add(btnDimensions);
				
				JLabel lblNewLabel = new JLabel("Height (m):");
				add(lblNewLabel);
				
				add(txtHeight);
				txtHeight.setColumns(10);
				
				JButton btnDelete = new JButton("Remove");
				btnDelete.setPreferredSize(new Dimension(100, 25));
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removePlat(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);					
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				add(btnDelete);						
			}
		};
		
		contentPane.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		contentPane.add(jsep);
		
		// Resize the frame.
		this.pack();
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);	
	}
	
	public void start() {
		setTitle("Edit Objects");
		setSize(200, 550);
		setVisible(true);
	}
	
	public void removeEditWindow(int ticket, JSeparator jsep) {
		contentPane.remove(this.edits.get(ticket));
		contentPane.remove(jsep);
		
		// Refresh the layer.
		contentPane.repaint();
	}

}
