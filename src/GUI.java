import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GUI {
	private JTextArea output;
	
	public GUI() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridLayout(2,1));
		selectionPanel.setSize(250, 20);
		JRadioButton encryptButton = new JRadioButton("Encrypt");
		JRadioButton decryptButton = new JRadioButton("Decrypt");
		ButtonGroup cipherMode = new ButtonGroup();
		cipherMode.add(encryptButton);
		cipherMode.add(decryptButton);
		encryptButton.setSelected(true);
		selectionPanel.add(encryptButton);
		selectionPanel.add(decryptButton);
		
		JTextField key = new JTextField();
		key.setSize(frame.getSize().width, 10);
		JButton button = new JButton("Go");
		button.setSize(20, 20);
		
		topPanel.setLayout(new GridLayout(3,1));
		topPanel.add(selectionPanel);
		topPanel.add(key);
		topPanel.add(button);
		
		JPanel outputPanel = new JPanel();
		output = new JTextArea();
		output.setEditable(false);
		outputPanel.add(output);
		
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(outputPanel, BorderLayout.CENTER);
		
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350, 400);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (encryptButton.isSelected())
					Encryptor.encryptAll(key.getText(), GUI.this);
				else
					Encryptor.decryptAll(key.getText(), GUI.this);
			}
			
		});
	}
	
	public static void main(String[] args) {
		new GUI();
	}
		
	public void consoleAppend(String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				output.append(text+"\n");
			}
			
		});
	}
}
