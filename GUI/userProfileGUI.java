package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userProfileGUI extends JFrame {

    private JLabel kullaniciAdiLabel;
    private JList<String> playlistList;
    private JButton addPlaylistButton;
    private JButton goToPlaylistButton;
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> searchResultsList;
    private DefaultListModel<String> searchResultsListModel;
    private JButton addToPlaylistButton;
    private String loggedInUsername;
    private int loggedInUserID;

    public userProfileGUI(String username) {
        setTitle("Kullanıcı - " + username);
        this.loggedInUsername = username;
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

       
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT UserID, Username FROM User WHERE Username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserID = rs.getInt("UserID");
                String kullaniciAdi = rs.getString("Username");
                kullaniciAdiLabel = new JLabel("Hoş geldin, " + kullaniciAdi);
            } else {
                kullaniciAdiLabel = new JLabel("Kullanıcı Adı Hatası");
                loggedInUserID = -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            kullaniciAdiLabel = new JLabel("Veritabanına Bağlanılamadı");
            loggedInUserID = -1;
        }

    
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (loggedInUserID != -1) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
                PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT Name FROM Playlist WHERE UserID = ?");
                statement.setInt(1, loggedInUserID);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    listModel.addElement(resultSet.getString("Name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        playlistList = new JList<>(listModel);

      
        addPlaylistButton = new JButton("Yeni Çalma Listesi Ekle");
        goToPlaylistButton = new JButton("Playlist'e Git");

      
        searchField = new JTextField(20);
        searchButton = new JButton("Ara");
        searchResultsListModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsListModel);
        addToPlaylistButton = new JButton("Seçilen Şarkıyı Playliste Ekle");

        
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(kullaniciAdiLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(playlistList), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchInputPanel = new JPanel();
        searchInputPanel.add(new JLabel("Şarkı veya Sanatçı Ara:"));
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        searchPanel.add(new JScrollPane(searchResultsList), BorderLayout.CENTER);
        searchPanel.add(addToPlaylistButton, BorderLayout.SOUTH);

        centerPanel.add(searchPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addPlaylistButton);
        buttonPanel.add(goToPlaylistButton);
        add(buttonPanel, BorderLayout.SOUTH);

        
        addPlaylistButton.addActionListener(e -> {
        
            AddPlaylistGUI addPlaylistGUI = new AddPlaylistGUI(username);
            addPlaylistGUI.setVisible(true);
        });

        goToPlaylistButton.addActionListener(e -> {
         
            int selectedIndex = playlistList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedPlaylistName = playlistList.getModel().getElementAt(selectedIndex);
                showPlaylistSongs userGUI = new showPlaylistSongs(selectedPlaylistName);
                userGUI.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Lütfen bir çalma listesi seçin.");
            }
        });

        searchButton.addActionListener(e -> performSearch());

        addToPlaylistButton.addActionListener(e -> {
            int selectedSongIndex = searchResultsList.getSelectedIndex();
            if (selectedSongIndex != -1) {
                String selectedSongInfo = searchResultsList.getModel().getElementAt(selectedSongIndex);
                String[] parts = selectedSongInfo.split(" - ");
                String selectedSongTitle = parts[2]; // Assuming "Song: Title"
                int selectedPlaylistIndex = playlistList.getSelectedIndex();

                if (selectedPlaylistIndex != -1) {
                    String selectedPlaylistName = playlistList.getModel().getElementAt(selectedPlaylistIndex);
                    addSongToPlaylist(selectedSongTitle, selectedPlaylistName);
                } else {
                    JOptionPane.showMessageDialog(null, "Lütfen bir çalma listesi seçin.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Lütfen bir şarkı seçin.");
            }
        });
    }

    private void performSearch() {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir arama terimi girin.");
            return;
        }

        List<String> results = searchSongsOrArtists(searchQuery);
        searchResultsListModel.clear();
        if (results.isEmpty()) {
            searchResultsListModel.addElement("Sonuç bulunamadı.");
        } else {
            for (String result : results) {
                searchResultsListModel.addElement(result);
            }
        }
    }

    private List<String> searchSongsOrArtists(String query) {
        List<String> results = new ArrayList<>();
        String searchSQLForSongs = "SELECT SongTitle, AlbumTitle, ArtistName FROM view_song_titles WHERE SongTitle LIKE ?";
        String searchSQLForArtists = "SELECT SongTitle, AlbumTitle, ArtistName FROM view_songs_by_artist WHERE ArtistName LIKE ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            
            try (PreparedStatement stmt = conn.prepareStatement(searchSQLForSongs)) {
                stmt.setString(1, "%" + query + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    results.add("Artist: " + rs.getString("ArtistName") + " - Album: " + rs.getString("AlbumTitle") + " - Song: " + rs.getString("SongTitle"));
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(searchSQLForArtists)) {
                stmt.setString(1, "%" + query + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    results.add("Artist: " + rs.getString("ArtistName") + " - Album: " + rs.getString("AlbumTitle") + " - Song: " + rs.getString("SongTitle"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private void addSongToPlaylist(String songTitle, String playlistName) {
        String getPlaylistIDQuery = "SELECT PlaylistID FROM playlist WHERE Name = ? AND UserID = ?";
        String getSongIDQuery = "SELECT SongID FROM song_title WHERE Title = ?";
        String insertSongToPlaylistQuery = "INSERT INTO playlist (PlaylistID, SongID) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            
        	try (PreparedStatement getPlaylistIDStmt = connection.prepareStatement(getPlaylistIDQuery)) {
                getPlaylistIDStmt.setString(1, playlistName);
                getPlaylistIDStmt.setInt(2, loggedInUserID);
                
                try (ResultSet rs = getPlaylistIDStmt.executeQuery()) {
                    if (rs.next()) {
                        int playlistID = rs.getInt("PlaylistID");

                        
                        try (PreparedStatement getSongIDStmt = connection.prepareStatement(getSongIDQuery)) {
                            getSongIDStmt.setString(1, songTitle);
                            try (ResultSet rs2 = getSongIDStmt.executeQuery()) {
                                if (rs2.next()) {
                                    int songID = rs2.getInt("SongID");

                                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSongToPlaylistQuery)) {
                                        insertStmt.setInt(1, playlistID);
                                        insertStmt.setInt(2, songID);
                                        int rowsAffected = insertStmt.executeUpdate();
                                        if (rowsAffected > 0) {
                                            JOptionPane.showMessageDialog(this, "Şarkı başarıyla eklendi.");
                                        } else {
                                            JOptionPane.showMessageDialog(this, "Şarkı eklenirken bir hata oluştu.");
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(this, "Seçilen şarkı bulunamadı.");
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Çalma listesi bulunamadı.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            userProfileGUI gui = new userProfileGUI("exampleUser");
            gui.setVisible(true);
        });
    }
}
