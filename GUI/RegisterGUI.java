package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class RegisterGUI extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField ageField;
    private JPasswordField passwordField;
    private JButton registerUserButton;
    private JButton registerArtistButton;
    private JButton userlogin;
    private JButton artistlogin;

    public RegisterGUI() {
        setTitle("Kullanıcı ve Sanatçı Kaydı");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Kullanıcı Adı:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("E-posta:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Yaş:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Şifre:"));
        passwordField = new JPasswordField();
        add(passwordField);

        registerUserButton = new JButton("Kullanıcı Kayıt");
        add(registerUserButton);

        registerArtistButton = new JButton("Sanatçı Kayıt");
        add(registerArtistButton);

        userlogin = new JButton(" User Giriş");
        add(userlogin);

        artistlogin = new JButton(" Artist Giriş");
        add(artistlogin);


        registerUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        registerArtistButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerArtist();
            }
        });

        userlogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new userLoginGUI().setVisible(true);
            }
        });
        artistlogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new artistLoginGUI().setVisible(true);
            }
        });
    }

    private void registerUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String ageText = ageField.getText();
        Integer age = ageText.isEmpty() ? null : Integer.parseInt(ageText);
        String password = new String(passwordField.getPassword());


        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kullanıcı adı, e-posta ve şifre alanları boş bırakılamaz.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, email, age, password) VALUES (?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, email);
            if (age == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, age);
            }
            stmt.setString(4, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla eklendi.");
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }
    }

    private void registerArtist() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String ageText = ageField.getText();
        Integer age = ageText.isEmpty() ? null : Integer.parseInt(ageText);
        String password = new String(passwordField.getPassword());


        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sanatçı adı ve şifre alanları boş bırakılamaz.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO artist (ArtistName, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sanatçı başarıyla eklendi.");
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegisterGUI().setVisible(true);
            }
   });
}
}