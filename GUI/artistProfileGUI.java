package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class artistProfileGUI extends JFrame {

    private JLabel kullaniciAdiLabel;
    private JPanel albumPanel;
    private JButton addAlbumButton;
    private String loggedInUsername;
    private int artistID;

    public artistProfileGUI(String username) {
        setTitle("Artist - " + username);
        this.loggedInUsername = username;
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755");) {
            PreparedStatement stmt = conn.prepareStatement("SELECT ArtistID, ArtistName FROM Artist WHERE ArtistName = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                artistID = rs.getInt("ArtistID");
                String artistName = rs.getString("ArtistName");
                kullaniciAdiLabel = new JLabel("Hoş geldin, " + artistName);
            } else {
                kullaniciAdiLabel = new JLabel("Sanatçı Adı Hatası");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            kullaniciAdiLabel = new JLabel("Veritabanına Bağlanılamadı");
        }

        
        albumPanel = new JPanel();
        albumPanel.setLayout(new BoxLayout(albumPanel, BoxLayout.Y_AXIS));
        loadAlbums();

      
        addAlbumButton = new JButton("Yeni Albüm Ekle");

        
        setLayout(new BorderLayout());
        add(kullaniciAdiLabel, BorderLayout.NORTH);
        add(new JScrollPane(albumPanel), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addAlbumButton);
        add(buttonPanel, BorderLayout.SOUTH);

     
        addAlbumButton.addActionListener(e -> {
          
            AddAlbumGUI addAlbumGUI = new AddAlbumGUI(username);
            addAlbumGUI.setVisible(true);
        });
    }

    private void loadAlbums() {
        albumPanel.removeAll();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement statement = connection.prepareStatement("SELECT Title FROM Album WHERE ArtistID = ?");
            statement.setInt(1, artistID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String albumTitle = resultSet.getString("Title");
                JButton albumButton = new JButton(albumTitle);
                albumButton.addActionListener(e -> {
                    showAlbumSongs albumSongsGUI = new showAlbumSongs(albumTitle, artistID);
                    albumSongsGUI.setVisible(true);
                });
                albumPanel.add(albumButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Albüm yüklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        albumPanel.revalidate();
        albumPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            artistProfileGUI profileGUI = new artistProfileGUI("exampleArtistName"); 
            profileGUI.setVisible(true);
        });
    }
}

