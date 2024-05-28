package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class showPlaylistSongs extends JFrame {
	
    public showPlaylistSongs(String playlistName) {
        setTitle("Playlist Şarkıları - " + playlistName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
          
            String getPlaylistIDQuery = "SELECT PlayListID FROM playlist WHERE Name = ?";
            int playlistID = -1;
            try (PreparedStatement getPlaylistIDStmt = connection.prepareStatement(getPlaylistIDQuery)) {
                getPlaylistIDStmt.setString(1, playlistName);
                try (ResultSet rs = getPlaylistIDStmt.executeQuery()) {
                    if (rs.next()) {
                        playlistID = rs.getInt("PlayListID");
                    } else {
                        JOptionPane.showMessageDialog(null, "Playlist bulunamadı: " + playlistName);
                        return; 
                    }
                }
            }

          
            String getSongsQuery = "SELECT title FROM song_title WHERE SongID IN (SELECT SongID FROM playlist WHERE PlayListID = ?)";
            DefaultListModel<String> songListModel = new DefaultListModel<>();
            try (PreparedStatement getSongsStmt = connection.prepareStatement(getSongsQuery)) {
                getSongsStmt.setInt(1, playlistID);
                try (ResultSet resultSet = getSongsStmt.executeQuery()) {
                    while (resultSet.next()) {
                        String songTitle = resultSet.getString("title");
                        songListModel.addElement(songTitle);
                    }
                }
            }

          
            JList<String> songList = new JList<>(songListModel);
            add(new JScrollPane(songList), BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata: " + e.getMessage());
        }

        setVisible(true);
    }

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> new showPlaylistSongs("YourPlaylistName"));
    }
}
