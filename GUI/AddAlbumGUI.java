package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddAlbumGUI extends JFrame {

    private JTextField albumAdiField;
    private JLabel sanatciLabel;
    private JButton ekleButton;
    private JPanel albümlerPanel;
    private String loggedInUsername;

    public AddAlbumGUI(String username) {
        this.loggedInUsername = username;
        setTitle("Yeni Albüm Ekleme");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel albumAdiLabel = new JLabel("Albüm Adı:");
        albumAdiField = new JTextField();
        sanatciLabel = new JLabel();
        ekleButton = new JButton("Ekle");
        albümlerPanel = new JPanel();
        albümlerPanel.setLayout(new BoxLayout(albümlerPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(albümlerPanel);

        panel.add(albumAdiLabel);
        panel.add(albumAdiField);
        panel.add(new JLabel("Sanatçı:"));
        panel.add(sanatciLabel);
        panel.add(new JLabel());
        panel.add(ekleButton);
        panel.add(new JLabel("Mevcut Albümler:"));
        panel.add(scrollPane);

        ekleButton.addActionListener(e -> {
            String albumAdi = albumAdiField.getText();
            if (albumAdi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Albüm adı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
            } else {
                ekleAlbum(albumAdi);
                listeleMevcutAlbümler();
            }
        });

        setSanatciAdi();
        listeleMevcutAlbümler();
        add(panel);
    }

    private void setSanatciAdi() {
        sanatciLabel.setText(this.loggedInUsername);
    }

    private void ekleAlbum(String albumAdi) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement artistIDSorgu = conn.prepareStatement("SELECT ArtistID FROM Artist WHERE ArtistName = ?");
            artistIDSorgu.setString(1, loggedInUsername);
            ResultSet resultSet = artistIDSorgu.executeQuery();
            int artistID = -1;
            if (resultSet.next()) {
                artistID = resultSet.getInt("ArtistID");
            } else {
                JOptionPane.showMessageDialog(this, "Sanatçı bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement eklemeStmt = conn.prepareStatement("INSERT INTO Album (Title, ArtistID) VALUES (?, ?)");
            eklemeStmt.setString(1, albumAdi);
            eklemeStmt.setInt(2, artistID);
            int etkilenenSatirSayisi = eklemeStmt.executeUpdate();

            if (etkilenenSatirSayisi > 0) {
                JOptionPane.showMessageDialog(this, "Yeni albüm başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Yeni albüm eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listeleMevcutAlbümler() {
        albümlerPanel.removeAll();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755")) {
            PreparedStatement artistIDSorgu = conn.prepareStatement("SELECT ArtistID FROM Artist WHERE ArtistName = ?");
            artistIDSorgu.setString(1, loggedInUsername);
            ResultSet resultSet = artistIDSorgu.executeQuery();
            int artistID = -1;
            if (resultSet.next()) {
                artistID = resultSet.getInt("ArtistID");
            } else {
                JOptionPane.showMessageDialog(this, "Sanatçı bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement albümlerSorgu = conn.prepareStatement("SELECT AlbumID, Title FROM Album WHERE ArtistID = ?");
            albümlerSorgu.setInt(1, artistID);
            resultSet = albümlerSorgu.executeQuery();
            while (resultSet.next()) {
                int albumID = resultSet.getInt("AlbumID");
                String albumAdi = resultSet.getString("Title");
                JButton albümButton = new JButton(albumAdi);
                albümButton.addActionListener(e -> openAddSongGUI(albumID, albumAdi));
                albümlerPanel.add(albümButton);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        albümlerPanel.revalidate();
        albümlerPanel.repaint();
    }

    private void openAddSongGUI(int albumID, String albumAdi) {
        AddSongGUI addSongGUI = new AddSongGUI(albumID, albumAdi);
        addSongGUI.setVisible(true);
    }
}
