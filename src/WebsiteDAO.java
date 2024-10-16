import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebsiteDAO {
    public List<Website> getAllWebsites() throws SQLException {
        List<Website> websites = new ArrayList<>();
        String query = "SELECT * FROM websites";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Website website = new Website(rs.getInt("id"), rs.getString("url"), rs.getString("display_text"));
                websites.add(website);
            }
        }
        return websites;
    }

    public void addWebsite(Website website) throws SQLException {
        String query = "INSERT INTO websites (url, display_text) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, website.getUrl());
            stmt.setString(2, website.getDisplayText());
            stmt.executeUpdate();
        }
    }

    public void deleteWebsite(int id) throws SQLException {
        String query = "DELETE FROM websites WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
