package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
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
	private String newPath;
	private JTextField txtInputPath;

	/**
	 * Create the frame.
	 */
	public OutputWindow(IOutputToLevelAdapter otlAdapter) {	
		this.makeNew = false;
		this.editOld = false;
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
				    
				} else if (editOld) {
					int xp = e.getX();
				    int yp = e.getY();
				    System.out.println("Requesting center point " + xp + ", " + yp + "; pixels.");
				    // TODO: Put back in when LayerWindow is up and working.
					// otlAdapter.editCenter(xp, yp);
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
		
		
		getContentPane().add(pnlContent, BorderLayout.CENTER);
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
		this.newPath = path;
	}
	
	public void setLevelName(String name) {
		lblCurLevel.setText(name);
	}

}
