package new_client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EditWindow extends JPanel {

	/**
	 * Name of this type of edit window. 
	 */
	String name;
	
	/**
	 * Ticket number for the associated object this window is editing.
	 */
	int ticket;
	
	/**
	 * Number of two-object properties made.
	 */
	int twos;
	
	/**
	 * Number of one-object properties made.
	 */
	int ones;
	
	/**
	 * JButton for submitting updates.
	 */
	JButton submit;
	
	/**
	 * A window to edit the properties of an in-game object.
	 * @param name name of the type of object this window edits
	 * @param number ticket number of the edited object
	 */
	public EditWindow(String name, int number) {
		this.name = name;		
		this.twos = 0;
		this.ones = 0;
		
		// Make the initial JLabel featuring the ticket number.
		JLabel label = new JLabel("<html><b>#" + Integer.toString(number) +"</b></html>");
				
		// Make the submit button.
		this.submit = new JButton("Submit");
	}
	
	/**
	 * Set the action listener for submit. It needs to give the results back to someone.
	 * @param listener
	 */
	void setSubmitListener(ActionListener listener) {
		this.submit.addActionListener(listener);
	}
	
	// TODO: Make a map of all properties to their values for the submit listener to return.
	// TODO: make lists for each type of property so submit listener can do this.
	
	/**
	 * Make a new button and give it an action listener.
	 * @param text text on the button
	 * @param listener action listener for the button
	 */
	void makeButton(String text, ActionListener listener) {
		JButton btnDimensions = new JButton(text);
		btnDimensions.addActionListener(listener);
		add(btnDimensions);
		
		this.ones++;
	}
	
	
	/**
	 * Make a new int-returning set. This includes a JLabel and JTextBox. 
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	void makeIntProperty(String text, int defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Integer.toString(defaultValue));
		add(label);
		add(txtField);
		
		this.twos++;
	}
	
	/**
	 * Make a new double-returning set. This includes a JLabel and JTextBox.
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	void makeDoubleProperty(String text, double defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Double.toString(defaultValue));
		add(label);
		add(txtField);
		
		this.twos++;
	}
	
	/**
	 * Make a new float-returning set. This includes a JLabel and JTextBox.
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	void makeFloatProperty(String text, float defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Float.toString(defaultValue));
		add(label);
		add(txtField);
		
		this.twos++;
	}
	
	/**
	 * Make a new String-returning set. This includes a JLabel and JTextBox.
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	void makeStringProperty(String text, String defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(defaultValue);
		add(label);
		add(txtField);
		
		this.twos++;
	}
	
	/**
	 * Make a new booleane-returning set. This includes a JLabel and JCheckBox.
	 * @param text label's text
	 * @param defaultValue default value for this JCheckBox
	 */
	void makeBooleanProperty(String text, boolean defaultValue) {
		JLabel label = new JLabel(text);
		JCheckBox chckBox = new JCheckBox();
		chckBox.setSelected(defaultValue);
		
		add(label);
		add(chckBox);
		
		this.twos++;
	}
	
	/**
	 * Set this window's associated ticket based on the object it's associated with.
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	void setTicket(int number) {
		this.ticket = number;
	}
	
	/**
	 * Get the number of JObjects in this EditWindow. Outsiders can use this value
	 * to set a GridLayout with two columns to the appropriate number of rows.
	 * @return number of JObjects in this window
	 */
	int getNumberContained() {
		return this.ones + this.twos * 2;
	}
	
	/**
	 * Get the type of editing window this is - for example, a window that edits "Rock".
	 * @return name of type of editing window
	 */
	String getEditWindowType() {
		return this.name;
	}
	
	/**
	 * Get the ticket of the object this window edits.
	 * @return object'sticket number
	 */
	int getTicket() {
		return this.ticket;
	}
}
