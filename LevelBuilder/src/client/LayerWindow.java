package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class LayerWindow extends JFrame {

	JPanel pnlBack;
	JScrollPane scrPaneScroll;
	
	private static final long serialVersionUID = -1435555721573127869L;
	Map<Integer, EditWindow> windows;
	Map<Integer, JSeparator> separators;
	
	Dimension dimButton = new Dimension(68, 70);
	
	private static final String[] TYPES = {
			"All", "Platform", "Boulder", "Peg", "Vine", "NPC", "TextTip", "AttackZone"
	};;
		
	ILayerToLevelAdapter ltlAdapter;
	
	public LayerWindow(ILayerToLevelAdapter ltlAdapter) {
		this.ltlAdapter = ltlAdapter;
		this.windows = new HashMap<Integer, EditWindow>();
		this.separators = new HashMap<Integer, JSeparator>();
		
		initGUI();
	}
	
	/**
	 * Make a blank EditWindow. 
	 * @param number ticket number of object this is editing
	 * @param name type of object this is editing
	 */
	public EditWindow makeEditWindow(int number, String name) {
		// Make the window.
		EditWindow window = new EditWindow(name, number);
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);
		
		// Set its removal action listener.
		window.setRemoveListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeEditWindow(number);
			}			
		});
		
		// Add it to our known lists.		
		this.windows.put(number, window);
		this.separators.put(number, jsep);
		
		// Add it to the window.
		pnlBack.add(window);
		
		// Add a line to separate areas.
		pnlBack.add(jsep);
		
		// Resize the frame.
        Dimension d = new Dimension(500,300);
        this.scrPaneScroll.setPreferredSize(d);
		this.pack();	
		
		// Request focus.
		window.requestFocus();		
		window.scrollRectToVisible(window.getBounds());
		return window;
	}

	/**
	 * Remove an EditWindow.
	 * @param number ticket number of object this is editing
	 */
	public void removeEditWindow(int number) {
		// Get the type of window for later use.
		String type = this.windows.get(number).getEditWindowType();
		
		// Remove from the screen.
		pnlBack.remove(this.windows.get(number));
		pnlBack.remove(this.separators.get(number));
		
		// Remove from our known lists.
		this.windows.remove(number);		
		this.separators.remove(number);		
		
		// Remove the object from the level as well.		
		ltlAdapter.removeEntity(number, type);
		
		// Repaint our layer window.
		this.repaint();
	}
	
	/**
	 * Remove everything.
	 */
	public void clean() {
		this.windows.clear();
		this.separators.clear();
	}
	
	public void initGUI() {		
		// Make this a BoxY layout. 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(950, 50, 100, 100);
		JPanel contentPane = new JPanel();
		pnlBack = new JPanel();
		scrPaneScroll = new JScrollPane(pnlBack);		
		scrPaneScroll.setViewportView(pnlBack);
		scrPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPaneScroll.getVerticalScrollBar().setUnitIncrement(16);
		
		pnlBack.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBack.setLayout(new BoxLayout(pnlBack, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		contentPane.add(scrPaneScroll);
		
		// Add a panel that toggles all.
		JPanel pnlToggle = new JPanel();
		pnlToggle.setLayout(new GridLayout(3, 3, 0, 0));
		pnlBack.add(pnlToggle);		
		
		for (String type : TYPES) { 
			JButton btnBg = new JButton(type);
			btnBg.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Make invisible only objects that share this type. 
					windows.forEach((ticket, window) -> {
						if (window.getEditWindowType().equals(type) || type.equals("All")) {
							window.setVisible(true);
							separators.get(ticket).setVisible(true);
						} else {
							window.setVisible(false);
							separators.get(ticket).setVisible(false);
						}
					});
				}				
			}); 			
			btnBg.setPreferredSize(dimButton);
			btnBg.setBackground(new Color(143, 214, 248));
			pnlToggle.add(btnBg);
		}		
		
		// Add the group selection buttons.
		JButton btnSelectAll = new JButton("Select all");
		btnSelectAll.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// Tell the level manager to select all.
				ltlAdapter.selectAll();	
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}			
		});
		btnSelectAll.setPreferredSize(dimButton);
		btnSelectAll.setBackground(new Color(143, 251, 140));
		pnlToggle.add(btnSelectAll);
		
		JButton btnDeselectAll = new JButton("Deselect all");
		btnDeselectAll.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// Tell the level manager to deselect all.
				ltlAdapter.deselectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}			
		});
		btnDeselectAll.setPreferredSize(dimButton);
		pnlToggle.add(btnDeselectAll);
		
		JButton btnMoveSelected = new JButton("Group move");
		btnMoveSelected.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// Tell the level manager to group move.
				ltlAdapter.groupMove();	
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}			
		});
		btnMoveSelected.setPreferredSize(dimButton);
		btnMoveSelected.setBackground(new Color(248, 143, 151));
		pnlToggle.add(btnMoveSelected);			
	}
	
	public void start() {
		setTitle("Edit Objects");
		setSize(200, 550);
		setVisible(true);
	}
}
