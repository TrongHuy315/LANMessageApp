package src.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDAO {
    public static void save(String sender, String content) {
        String sql = "INSERT INTO messages (sender, content) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sender);
            stmt.setString(2, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DB] Không thể lưu tin nhắn: " + e.getMessage());
        }
    }
}
