package client;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

import java.awt.image.BufferedImage;

import javax.swing.JTextField;


/**
 * Window that holds information for a specific object (probably a platform)
 * on screen. This is more of a skeletal; the implementation is done as an 
 * anonymous inner class in LayerWindow.
 * 
 * @author melindacrane
 */
public class EditWindow extends JPanel {

	private static final long serialVersionUID = -3747952784243808533L;

	private JPanel contentPane;
	
	protected int ticket;
	private BufferedImage image;
	
	// Things that are set upon loading a level:
	JCheckBox chckbxDisappears;
	JCheckBox chckbxMoveable;
	JCheckBox chckbxSinkable;
	JCheckBox chckbxClimbable;
	JCheckBox chckbxCollidable;
	JCheckBox chckbxPolygon;
	JTextField txtSCK;
	JTextField txtVelocity;
	JTextField txtWidth;
	JTextField txtHeight;
	JTextField txtRotation;
	JTextField txtJointID;
	JTextField txtScale;
	JTextField txtArcl;
	JTextField txtStartVel;
	JTextField txtMass;
	JTextField txtB1;
	JTextField txtB2;
	JTextField txtOBX1;
	JTextField txtOBY1;
	JTextField txtOBX2;
	JTextField txtOBY2;
	JSlider slider;
	int cTicket1;
	int cTicket2;

	
	public EditWindow getThis() {
		return this;
	}

	/**
	 * Create the frame.
	 */
	public EditWindow(int ticket) {
		this.ticket = ticket;
		
		initGUI();

	}
	
	public void initGUI() {
		// To be filled in by LayerWindow.

	}

	public void start() {
		setVisible(true);
	}	
	
	/**
	 * Used when loading in a JSON file. We need a way to get rid of this window AND its JSON separator.
	 */
	public void manualRemove() {
		// To be filled in in LayerWindow
	}
}
