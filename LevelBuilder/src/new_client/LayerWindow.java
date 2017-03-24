package new_client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	private static final long serialVersionUID = -1435555721573127869L;
	Map<Integer, EditWindow> windows;
	Map<Integer, JSeparator> separators;
	
	Dimension dimButton = new Dimension(68, 70);
	
	private static final String[] TYPES = {
			"All", "Platform", "Rock", "Vine", "Peg", "Trap"
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
		
		return window;
	}

	/**
	 * Remove an EditWindow.
	 * @param number ticket number of object this is editing
	 */
	public void removeEditWindow(int number) {
		String type = this.windows.get(number).getEditWindowType();
		
		this.windows.remove(number);
		this.separators.remove(number);		
		
		// Remove the object from the level as well.
		ltlAdapter.removeEntity(number, type);
		
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
		JPanel contentPane = new JPanel();
		JPanel pnlBack = new JPanel();
		JScrollPane scrPaneScroll = new JScrollPane(pnlBack);
		scrPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		pnlBack.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBack.setLayout(new BoxLayout(pnlBack, BoxLayout.Y_AXIS));
		setContentPane(contentPane);
		contentPane.add(scrPaneScroll);
		
		// Add a panel that toggles all.
		JPanel pnlToggle = new JPanel();
		pnlToggle.setLayout(new GridLayout(2, 3, 0, 0));
		pnlBack.add(pnlToggle);
		
		JButton btnAll = new JButton("All");
		btnAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				windows.forEach((ticket,window) -> {
					window.setVisible(true);
					separators.get(ticket).setVisible(true);
				});				
			}			
		});
		pnlToggle.add(btnAll);		
		
		for (String type : TYPES) { 
			JButton btnBg = new JButton(type);
			btnBg.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Make invisible only objects that share this type. 
					windows.forEach((ticket, window) -> {
						if (window.getEditWindowType().equals(type)) {
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
			pnlToggle.add(btnAll);
		}		
	}
	
	public void start() {
		setTitle("Edit Objects");
		setSize(200, 550);
		setVisible(true);
	}
}
