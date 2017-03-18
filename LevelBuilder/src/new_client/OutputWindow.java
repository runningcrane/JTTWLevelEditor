package new_client;

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
		
	private JTextField txtInputPath;
	
	private double offset;
	private JTextField txtNextLevel;

	/**
	 * Create the frame.
	 */
	public OutputWindow(IOutputToLevelAdapter otlAdapter) {
		this.offset = 1.5;
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
			    int xp = e.getX();
			    int yp = e.getY();
		    	otlAdapter.sendCoordinates(xp, yp);
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
}
