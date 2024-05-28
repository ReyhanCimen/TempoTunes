package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class showAlbumSongs extends JFrame {

    public showAlbumSongs(String albumName, int artistID) {
        setTitle("Albüm Şarkıları - " + albumName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            // Albüm ID'sini al
            PreparedStatement albumIDStmt = connection.prepareStatement("SELECT AlbumID FROM Album WHERE Title = ? AND ArtistID = ?");
            albumIDStmt.setString(1, albumName);
            albumIDStmt.setInt(2, artistID);
            ResultSet resultSet = albumIDStmt.executeQuery();
            int albumID = -1;
            if (resultSet.next()) {
                albumID = resultSet.getInt("AlbumID");
            }

            if (albumID == -1) {
                JOptionPane.showMessageDialog(this, "Albüm bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Şarkıları al 
            PreparedStatement songStmt = connection.prepareStatement("SELECT Title FROM song_title WHERE AlbumID = ?");
            songStmt.setInt(1, albumID);
            resultSet = songStmt.executeQuery();

            DefaultListModel<String> songListModel = new DefaultListModel<>();
            while (resultSet.next()) {
                songListModel.addElement(resultSet.getString("Title"));
            }

            JList<String> songList = new JList<>(songListModel);
            add(new JScrollPane(songList), BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }

        setVisible(true);
    }
}

