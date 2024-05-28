package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainGUI extends JFrame {
    private JButton loginButton;
    private JButton registerButton;
    private JButton artistLoginButton;

    public MainGUI() {
        setTitle("Ana Ekran");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        
        loginButton = new JButton("Giriş");
        add(loginButton);

        
        registerButton = new JButton("Kayıt");
        add(registerButton);

        artistLoginButton = new JButton("Artist Login");
        add(artistLoginButton);



        setLayout(new BorderLayout());
        add(new JScrollPane(registerButton), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(artistLoginButton);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);



        
        loginButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                new userLoginGUI().setVisible(true);
            }
        });

        registerButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                new RegisterGUI().setVisible(true);
            }
        });

        artistLoginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new artistLoginGUI().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }
}
