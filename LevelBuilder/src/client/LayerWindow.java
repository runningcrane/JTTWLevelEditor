package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class LayerWindow extends JFrame {

	private JPanel contentPane;
	private ILayerToLevelAdapter ltlAdapter;
	private HashMap<Integer, EditWindow> edits;
	
	/**
	 * Create the frame.
	 */
	public LayerWindow(ILayerToLevelAdapter ltlAdapter) {
		this.ltlAdapter = ltlAdapter;
		this.edits = new HashMap<Integer, EditWindow>();
		initGUI();
	}	
	
	public void addPlatformEdit(int ticket) {
		JSeparator jsep = new JSeparator(SwingConstants.HORIZONTAL);
		EditWindow newWindow = new EditWindow(ticket) {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 8116441836763125578L;

			@Override
			public void initGUI() {
				setLayout(new GridLayout(3, 1, 0, 0));
				
				JButton btnMove = new JButton("Move");
				btnMove.setPreferredSize(new Dimension(100, 25));
				btnMove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ltlAdapter.editPlatCenter(ticket);
					}
				});
				
				JLabel lblTicket = new JLabel("#: " + ticket);
				add(lblTicket);
				add(btnMove);
				
				JButton btnCollision = new JButton("Edit Box");
				btnCollision.setPreferredSize(new Dimension(100, 25));
				btnCollision.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.editPlatCollisionBox(ticket);
					}
				});
				add(btnCollision);
				
				JButton btnDelete = new JButton("Remove");
				btnDelete.setPreferredSize(new Dimension(100, 25));
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ltlAdapter.removePlat(ticket);
						
						// Remove this EditWindow.
						removeEditWindow(ticket, jsep);
					}
				});
				btnDelete.setBackground(Color.RED);
				btnDelete.setForeground(Color.WHITE);
				add(btnDelete);		
			}
		};
		
		contentPane.add(newWindow);
		this.edits.put(ticket, newWindow);
		
		// Add a line to separate areas.
		contentPane.add(jsep);
		
		// Resize the frame.
		this.pack();
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		setContentPane(contentPane);	
	}
	
	public void start() {
		setTitle("Edit Objects");
		setSize(200, 550);
		setVisible(true);
	}
	
	public void removeEditWindow(int ticket, JSeparator jsep) {
		contentPane.remove(this.edits.get(ticket));
		contentPane.remove(jsep);
		
		// Refresh the layer.
		contentPane.repaint();
	}

}
