package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.sql.*;

public class userLoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public userLoginGUI() {

        setTitle("Kullanıcı Girişi");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2));


        add(new JLabel("Kullanıcı Adı:"));
        usernameField = new JTextField();
        add(usernameField);


        add(new JLabel("Şifre:"));
        passwordField = new JPasswordField();
        add(passwordField);


        loginButton = new JButton("Giriş");
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755");

            

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Giriş başarılı!");

                
                userProfileGUI userGUI = new userProfileGUI(username);
                userGUI.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre yanlış.");
            }


            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }
    }
}
