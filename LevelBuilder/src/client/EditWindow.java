package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import interactable.PropertyBook;

public class EditWindow extends JPanel {

	private static final long serialVersionUID = 4911702070096013016L;

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
	 * JButton for submitting updates.
	 */
	JButton submit;
	
	/**
	 * JButton for recentering.
	 */
	JButton center;
	
	/**
	 * JButton for removing this window.
	 */
	JButton remove;	
	
	/**
	 * JCheckBox for adding/removing this object to the mass group-move.
	 */
	JCheckBox chkGroupSelect;
	
	/**
	 * Map of property ids to the property.
	 */
	PropertyBook book;
	
	Map<String, JTextField> fields = new HashMap<>();
	
	/**
	 * A window to edit the properties of an in-game object.
	 * @param name name of the type of object this window edits
	 * @param number ticket number of the edited object
	 */
	public EditWindow(String name, int number) {
		this.name = name;		
		this.twos = 0;
		this.book = new PropertyBook();
		
		// Make the initial JLabel featuring the ticket number.
		this.ticket = number;
		JLabel label = new JLabel("<html><b>#" + Integer.toString(number) +"</b></html>");
		add(label);
				
		// Make the submit button.
		this.submit = new JButton("Submit");
		add(this.submit);
		
		// Make the centering button.
		this.center = new JButton("New center");
		add(this.center);
		
		// Make the remove button.
		this.remove = new JButton("Remove");
		add(this.remove);		
		
		// Make the group move checkbox.
		JLabel groupLabel = new JLabel("");
		add(groupLabel);
		this.chkGroupSelect = new JCheckBox("Group select");
		add(this.chkGroupSelect);
		
		setLayout(new GridLayout(3,2,0,0));
		this.twos = 3;
	}
	
	/**
	 * Update the grid layout with however many properties we now have.
	 */
	void updateLayout() {
		setLayout(new GridLayout(this.twos, 2, 0, 0));
	}
	
	/**
	 * Set the action listener for submit. It needs to give the results back to someone.
	 * @param listener
	 */
	public void setSubmitListener(ActionListener listener) {
		this.submit.addActionListener(listener);
	}
	
	/**
	 * Set the action listener for remove. It informs the layer window it needs to be removed.
	 * @param listener
	 */
	void setRemoveListener(ActionListener listener) {
		this.remove.addActionListener(listener);
	}
	
	/**
	 * Set the action lister for telling the LayerManager to change center positions.
	 * @param listener
	 */
	public void setCenterListener(ActionListener listener) {
		this.center.addActionListener(listener);
	}
	
	/**
	 * Set the Group selection listener.
	 * @param type
	 */
	public void setGroupSelectListener(FocusListener listener) {
		this.chkGroupSelect.addFocusListener(listener);		
	}
	
	/**
	 * Return this edit window's properties and values
	 * @return properties
	 */
	public PropertyBook getPropertyBook() {
		return this.book;
	}
	
	/**
	 * Set this edit window's properties and values
	 * @param book properties
	 */
	void setPropertyBook(PropertyBook book) {
		this.book = book;
	}
	
	/**
	 * Make a new button and give it an action listener.
	 * @param text text on the button
	 * @param listener action listener for the button
	 */
	public void makeButton(String text, ActionListener listener) {
		JLabel lblBlank = new JLabel();
		add(lblBlank);
		
		JButton btnDimensions = new JButton(text);
		btnDimensions.addActionListener(listener);
		add(btnDimensions);
		
		this.twos++;
		updateLayout();
	}
	
	
	/**
	 * Make a new int-returning set. This includes a JLabel and JTextBox. 
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	public void makeIntProperty(String text, int defaultValue, PropertyBook pb) {
		int actualVal = defaultValue;
		if (pb != null && pb.getIntList().get(text) != null) {
			actualVal = pb.getIntList().get(text);
		}
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Integer.toString(actualVal));
		txtField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				book.getIntList().replace(text, Integer.valueOf(txtField.getText()));
			}
			
		});
		
		add(label);
		add(txtField);
		fields.put(text, txtField);
		
		book.getIntList().put(text, actualVal);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new double-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	public void makeDoubleProperty(String text, double defaultValue, PropertyBook pb) {
		double actualVal = defaultValue;
		if (pb != null && pb.getDoubList().get(text) != null) {
			actualVal = pb.getDoubList().get(text);
		}
		
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Double.toString(actualVal));
		txtField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				book.getDoubList().replace(text, Double.valueOf(txtField.getText()));
			}
		});
		add(label);
		add(txtField);
		
		fields.put(text, txtField);
		book.getDoubList().put(text, actualVal);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new float-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	public void makeFloatProperty(String text, float defaultValue, PropertyBook pb) {
		float actualVal = defaultValue;
		if (pb != null && pb.getFloatList().get(text) != null) {
			actualVal = pb.getFloatList().get(text);
		}
		
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(Float.toString(actualVal));
		txtField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				book.getFloatList().replace(text, Float.valueOf(txtField.getText()));
			}
			
		});
		add(label);
		add(txtField);
		
		fields.put(text, txtField);
		book.getFloatList().put(text, actualVal);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new String-returning set. This includes a JLabel and JTextBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this textbox
	 */
	public void makeStringProperty(String text, String defaultValue, PropertyBook pb) {
		String actualVal = defaultValue;
		if (pb != null && pb.getStringList().get(text) != null) {
			actualVal = pb.getStringList().get(text);
		}
		
		JLabel label = new JLabel(text);
		JTextField txtField = new JTextField(actualVal);
		txtField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				book.getStringList().replace(text, txtField.getText());				
			}
			
		});
		add(label);
		add(txtField);
		
		fields.put(text, txtField);
		book.getStringList().put(text, actualVal);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Make a new boolean-returning set. This includes a JLabel and JCheckBox.
	 * @param text property's name & jlabel's text
	 * @param defaultValue default value for this JCheckBox
	 */
	public void makeBooleanProperty(String text, boolean defaultValue, PropertyBook pb) {
		boolean actualVal = defaultValue;
		if (pb != null && pb.getBoolList().get(text) != null) {
			actualVal = pb.getBoolList().get(text);
		}
		
		JLabel label = new JLabel(text);
		JCheckBox chckBox = new JCheckBox();
		chckBox.setSelected(actualVal);
		// TODO: Check if this is OK as an actionListener and not a focusListener.
		chckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				book.getBoolList().replace(text, chckBox.isSelected());
			}			
		});
		
		add(label);
		add(chckBox);
		
		book.getBoolList().put(text, actualVal);
		
		this.twos++;
		updateLayout();
	}
	
	public <E extends Enum<E>> void makeEnumProperty(Class<E> e, E defaultValue, PropertyBook pb) {
    	E actualVal = defaultValue;
    	if (pb != null && pb.getStringList().get(e.getName()) != null) {
    		actualVal = Enum.valueOf(e, pb.getStringList().get(e.getName()));
    	}
        
    	JLabel label = new JLabel(e.getName());
 
    	JComboBox<E> box = new JComboBox<E>();
    	box.setModel(new DefaultComboBoxModel<>(e.getEnumConstants()));
    	box.setSelectedItem(actualVal);
    	
    	box.addActionListener((arg0) -> {
    		book.getStringList().replace(e.getName(), box.getItemAt(box.getSelectedIndex()).name());
    	});
    	add(label);
    	add(box);
    	book.getStringList().put(e.getName(), actualVal.name());
    	
    	this.twos++;
    	updateLayout();
    }
	
	/**
	 * Add a component with an optional label.
	 * @param component
	 */
	public void addComponentInstance(String optional, JComponent component) {
		JLabel label = new JLabel(optional);
		add(label);
		add(component);
		
		this.twos++;
		updateLayout();
	}
	
	/**
	 * Set this window's associated ticket based on the object it's associated with.
	 * @param text label's text
	 * @param defaultValue default value for this textbox
	 */
	public void setTicket(int number) {
		this.ticket = number;
	}
	
	/**
	 * Get the type of editing window this is - for example, a window that edits "Rock".
	 * @return name of type of editing window
	 */
	public String getEditWindowType() {
		return this.name;
	}
	
	/**
	 * Get the ticket of the object this window edits.
	 * @return object'sticket number
	 */
	int getTicket() {
		return this.ticket;
	}
	
	/**
	 * Updates the properties of this window. 
	 * If the new property book contains properties not in this book,
	 * those properties will be added.
	 * @param newBook new book of properties
	 */
	public void updateProperties(PropertyBook newBook) {				
        this.book.updateProperties(newBook);
        for (Map.Entry<String, Double> e : book.getDoubList().entrySet()) {
        	System.out.println(e + ": " + e.getKey() + ", " + e.getValue());
        	fields.get(e.getKey()).setText(Double.toString(e.getValue()));
        }
        for (Map.Entry<String, Integer> e : book.getIntList().entrySet()) {
        	fields.get(e.getKey()).setText(Integer.toString(e.getValue()));
        }
        for (Map.Entry<String, String> e : book.getStringList().entrySet()) {
        	fields.get(e.getKey()).setText(e.getValue());
        }
	}	
}
