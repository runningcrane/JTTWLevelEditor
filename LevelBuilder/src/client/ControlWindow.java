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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	/**
	 * All of the assets that we can use. To add another, just put the
	 * file name (without extension) in here and the image in ASSETS_PATH.
	 * 
	 * TODO: Read this stuff in from the assets folder automatically. 
	 */
	private static final String[] BACKGROUNDS = {
			"bgSunny", "bgCloud"
	};
	private static final String[] PLATFORMS = {
			"Pedestal", "blueGround", "canyonR", "canyonL", 
			"ForestGround", "Tree1", "Tree2", "paradiserock",
			"Ramp"
	};
	private static final String[] ROCKS = {
			"Rock1", "Rock2", "Rock3", "Rock4", "Rock5", 
			"lvl1Rock1", "lvl1Rock2", "lvl1Rock3", "lvl1Rock4"
	};
	private static final String[] CLOUDS = {
			"cldClear", "cldCloud", "cldStormy", "cldSunset",
			"cldClearDiagL", "cldCloudDiagL", "cldStormyDiagL", "cldSunsetDiagL",
			"cldClearDiagR", "cldCloudDiagR", "cldStormyDiagR", "cldSunsetDiagR",
			"cldClearUp", "cldCloudUp", "cldStormyUp", "cldSunsetUp"
	};;
	private static final String[] BOULDERS = {
			"BoulderA", "BoulderB", "BoulderC", "boulder0", "boulder1", "boulder2", "boulder3",
			"boulder4", "boulder5", "boulder6", "boulder7", "boulder8", "boulder9"
	};
	private static final String[] VINES = {
			"vine1", "vine2", "vine3"
	};
	
	private static final String[] PEGS = {
			"peg"
	};
	
	private static final String[] SPECIALS = {
		    "BuddhaHand", "lvl1Gate"	
	}; 
	
	private static final String ASSETS_PATH = "../assets/";
	private static final String THUMBNAIL_PATH = "../assets/thumbnails/";
	
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
		try {
			initGUI();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * GUI initialization code. Most of this is automatically generated by Swing.
	 * @throws FileNotFoundException 
	 */
	private void initGUI() throws FileNotFoundException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 300, 700);		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		pnlBack = new JPanel();
		scrPaneScroll = new JScrollPane(pnlBack);
		scrPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPaneScroll.getVerticalScrollBar().setUnitIncrement(10);
		
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
		slMToPixel.setValue(50);
		slMToPixel.setMaximum(100);
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
		btnEOL.addActionListener((arg0) -> ctlAdapter.markEOL()); 
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
		btnRP.addActionListener((arg0) -> ctlAdapter.markRP()); 
		pnlRPControls.add(btnRP);
		
		JButton btnRPRemove = new JButton("Remove RP");
		btnRPRemove.addActionListener((arg0) -> ctlAdapter.removeRP()); 
		pnlRPControls.add(btnRPRemove);
		
		// Background panel		
		JPanel pnlBackground = new JPanel();
		pnlBackground.setPreferredSize(dimPanel);
		pnlBackground.setLayout(new BoxLayout(pnlBackground, BoxLayout.Y_AXIS));
		
		pnlBack.add(pnlBackground, BorderLayout.NORTH);
		
		JLabel lblBackground = new JLabel("<html><b>Background</b></html>");
		pnlBackground.add(lblBackground);	
		
		// Background panel - grid		
		JPanel pnlGrid = new JPanel();
		pnlGrid.setLayout(new GridLayout(1,3));
		pnlBackground.add(pnlGrid);

		// Background panel - radio buttons
		ButtonGroup bgRadGroup = new ButtonGroup();
		
		for (String bg : BACKGROUNDS) { 
			JButton btnBg = new JButton(bg);
			btnBg.addActionListener((arg0) -> ctlAdapter.setBg(ASSETS_PATH + bg + ".png")); 
			btnBg.setIcon(new ImageIcon(THUMBNAIL_PATH + bg + "Thumbnail.png"));
			btnBg.setPreferredSize(dimButton);
			pnlGrid.add(btnBg);
			bgRadGroup.add(btnBg);
		}

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
		
		ImageIcon iiMonkey = new ImageIcon(THUMBNAIL_PATH + "MonkeyThumbnail.png");				
		ImageIcon iiMonk = new ImageIcon(THUMBNAIL_PATH + "MonkThumbnail.png");				
		ImageIcon iiPig = new ImageIcon(THUMBNAIL_PATH + "PigThumbnail.png");	
		ImageIcon iiSandy = new ImageIcon(THUMBNAIL_PATH + "SandyThumbnail.png");
		
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
	
		for (String vine : VINES) {
			JToggleButton tglBtnVine = new JToggleButton(vine);
			tglBtnVine.addActionListener((arg0) -> ctlAdapter.makeVine(ASSETS_PATH + vine + ".png")); 
			tglBtnVine.setIcon(new ImageIcon(THUMBNAIL_PATH + vine + "Thumbnail.png"));
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

		// Platform panel - grid
		
		JLabel lblPlatLabel = new JLabel("<html><b>Other platforms</b></html>");
		pnlPlatform.add(lblPlatLabel);
		
		JPanel pnlPlatGrid = new JPanel();
		pnlPlatGrid.setLayout(new GridLayout(2,4));
		pnlPlatform.add(pnlPlatGrid);
		
		// Platform panel - toggle buttons
		ButtonGroup platToggleGroup = new ButtonGroup();														

		for (String platform : PLATFORMS) {
			JToggleButton tglBtnPlatform = new JToggleButton(platform);
			tglBtnPlatform.addActionListener((arg0) -> ctlAdapter.makePlatform(ASSETS_PATH + platform + ".png"));
			tglBtnPlatform.setIcon(new ImageIcon(THUMBNAIL_PATH + platform + "Thumbnail.png"));
			tglBtnPlatform.setPreferredSize(dimButton);
			pnlPlatGrid.add(tglBtnPlatform);
			platToggleGroup.add(tglBtnPlatform); 
		}
		
		for (String rock : ROCKS) {
			JToggleButton tglBtnRock = new JToggleButton(rock);
			tglBtnRock.addActionListener((arg0) -> ctlAdapter.makePlatform(ASSETS_PATH + rock + ".png"));
			tglBtnRock.setIcon(new ImageIcon(THUMBNAIL_PATH + rock + "Thumbnail.png"));
			tglBtnRock.setPreferredSize(dimButton);
			pnlRocks.add(tglBtnRock);
			platToggleGroup.add(tglBtnRock); 
		}

		// Clouds				
		for (String cloud : CLOUDS) {	
			JToggleButton tglcld = new JToggleButton(cloud);
			tglcld.addActionListener((arg0) -> ctlAdapter.makePlatform(ASSETS_PATH + cloud + ".png"));
			tglcld.setIcon(new ImageIcon(THUMBNAIL_PATH + cloud + "Thumbnail.png"));
			tglcld.setPreferredSize(dimButton);
			pnlClouds.add(tglcld);
			platToggleGroup.add(tglcld);
		}
			
		JLabel lblSpecialsLabel = new JLabel("<html><b>Special</b></html>");
		pnlPlatform.add(lblSpecialsLabel);

		JPanel pnlSpecials = new JPanel();		
		pnlPlatform.add(pnlSpecials);
			
		// Special images
		for (String spec : SPECIALS) {	
			JToggleButton tglSpec = new JToggleButton(spec);
			tglSpec.addActionListener((arg0) -> ctlAdapter.makePlatform(ASSETS_PATH + spec + ".png"));
			tglSpec.setIcon(new ImageIcon(THUMBNAIL_PATH + spec + "Thumbnail.png"));
			tglSpec.setPreferredSize(dimButton);
			pnlSpecials.add(tglSpec);
			platToggleGroup.add(tglSpec);
		}

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
		
		for (String boulder : BOULDERS) {
			JToggleButton tglBtnBoulder = new JToggleButton(boulder);
			tglBtnBoulder.addActionListener((arg0) -> ctlAdapter.makeRock(ASSETS_PATH + boulder + ".png")); 
			tglBtnBoulder.setIcon(new ImageIcon(THUMBNAIL_PATH + boulder + "Thumbnail.png"));
			tglBtnBoulder.setPreferredSize(dimButton);
			pnlBoulderGrid.add(tglBtnBoulder);
			boulderToggleGroup.add(tglBtnBoulder);
		}
		
		JButton btnAddJoint = new JButton("Add Boulder Joint");
		btnAddJoint.addActionListener((arg0) -> ctlAdapter.makeBoulderJoint());
		
		for (String peg : PEGS) {
			JToggleButton tglBtnPeg = new JToggleButton(peg);
			tglBtnPeg.addActionListener((arg0) -> ctlAdapter.makePeg(ASSETS_PATH + peg + ".png"));
			tglBtnPeg.setIcon(new ImageIcon(THUMBNAIL_PATH + peg + "Thumbnail.png"));
			tglBtnPeg.setPreferredSize(dimButton);
			pnlBoulderGrid.add(tglBtnPeg);
			boulderToggleGroup.add(tglBtnPeg);
		}
		pnlBack.add(btnAddJoint);

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
