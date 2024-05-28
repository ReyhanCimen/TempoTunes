package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddSongGUI extends JFrame {
    private JTextField songTitleField;
    private JButton addButton;
    private int albumID;

    public AddSongGUI(int albumID, String albumName) {
        this.albumID = albumID;
        setTitle("Albüm'e Şarkı Ekle - " + albumName);
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel songTitleLabel = new JLabel("Şarkı Adı:");
        songTitleField = new JTextField();
        addButton = new JButton("Ekle");

        panel.add(songTitleLabel);
        panel.add(songTitleField);
        panel.add(new JLabel());
        panel.add(addButton);

        addButton.addActionListener(e -> {
            String songTitle = songTitleField.getText();
            if (songTitle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Şarkı adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                addSongToAlbum(songTitle);
            }
        });

        add(panel);
    }

    private void addSongToAlbum(String songTitle) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO song_title (AlbumID, Title) VALUES (?, ?)");
            stmt.setInt(1, albumID);
            stmt.setString(2, songTitle);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Şarkı başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                songTitleField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Şarkı eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}
