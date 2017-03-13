package new_client;

import java.awt.GridLayout;
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
	 * Map of property ids to the property.
	 */
	PropertyBook book;
	
	/**
	 * A window to edit the properties of an in-game object.
	 * @param name name of the type of object this window edits
	 * @param number ticket number of the edited object
	 */
	public EditWindow(String name, int number) {
		this.name = name;		
		this.twos = 0;
		this.ones = 0;
		this.book = new PropertyBook();
		
		// Make the initial JLabel featuring the ticket number.
		this.ticket = number;
		JLabel label = new JLabel("<html><b>#" + Integer.toString(number) +"</b></html>");
		add(label);
				
		// Make the submit button.
		this.submit = new JButton("Submit");
		add(this.submit);
		
		setLayout(new GridLayout(1,2,0,0));
	}
	
	/**
	 * Update the grid layout with however many properties we now have.
	 */
	void updateLayout() {
		setLayout(new GridLayout((this.ones + this.twos * 2)/2, 2, 0, 0));
	}
	
	/**
	 * Set the action listener for submit. It needs to give the results back to someone.
	 * @param listener
	 */
	void setSubmitListener(ActionListener listener) {
		this.submit.addActionListener(listener);
	}
	
	/**
	 * Return this edit window's properties and values
	 * @return properties
	 */
	PropertyBook getPropertyBook() {
		return this.book;
	}
	
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
		updateLayout();
	}
	
	
	/**
	 * Make a new int-returning set. This includes a JLabel and JTextBox. 
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	void makeIntProperty(String text, int defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Integer.toString(defaultValue) + ":");
		txtField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getIntList().replace(text, Integer.valueOf(txtField.getText()));
				
			}			
		});
		add(label);
		add(txtField);
		
		book.getIntList().put(text, defaultValue);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new double-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	void makeDoubleProperty(String text, double defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Double.toString(defaultValue));
		txtField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getDoubList().replace(text, Double.valueOf(txtField.getText()));
				
			}			
		});
		add(label);
		add(txtField);
		
		book.getDoubList().put(text, defaultValue);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new float-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	void makeFloatProperty(String text, float defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Float.toString(defaultValue));
		txtField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getFloatList().replace(text, Float.valueOf(txtField.getText()));
				
			}			
		});
		add(label);
		add(txtField);
		
		book.getFloatList().put(text, defaultValue);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new String-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	void makeStringProperty(String text, String defaultValue) {
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(defaultValue);
		txtField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getStringList().replace(text, txtField.getText());
				
			}			
		});
		add(label);
		add(txtField);
		
		book.getStringList().put(text, defaultValue);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new booleane-returning set. This includes a JLabel and JCheckBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this JCheckBox
	 */
	void makeBooleanProperty(String text, boolean defaultValue) {
		JLabel label = new JLabel(text);
		JCheckBox chckBox = new JCheckBox();
		chckBox.setSelected(defaultValue);
		chckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getBoolList().replace(text, chckBox.isSelected());
				
			}			
		});
		
		add(label);
		add(chckBox);
		
		book.getBoolList().put(text, defaultValue);
		
		this.twos++;
		updateLayout();
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
