package GUI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AlbumGuncelleme {

    public static void main(String[] args) {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tempotunes", "root", "jusnitorenK8755");
            
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Yeni albüm adını girin: ");
            String yeniAlbumAdi = scanner.nextLine();

       
            String artistAdi = "Son Feci Bisiklet"; 
            PreparedStatement artistIDSorgu = conn.prepareStatement("SELECT ArtistID FROM TempoTunes.Artist WHERE ArtistName = ?");
            artistIDSorgu.setString(1, artistAdi);
            
            int artistID;
            try (ResultSet resultSet = artistIDSorgu.executeQuery()) {
                if (resultSet.next()) {
                    artistID = resultSet.getInt("ArtistID");
                } else {
                    System.out.println("Belirtilen sanatçı bulunamadı.");
                    return;
                }
            }

           
            PreparedStatement guncellemeStmt = conn.prepareStatement("UPDATE TempoTunes.Album SET title = ? WHERE ArtistID = ?");
            guncellemeStmt.setString(1, yeniAlbumAdi);
            guncellemeStmt.setInt(2, artistID);
            int etkilenenSatirSayisi = guncellemeStmt.executeUpdate();
            
            if (etkilenenSatirSayisi > 0) {
                System.out.println("Albüm başarıyla güncellendi.");
            } else {
                System.out.println("Albüm güncellenirken bir hata oluştu.");
            }

            
            scanner.close();
            artistIDSorgu.close();
            guncellemeStmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
