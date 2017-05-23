import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ButtonGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.BorderLayout;

public class GUI extends JPanel {
	private JPasswordField passwordField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	JRadioButton rdbtnEncrypt;
	JRadioButton rdbtnDecrypt;
	JTextPane txtpnConsole;
	
	public GUI() {
		
		passwordField = new JPasswordField();
		passwordField.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JButton btnOk = new JButton("OK");
		btnOk.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnEncrypt.isSelected()) {
					encrypt(new String(passwordField.getPassword()));
				} else {
					decrypt(new String(passwordField.getPassword()));
				}
			}
		});
		
		rdbtnEncrypt = new JRadioButton("Encrypt");
		rdbtnEncrypt.setEnabled(true);
		buttonGroup.add(rdbtnEncrypt);
		rdbtnEncrypt.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		rdbtnDecrypt = new JRadioButton("Decrypt");
		buttonGroup.add(rdbtnDecrypt);
		rdbtnDecrypt.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JScrollPane scrollPaneConsole = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(rdbtnDecrypt, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
						.addComponent(rdbtnEncrypt)
						.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
						.addComponent(btnOk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(49)
					.addComponent(scrollPaneConsole, GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnEncrypt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnDecrypt, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(56, Short.MAX_VALUE))
				.addComponent(scrollPaneConsole, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
		);
		
		JPanel panelConsole = new JPanel();
		panelConsole.setBackground(Color.WHITE);
		scrollPaneConsole.setViewportView(panelConsole);
		panelConsole.setLayout(new BorderLayout(0, 0));
		
		txtpnConsole = new JTextPane();
		txtpnConsole.setEditable(false);
		panelConsole.add(txtpnConsole);
		
		JLabel lblConsole = new JLabel("Console");
		scrollPaneConsole.setColumnHeaderView(lblConsole);
		setLayout(groupLayout);
	}
	
	private void encrypt(String key) {
		try {
			Encryptor.encryptAll(key, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void decrypt(String key) {
		try {
			Encryptor.decryptAll(key, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateConsole(String str) {
		txtpnConsole.setText(txtpnConsole.getText() + str);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(new GUI());
		
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		/*String key = "password";
		String plaintext = "Dallan's secret.";
		try {
			byte[] keyByte1 = PasswordClass.hash(key, 128);
			byte[] keyByte2 = PasswordClass.hash("password", 128);
			
			System.out.println("====== Hashing test ======");
			System.out.println("hash: "+Encryptor.bytesToHex(keyByte1));
			System.out.println("hash: "+Encryptor.bytesToHex(keyByte2));
			
			byte[] data = plaintext.getBytes();
			byte[] encryptedData = Encryptor.encrypt(key, data);
			String encryptedText = Encryptor.bytesToHex(encryptedData);
			System.out.println("====== Encrypting test ======");
			System.out.println("plaintext: "+plaintext);
			System.out.println("encrypted: "+encryptedText);
			System.out.println("extracted hash: "+Encryptor.bytesToHex(Arrays.copyOfRange(encryptedData, 0, 16)));
			
			byte[] decryptedData = Encryptor.decrypt(key, encryptedData);
			System.out.println("====== Decrypting test ======");
			if (decryptedData != null) {
				System.out.println("decrypted: "+ new String(decryptedData));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
