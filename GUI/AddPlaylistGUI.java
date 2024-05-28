package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddPlaylistGUI extends JFrame {

    private JTextField playlistNameField;
    private JTextField songNameField;
    private JButton addButton;
    private String loggedInUsername;
    private int loggedInUserID;

    public AddPlaylistGUI(String username) {
        this.loggedInUsername = username;

        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT UserID FROM User WHERE Username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserID = rs.getInt("UserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }

        setTitle("Yeni Çalma Listesi Ekle");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        playlistNameField = new JTextField(20);
        songNameField = new JTextField(20);
        addButton = new JButton("Ekle");

        setLayout(new FlowLayout());
        add(new JLabel("Çalma Listesi Adı:"));
        add(playlistNameField);
        add(new JLabel("Şarkı Adı:"));
        add(songNameField);
        add(addButton);

        addButton.addActionListener(e -> eklePlaylist());
    }

    private void eklePlaylist() {
        String playlistName = playlistNameField.getText();
        String songName = songNameField.getText();
        if (playlistName.isEmpty() || songName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Çalma listesi adı ve şarkı adı boş olamaz.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            
            int songID = getSongID(conn, songName);
            if (songID == -1) {
                return; 
            }

            
            PreparedStatement playlistStmt = conn.prepareStatement("INSERT INTO Playlist (Name, UserID, songID) VALUES (?, ?, ?)");
            playlistStmt.setString(1, playlistName);
            playlistStmt.setInt(2, loggedInUserID);
            playlistStmt.setInt(3, songID);

            int affectedRows = playlistStmt.executeUpdate();

            if (affectedRows > 0) {
                int playlistID = getInsertedPlaylistID(conn);

                PreparedStatement playlistSongStmt = conn.prepareStatement("INSERT INTO playlist_song (PlaylistID, SongID) VALUES (?, ?)");
                playlistSongStmt.setInt(1, playlistID);
                playlistSongStmt.setInt(2, songID);
                playlistSongStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Çalma listesi ve şarkı başarıyla eklendi.");
            } else {
                JOptionPane.showMessageDialog(this, "Çalma listesi eklenirken bir hata oluştu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }
    }

    private int getSongID(Connection conn, String songName) throws SQLException {
        PreparedStatement songStmt = conn.prepareStatement("SELECT SongID FROM song_title WHERE Title = ?");
        songStmt.setString(1, songName);
        ResultSet songRs = songStmt.executeQuery();
        
        if (songRs.next()) {
            return songRs.getInt("SongID");
        } else {
            JOptionPane.showMessageDialog(this, "Girilen şarkı adı bulunamadı.");
            return -1;
        }
    }

    private int getInsertedPlaylistID(Connection conn) throws SQLException {
        PreparedStatement playlistIDStmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet playlistIDRs = playlistIDStmt.executeQuery();
        if (playlistIDRs.next()) {
            return playlistIDRs.getInt(1);
        }
        return -1;
    }
}
