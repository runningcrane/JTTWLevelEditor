package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;

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
	private JSlider slMToPixel;
	
	private JPanel pnlBack;	
	private JScrollPane scrPaneScroll;
	
	// Dimensions
	Dimension dimFrame = new Dimension(300,500);
	Dimension dimButton = new Dimension(68, 70);	

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
		
		pnlBack = new JPanel();
		scrPaneScroll = new JScrollPane(pnlBack);
		scrPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		pnlBack.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBack.setLayout(new BoxLayout(pnlBack, BoxLayout.Y_AXIS));
		getContentPane().add(scrPaneScroll);		
		
		JPanel pnlLevelResize = new JPanel();			
		pnlLevelResize.setLayout(new BoxLayout(pnlLevelResize, BoxLayout.Y_AXIS));
		pnlBack.add(pnlLevelResize, BorderLayout.WEST);
		
		JLabel lblTitleLabel = new JLabel("<html><b>Game Window Size</b></html>");
		pnlLevelResize.add(lblTitleLabel);
		
		JPanel pnlRControls = new JPanel();
		pnlRControls.setLayout(new GridLayout(2,4));
		Dimension dimPanel = new Dimension(250, 100);
		pnlRControls.setPreferredSize(dimPanel);
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
		pnlLevelResize.add(lblMToPixel);
		
		slMToPixel = new JSlider();
		slMToPixel.setValue(90);
		slMToPixel.setMaximum(390);
		slMToPixel.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent event) {
		        int value = slMToPixel.getValue();			        
		        
				// TODO: negative numbers check				
				ctlAdapter.setMToPixel(value + 10);		        
		      }
		    });
		pnlLevelResize.add(slMToPixel);
		
		JPanel pnlControlButtons = new JPanel();
		pnlControlButtons.setPreferredSize(new Dimension(50,60));
		pnlBack.add(pnlControlButtons);
		
		
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
		
		// EOL Panel
		
		JPanel pnlEOL = new JPanel();
		pnlEOL.setLayout(new BoxLayout(pnlEOL, BoxLayout.Y_AXIS));
		pnlEOL.setPreferredSize(new Dimension(50,50));
		pnlBack.add(pnlEOL);
		
		JLabel lblEOL = new JLabel("<html><b>End of Level</b></html>");
		pnlEOL.add(lblEOL);
		
		JButton btnEOL = new JButton("Mark EOL");
		btnEOL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.markEOL();
			}
		});
		pnlEOL.add(btnEOL);
		
		// Respawn points panel		
		JPanel pnlRP = new JPanel();
		pnlRP.setLayout(new BoxLayout(pnlRP, BoxLayout.Y_AXIS));
		pnlRP.setPreferredSize(new Dimension(50,50));
		pnlBack.add(pnlRP);
		
		JLabel lblRP = new JLabel("<html><b>Respawn Points</b></html>");
		pnlRP.add(lblRP);
		
		JPanel pnlRPControls = new JPanel();		
		pnlRControls.setPreferredSize(dimPanel);
		pnlRP.add(pnlRPControls);
		
		JButton btnRP = new JButton("Make RP");
		btnRP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.markRP();
			}
		});
		pnlRPControls.add(btnRP);
		
		JButton btnRPRemove = new JButton("Remove RP");
		btnRPRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.removeRP();
			}
		});
		pnlRPControls.add(btnRPRemove);
		
		// Background panel		
		JPanel pnlBackground = new JPanel();
		pnlBackground.setPreferredSize(dimPanel);
		pnlBackground.setLayout(new BoxLayout(pnlBackground, BoxLayout.Y_AXIS));
		
		pnlBack.add(pnlBackground, BorderLayout.NORTH);
		
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
		btnBgCloudy.setPreferredSize(dimButton);
		pnlGrid.add(btnBgCloudy);
		bgRadGroup.add(btnBgCloudy);
		
		
		JButton btnBgSunny = new JButton("Sunny");
		btnBgSunny.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctlAdapter.setBg("assets/bgSunny.png");
			}
		});
		btnBgSunny.setIcon(iiSunny);
		btnBgSunny.setPreferredSize(dimButton);
		pnlGrid.add(btnBgSunny);
		bgRadGroup.add(btnBgSunny);
				
		// Player panel
		
		JPanel pnlPlayer = new JPanel();
		pnlPlayer.setLayout(new BoxLayout(pnlPlayer, BoxLayout.Y_AXIS));
		
		pnlBack.add(pnlPlayer, BorderLayout.SOUTH);		
		
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
		tglMonkey.setPreferredSize(dimButton);
		pnlCharGrid.add(tglMonkey);
		charToggleGroup.add(tglMonkey);
		
		JToggleButton tglMonk = new JToggleButton("Monk");
		tglMonk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Monk", tglMonk.isSelected());
			}
		});
		tglMonk.setIcon(iiMonk);
		tglMonk.setPreferredSize(dimButton);
		pnlCharGrid.add(tglMonk);
		charToggleGroup.add(tglMonk);
		
		JToggleButton tglPig = new JToggleButton("Piggy");
		tglPig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Piggy", tglPig.isSelected());
			}
		});
		tglPig.setIcon(iiPig);
		tglPig.setPreferredSize(dimButton);
		pnlCharGrid.add(tglPig);
		charToggleGroup.add(tglPig);
		
		JToggleButton tglSandy = new JToggleButton("Sandy");
		tglSandy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.togglePlayer("Sandy", tglSandy.isSelected());
			}
		});
		tglSandy.setIcon(iiSandy);
		tglSandy.setPreferredSize(dimButton);
		pnlCharGrid.add(tglSandy);
		charToggleGroup.add(tglSandy);
		
		JPanel pnlForeground = new JPanel();
		pnlBack.add(pnlForeground, BorderLayout.EAST);
		
		// Vines
		JPanel pnlVines = new JPanel();
		pnlVines.setLayout(new BoxLayout(pnlVines, BoxLayout.Y_AXIS));
		pnlBack.add(pnlVines);
		
		JLabel lblVines = new JLabel("<html><b>Vines</b></html>");
		pnlVines.add(lblVines);

		ButtonGroup vineToggleGroup = new ButtonGroup();
		JPanel pnlVineGrid = new JPanel();
		pnlVineGrid.setLayout(new GridLayout(1,3));
		pnlVines.add(pnlVineGrid);
		
		String[] vines = {"vine1", "vine2", "vine3"};
		
		for (String vine : vines) {
			JToggleButton tglBtnVine = new JToggleButton(vine);
			tglBtnVine.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ctlAdapter.makeVine("assets/" + vine + ".png");
				}
			});
			ImageIcon iiVine = new ImageIcon("assets/" + vine + "Thumbnail.png");
			tglBtnVine.setIcon(iiVine);
			tglBtnVine.setPreferredSize(dimButton);
			pnlVineGrid.add(tglBtnVine);
			vineToggleGroup.add(tglBtnVine);
		}
		
		// Platforms
		
		JPanel pnlPlatform = new JPanel();
		pnlPlatform.setLayout(new BoxLayout(pnlPlatform, BoxLayout.Y_AXIS));
		pnlBack.add(pnlPlatform);		
		
		JLabel lblRockLabel = new JLabel("<html><b>Rocks</b></html>");
		pnlPlatform.add(lblRockLabel);
		
		JPanel pnlRocks = new JPanel();
		pnlRocks.setLayout(new GridLayout(3,4));
		pnlPlatform.add(pnlRocks);
		
		JLabel lblCloudLabel = new JLabel("<html><b>Clouds</b></html>");
		pnlPlatform.add(lblCloudLabel);
		
		JPanel pnlClouds = new JPanel();
		pnlClouds.setLayout(new GridLayout(4,4));
		pnlPlatform.add(pnlClouds);
		
		JLabel lblSpecialsLabel = new JLabel("<html><b>Special</b></html>");
		pnlPlatform.add(lblSpecialsLabel);
		
		JPanel pnlSpecials = new JPanel();		
		pnlPlatform.add(pnlSpecials);
			
		// Platform panel - grid
		
		JLabel lblPlatLabel = new JLabel("<html><b>Other platforms</b></html>");
		pnlPlatform.add(lblPlatLabel);
		
		JPanel pnlPlatGrid = new JPanel();
		pnlPlatGrid.setLayout(new GridLayout(2,4));
		pnlPlatform.add(pnlPlatGrid);
		
		ImageIcon iiPedestal = new ImageIcon("assets/PedestalThumbnail.png");				
		ImageIcon iiRock1 = new ImageIcon("assets/Rock1Thumbnail.png");				
		ImageIcon iiRock2 = new ImageIcon("assets/Rock2Thumbnail.png");
		ImageIcon iiRock3 = new ImageIcon("assets/Rock3Thumbnail.png");	
		ImageIcon iiRock4 = new ImageIcon("assets/Rock4Thumbnail.png");	
		ImageIcon iiLVL1Rock1 = new ImageIcon("assets/lvl1Rock1Thumbnail.png");				
		ImageIcon iiLVL1Rock2 = new ImageIcon("assets/lvl1Rock2Thumbnail.png");
		ImageIcon iiLVL1Rock3 = new ImageIcon("assets/lvl1Rock3Thumbnail.png");	
		ImageIcon iiLVL1Rock4 = new ImageIcon("assets/lvl1Rock4Thumbnail.png");	
		ImageIcon iiRock5 = new ImageIcon("assets/Rock5Thumbnail.png");	
		ImageIcon iiBlueGround = new ImageIcon("assets/blueGroundThumbnail.png");
		ImageIcon iicanyonR = new ImageIcon("assets/canyonRThumbnail.png");				
		ImageIcon iicanyonL = new ImageIcon("assets/canyonLThumbnail.png");
		ImageIcon iiForestGround = new ImageIcon("assets/ForestGroundThumbnail.png");	
		ImageIcon iiTree1 = new ImageIcon("assets/Tree1Thumbnail.png");
		ImageIcon iiTree2 = new ImageIcon("assets/Tree2Thumbnail.png");

		// Platform panel - toggle buttons
		ButtonGroup platToggleGroup = new ButtonGroup();														
				
		JToggleButton tglBtnPedestal = new JToggleButton("Pedestal");
		tglBtnPedestal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Pedestal.png");
			}
		});
		tglBtnPedestal.setIcon(iiPedestal);
		tglBtnPedestal.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtnPedestal);
		platToggleGroup.add(tglBtnPedestal); 
		
		JToggleButton tglBtncanyonL = new JToggleButton("canyonL");
		tglBtncanyonL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/canyonL.png");
			}
		});
		tglBtncanyonL.setIcon(iicanyonL);
		tglBtncanyonL.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtncanyonL);
		platToggleGroup.add(tglBtncanyonL); 
		
		JToggleButton tglBtncanyonR = new JToggleButton("canyonR");
		tglBtncanyonR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/canyonR.png");
			}
		});
		tglBtncanyonR.setIcon(iicanyonR);
		tglBtncanyonR.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtncanyonR);
		platToggleGroup.add(tglBtncanyonR); 		
		
		JToggleButton tglBtnTree1 = new JToggleButton("Tree1");
		tglBtnTree1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Tree1.png");
			}
		});
		tglBtnTree1.setIcon(iiTree1);
		tglBtnTree1.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtnTree1);
		platToggleGroup.add(tglBtnTree1);
		
		JToggleButton tglBtnTree2 = new JToggleButton("Tree2");
		tglBtnTree2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Tree2.png");
			}
		});
		tglBtnTree2.setIcon(iiTree2);
		tglBtnTree2.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtnTree2);
		platToggleGroup.add(tglBtnTree2); 
		
		JToggleButton tglBtnForestGround = new JToggleButton("ForestGround");
		tglBtnForestGround.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/ForestGround.png");
			}
		});
		tglBtnForestGround.setIcon(iiForestGround);
		tglBtnForestGround.setPreferredSize(dimButton);
		pnlPlatGrid.add(tglBtnForestGround);
		platToggleGroup.add(tglBtnForestGround); 				
				
		// Rocks
		
		JToggleButton tglBtnRock1 = new JToggleButton("Rock1");
		tglBtnRock1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock1.png");
			}
		});
		tglBtnRock1.setIcon(iiRock1);
		tglBtnRock1.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnRock1);
		platToggleGroup.add(tglBtnRock1);
		
		JToggleButton tglBtnRock2 = new JToggleButton("Rock2");
		tglBtnRock2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock2.png");
			}
		});
		tglBtnRock2.setIcon(iiRock2);
		tglBtnRock2.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnRock2);
		platToggleGroup.add(tglBtnRock2);	
		
		JToggleButton tglBtnRock3 = new JToggleButton("Rock3");
		tglBtnRock3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock3.png");
			}
		});
		tglBtnRock3.setIcon(iiRock3);
		tglBtnRock3.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnRock3);
		platToggleGroup.add(tglBtnRock3);
		
		JToggleButton tglBtnRock4 = new JToggleButton("Rock4");
		tglBtnRock4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock4.png");
			}
		});
		tglBtnRock4.setIcon(iiRock4);
		tglBtnRock4.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnRock4);
		platToggleGroup.add(tglBtnRock4);
		
		JToggleButton tglBtnRock5 = new JToggleButton("Rock5");
		tglBtnRock5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/Rock5.png");
			}
		});
		tglBtnRock5.setIcon(iiRock5);
		tglBtnRock5.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnRock5);
		platToggleGroup.add(tglBtnRock5);
		
		// Level 1-specific rocks
		JToggleButton tglBtnLVL1Rock1 = new JToggleButton("lvl1Rock1");
		tglBtnLVL1Rock1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/lvl1Rock1.png");
			}
		});
		tglBtnLVL1Rock1.setIcon(iiLVL1Rock1);
		tglBtnLVL1Rock1.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnLVL1Rock1);
		platToggleGroup.add(tglBtnLVL1Rock1);
		
		JToggleButton tglBtnLVL1Rock2 = new JToggleButton("lvl1Rock2");
		tglBtnLVL1Rock2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/lvl1Rock2.png");
			}
		});
		tglBtnLVL1Rock2.setIcon(iiLVL1Rock2);
		tglBtnLVL1Rock2.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnLVL1Rock2);
		platToggleGroup.add(tglBtnLVL1Rock2);	
		
		JToggleButton tglBtnLVL1Rock3 = new JToggleButton("lvl1Rock3");
		tglBtnLVL1Rock3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/lvl1Rock3.png");
			}
		});
		tglBtnLVL1Rock3.setIcon(iiLVL1Rock3);
		tglBtnLVL1Rock3.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnLVL1Rock3);
		platToggleGroup.add(tglBtnLVL1Rock3);
		
		JToggleButton tglBtnLVL1Rock4 = new JToggleButton("lvl1Rock4");
		tglBtnLVL1Rock4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/lvl1Rock4.png");
			}
		});
		tglBtnLVL1Rock4.setIcon(iiLVL1Rock4);
		tglBtnLVL1Rock4.setPreferredSize(dimButton);
		pnlRocks.add(tglBtnLVL1Rock4);
		platToggleGroup.add(tglBtnLVL1Rock4);
		
		JToggleButton btnBlueGround = new JToggleButton("BlueGround");
		btnBlueGround.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctlAdapter.makePlatform("assets/blueGround.png");
			}
		});
		btnBlueGround.setIcon(iiBlueGround);
		btnBlueGround.setPreferredSize(dimButton);
		pnlPlatGrid.add(btnBlueGround);
		platToggleGroup.add(btnBlueGround);
		
		// Clouds			
		String[] clouds = {"cldClear", "cldCloud", "cldStormy", "cldSunset",
				"cldClearDiagL", "cldCloudDiagL", "cldStormyDiagL", "cldSunsetDiagL",
				"cldClearDiagR", "cldCloudDiagR", "cldStormyDiarR", "cldSunsetDiagR",
				"cldClearUp", "cldCloudUp", "cldStormyUp", "cldSunsetUp"
		};
		
		for (String cloud : clouds) {
			ImageIcon iicld = new ImageIcon("assets/" + cloud + "Thumbnail.png");	
			JToggleButton tglcld = new JToggleButton(cloud);
			tglcld.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ctlAdapter.makePlatform("assets/" + cloud + "png");
				}
			});
			tglcld.setIcon(iicld);
			tglcld.setPreferredSize(dimButton);
			pnlClouds.add(tglcld);
			platToggleGroup.add(tglcld);
		}
		
		ImageIcon iiBuddhaHand = new ImageIcon("assets/BuddhaHandThumbnail.png");
		//ImageIcon iiBtnGate = new ImageIcon("assets/")
		
		// Special images
		JToggleButton tglBtnBuddhaHand = new JToggleButton("BuddhaHand");
		tglBtnBuddhaHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/BuddhaHand.png");
			}
		});
		tglBtnBuddhaHand.setIcon(iiBuddhaHand);
		tglBtnBuddhaHand.setPreferredSize(dimButton);
		pnlSpecials.add(tglBtnBuddhaHand);
		platToggleGroup.add(tglBtnBuddhaHand); 	
		
		// Boulders.
		JPanel pnlBoulders = new JPanel();
		pnlBoulders.setLayout(new BoxLayout(pnlBoulders, BoxLayout.Y_AXIS));
		pnlBack.add(pnlBoulders);
		
		JLabel lblBoulders = new JLabel("<html><b>Boulders</b></html>");
		pnlBoulders.add(lblBoulders);
		
		ButtonGroup boulderToggleGroup = new ButtonGroup();
		JPanel pnlBoulderGrid = new JPanel();
		pnlBoulderGrid.setLayout(new GridLayout(4,3));
		pnlBoulders.add(pnlBoulderGrid);
		
		String[] boulders = {"BoulderA", "BoulderB", "boulder0", "boulder1", "boulder2", "boulder3",
				"boulder4", "boulder5", "boulder6", "boulder7", "boulder8", "boulder9"
		};
		
		for (String boulder : boulders) {
			ImageIcon iiBoulder = new ImageIcon("assets/" + boulder + "Thumbnail.png");
			JToggleButton tglBtnBoulder = new JToggleButton(boulder);
			tglBtnBoulder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ctlAdapter.makeRock("assets/" + boulder + ".png");
				}
			});
			tglBtnBoulder.setIcon(iiBoulder);
			tglBtnBoulder.setPreferredSize(dimButton);
			pnlBoulderGrid.add(tglBtnBoulder);
			boulderToggleGroup.add(tglBtnBoulder);
		}
		
		JButton btnAddJoint = new JButton("Add Boulder Joint");
		btnAddJoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makeBoulderJoint();
			}
		});
		pnlBack.add(btnAddJoint);

		// Special images
		JToggleButton tglBtnGate = new JToggleButton("lvl1Gate");
		tglBtnGate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctlAdapter.makePlatform("assets/lvl1Gate.png");
			}
		});
		//tglBtnBuddhaHand.setIcon(ii);
		tglBtnBuddhaHand.setPreferredSize(dimButton);
		pnlSpecials.add(tglBtnGate);
		platToggleGroup.add(tglBtnGate); 		
		
		// Resize the frame.        
        this.scrPaneScroll.setPreferredSize(dimFrame);

		this.pack();
					
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
		slMToPixel.setValue((int)mToPixel - 10);		
	}

}
