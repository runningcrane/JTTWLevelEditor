package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 * One of the three main windows; this window holds objects to be put into the level
 * and some level logic (dimensions of level, for example).
 * 
 * @author melindacrane
 */
public class ControlWindow extends JFrame {
	
	/**
	 * UID for serialization.
	 */
	private static final long serialVersionUID = 2761055324358420713L;
	
	private JTextField txtLHeight;
	private JTextField txtLWidth;
	private JTextField txtVPHeight;
	private JTextField txtVPWidth;
	private IControlToLevelAdapter ctlAdapter;	
	private JTextField txtMToPixel;

	/**
	 * Create the frame.
	 */
	public ControlWindow(IControlToLevelAdapter ctlAdapter) {
		this.ctlAdapter = ctlAdapter;
		initGUI();
	}
	
	/**
	 * GUI initialization code. Most of this is automatically generated by Swing.
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 300, 700);		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel pnlLevelResize = new JPanel();			
		pnlLevelResize.setLayout(new BoxLayout(pnlLevelResize, BoxLayout.Y_AXIS));
		getContentPane().add(pnlLevelResize, BorderLayout.WEST);
		
		JLabel lblTitleLabel = new JLabel("<html><b>Game Window Size</b></html>");
		pnlLevelResize.add(lblTitleLabel);
		
		JPanel pnlRControls = new JPanel();
		pnlRControls.setLayout(new GridLayout(3,4));
		pnlLevelResize.add(pnlRControls);
		
		// Level
		JLabel lblLWidth = new JLabel("LWidth (m):");
		pnlRControls.add(lblLWidth);
		
		txtLWidth = new JTextField();
		pnlRControls.add(txtLWidth);
		txtLWidth.setText("20");
		txtLWidth.setColumns(10);					
		
		JLabel lblLHeight = new JLabel("LHeight (m):");
		pnlRControls.add(lblLHeight);
		
		txtLHeight = new JTextField();
		pnlRControls.add(txtLHeight);
		txtLHeight.setText("15");
		txtLHeight.setColumns(10);
		
		// VP
		JLabel lblVPWidth = new JLabel("VPWidth (m):");
		pnlRControls.add(lblVPWidth);
		
		txtVPWidth = new JTextField();
		pnlRControls.add(txtVPWidth);
		txtVPWidth.setText("8");
		txtVPWidth.setColumns(10);					
		
		JLabel lblVPHeight = new JLabel("VPHeight (m):");
		pnlRControls.add(lblVPHeight);
		
		txtVPHeight = new JTextField();
		pnlRControls.add(txtVPHeight);
		txtVPHeight.setText("6");
		txtVPHeight.setColumns(10);
		
		JLabel lblMToPixel = new JLabel("mToPixel");
		pnlRControls.add(lblMToPixel);
		
		txtMToPixel = new JTextField();
		txtMToPixel.setText("100");
		txtMToPixel.setToolTipText("");
		pnlRControls.add(txtMToPixel);
		txtMToPixel.setColumns(10);
		
		JPanel pnlControlButtons = new JPanel();
		getContentPane().add(pnlControlButtons);
		
		JButton btnMToPixel = new JButton("Change mToPixel");
		btnMToPixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double mToPixel;
				try {
					mToPixel = Double.valueOf(txtMToPixel.getText());
				} catch (NumberFormatException e) {			
					JOptionPane.showMessageDialog(new JFrame("JOptionPane showMessageDialouge"), 
							"mToPixel must be a number");
					return;
				}				
				
				// TODO: negative numbers check				
				ctlAdapter.setMToPixel(mToPixel);			
			}
		});
		pnlControlButtons.add(btnMToPixel);
		
		// Resize
		
		JButton btnResizeScreen = new JButton("VP Resize");
		pnlControlButtons.add(btnResizeScreen);	
		btnResizeScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double width;
				try {
					width = Double.valueOf(txtVPWidth.getText());
				} catch (NumberFormatException e) {			
					JOptionPane.showMessageDialog(new JFrame("JOptionPane showMessageDialouge"), 
							"Width must be an number.");
					return;
				}
				int height;
				try {
					height = Integer.valueOf(txtVPHeight.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(new JFrame("JOptionPane showMessageDialouge"), 
							"Height must be an number.");
					return;
				}
				
				// TODO: negative numbers check				
				ctlAdapter.setViewportDimensions(width, height);
			}
		});
		
		JButton btnResizeLevel = new JButton("Level Resize");
		pnlControlButtons.add(btnResizeLevel);
		btnResizeLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double width;
				try {
					width = Double.valueOf(txtLWidth.getText());
				} catch (NumberFormatException e) {			
					JOptionPane.showMessageDialog(new JFrame("JOptionPane showMessageDialouge"), 
							"Width must be an number.");
					return;
				}
				int height;
				try {
					height = Integer.valueOf(txtLHeight.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(new JFrame("JOptionPane showMessageDialouge"), 
							"Height must be an number.");
					return;
				}
				
				// TODO: negative numbers check				
				ctlAdapter.setLevelDimensions(width, height);
			}
		});
		
		JPanel pnlEOL = new JPanel();
		pnlEOL.setLayout(new BoxLayout(pnlEOL, BoxLayout.Y_AXIS));
		getContentPane().add(pnlEOL);
		
		JLabel lblEOL = new JLabel("<html><b>End of Level</b></html>");
		pnlEOL.add(lblEOL);
		
		JButton btnEOL = new JButton("Mark EOL");
		btnEOL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.markEOL();
			}
		});
		pnlEOL.add(btnEOL);
		
		
		// Background panel		
		JPanel pnlBackground = new JPanel();
		pnlBackground.setLayout(new BoxLayout(pnlBackground, BoxLayout.Y_AXIS));
		
		getContentPane().add(pnlBackground, BorderLayout.NORTH);
		
		JLabel lblBackground = new JLabel("<html><b>Background</b></html>");
		pnlBackground.add(lblBackground);	
		
		JPanel pnlGrid = new JPanel();
		pnlGrid.setLayout(new GridLayout(1,3));
		pnlBackground.add(pnlGrid);
		
		// Background panel - grid
		ImageIcon iiSunny = new ImageIcon("assets/bgSunnyThumbnail.png");									
		ImageIcon iiCloud = new ImageIcon("assets/bgCloudThumbnail.png");		
		
		// Background panel - radio buttons
		ButtonGroup bgRadGroup = new ButtonGroup();
		
		JButton btnBgCloudy = new JButton("Cloudy");
		btnBgCloudy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.setBg("assets/bgCloud.png");
			}
		});
		btnBgCloudy.setIcon(iiCloud);
		pnlGrid.add(btnBgCloudy);
		bgRadGroup.add(btnBgCloudy);
		
		
		JButton btnBgSunny = new JButton("Sunny");
		btnBgSunny.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctlAdapter.setBg("assets/bgSunny.png");
			}
		});
		btnBgSunny.setIcon(iiSunny);
		pnlGrid.add(btnBgSunny);
		bgRadGroup.add(btnBgSunny);
				
		// Player panel
		
		JPanel pnlPlayer = new JPanel();
		pnlPlayer.setLayout(new BoxLayout(pnlPlayer, BoxLayout.Y_AXIS));
		
		getContentPane().add(pnlPlayer, BorderLayout.SOUTH);		
		
		JLabel lblPlayer = new JLabel("<html><b>Players</b></html>");
		pnlPlayer.add(lblPlayer);
		
		// Player panel - icons
		JPanel pnlCharGrid = new JPanel();
		pnlCharGrid.setLayout(new GridLayout(2,2));
		pnlPlayer.add(pnlCharGrid);
		
		ImageIcon iiMonkey = new ImageIcon("assets/MonkeyThumbnail.png");				
		ImageIcon iiMonk = new ImageIcon("assets/MonkThumbnail.png");				
		ImageIcon iiPig = new ImageIcon("assets/PigThumbnail.png");	
		ImageIcon iiSandy = new ImageIcon("assets/SandyThumbnail.png");
		
		// Player panel - toggle buttons
		ButtonGroup charToggleGroup = new ButtonGroup();														
				
		JToggleButton tglMonkey = new JToggleButton("Monkey");
		tglMonkey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {								
				ctlAdapter.togglePlayer("Monkey", tglMonkey.isSelected());
			}
		});
		tglMonkey.setIcon(iiMonkey);
		pnlCharGrid.add(tglMonkey);
		charToggleGroup.add(tglMonkey);
		
		JToggleButton tglMonk = new JToggleButton("Monk");
		tglMonk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Monk", tglMonk.isSelected());
			}
		});
		tglMonk.setIcon(iiMonk);
		pnlCharGrid.add(tglMonk);
		charToggleGroup.add(tglMonk);
		
		JToggleButton tglPig = new JToggleButton("Piggy");
		tglPig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Piggy", tglPig.isSelected());
			}
		});
		tglPig.setIcon(iiPig);
		pnlCharGrid.add(tglPig);
		charToggleGroup.add(tglPig);
		
		JToggleButton tglSandy = new JToggleButton("Sandy");
		tglSandy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Sandy", tglSandy.isSelected());
			}
		});
		tglSandy.setIcon(iiSandy);
		pnlCharGrid.add(tglSandy);
		charToggleGroup.add(tglSandy);
		
		JPanel pnlForeground = new JPanel();
		getContentPane().add(pnlForeground, BorderLayout.EAST);
		
		
		// Platforms
		
		JPanel pnlPlatform = new JPanel();
		pnlPlatform.setLayout(new BoxLayout(pnlPlatform, BoxLayout.Y_AXIS));
		getContentPane().add(pnlPlatform);
		
		JLabel lblPlatform = new JLabel("<html><b>Platforms</b></html>");
		pnlPlatform.add(lblPlatform);
			
		// Platform panel - grid
		
		JPanel pnlPlatGrid = new JPanel();
		pnlPlatGrid.setLayout(new GridLayout(1,3));
		pnlPlatform.add(pnlPlatGrid);
		
		ImageIcon iiPedestal = new ImageIcon("assets/PedestalThumbnail.png");				
		ImageIcon iiRock1 = new ImageIcon("assets/Rock1Thumbnail.png");				
		ImageIcon iiRock2 = new ImageIcon("assets/Rock2Thumbnail.png");	
		ImageIcon iiBlueGround = new ImageIcon("assets/blueGroundThumbnail.png");	
		
		// Platform panel - toggle buttons
		ButtonGroup platToggleGroup = new ButtonGroup();														
				
		JToggleButton tglBtnPedestal = new JToggleButton("Pedestal");
		tglBtnPedestal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Pedestal.png");
			}
		});
		tglBtnPedestal.setIcon(iiPedestal);
		pnlPlatGrid.add(tglBtnPedestal);
		platToggleGroup.add(tglBtnPedestal); 
		
		JToggleButton tglBtnRock1 = new JToggleButton("Rock1");
		tglBtnRock1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock1.png");
			}
		});
		tglBtnRock1.setIcon(iiRock1);
		pnlPlatGrid.add(tglBtnRock1);
		platToggleGroup.add(tglBtnRock1);
		
		JToggleButton tglBtnRock2 = new JToggleButton("Rock2");
		tglBtnRock2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock2.png");
			}
		});
		tglBtnRock2.setIcon(iiRock2);
		pnlPlatGrid.add(tglBtnRock2);
		platToggleGroup.add(tglBtnRock2);	
		
		JToggleButton btnBlueGround = new JToggleButton("BlueGround");
		btnBlueGround.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctlAdapter.makePlatform("assets/blueGround.png");
			}
		});
		btnBlueGround.setIcon(iiBlueGround);	
		pnlPlatGrid.add(btnBlueGround);
		platToggleGroup.add(btnBlueGround);
				
		/**
		 * pnlDisplayArea.setSize(Integer.valueOf(txtWidth.getText()), 
				Integer.valueOf(txtHeight.getText()));
		 */
					
	}
	
	/**
	 * After setting up the frame, make it visible to the user.
	 */
	public void start() {
		setTitle("Controls");
		//setSize(200, 550);
		setVisible(true);
	}
	
	/**
	 * Show the JSON-provided mToPixel ratio.
	 * @param mToPixel in-game meters-to-pixels ratio
	 */
	public void setMToPixel(double mToPixel) {
		txtMToPixel.setText(Double.toString(mToPixel));
	}

}
