package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

import org.json.simple.JSONObject;

//import org.json.simple.JSONObject;

public class OutputWindow extends JFrame {

	private JPanel contentPane;
	private JPanel pnlContent;
	private IOutputToLevelAdapter otlAdapter;
	private JTextField txtJsonOutputPath;
	private JTextField txtPath;

	/**
	 * Create the frame.
	 */
	public OutputWindow(IOutputToLevelAdapter otlAdapter) {
		this.otlAdapter = otlAdapter;		
		initGUI();
	}
	
	public void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pnlControls = new JPanel();
		getContentPane().add(pnlControls, BorderLayout.NORTH);
		
		JLabel lblPath = new JLabel("JSON Output Path");
		pnlControls.add(lblPath);
		
		txtPath = new JTextField();
		txtPath.setText("level.json");
		pnlControls.add(txtPath);
		txtPath.setColumns(10);
		
		JButton btnMakeJSON = new JButton("Output JSON");
		btnMakeJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Try to write out the JSON file.
				 */
				FileWriter file;
				try {
					file = new FileWriter(txtPath.getText());
					file.write(otlAdapter.makeJSON().toJSONString());
					file.flush();
					file.close();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}			
			}
		});
		pnlControls.add(btnMakeJSON);
		
		pnlContent = new JPanel() {
			/**
			 * UID for serialization.
			 */
			private static final long serialVersionUID = 242174451501027598L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				otlAdapter.render(this, g);
			}
		};
		getContentPane().add(pnlContent, BorderLayout.CENTER);
	}
	/**
	 * After setting up the frame, make it visible to the user.
	 */
	public void start() {		
		setTitle("GameView");
		setVisible(true);
	}
	
	/**
	 * Resize this panel.
	 * @param wm width in pixels
	 * @param hm height in pixels
	 */
	public void setDimensions(int wm, int hm) {
		pnlContent.setSize(wm, hm);
	}
		
	
	/**
	 * Redraws the panel with any new/deleted art.
	 */
	public void redraw() {
		pnlContent.repaint();
	}	

}
