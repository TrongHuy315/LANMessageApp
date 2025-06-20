package src.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public static boolean validateLogin(String username, String macAddress) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND mac_address = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, macAddress);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkMacAddress(String macAddress) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT username FROM users WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, macAddress);
            
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean registerUser(String username, String macAddress) {
        if (checkMacAddress(macAddress)) {
            throw new RuntimeException();
        }

        try (Connection conn = DBConnection.connect()) {
            String sql = "INSERT INTO users (username, mac_address) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, macAddress);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("[DB] Đăng ký thất bại: " + e.getMessage());

            return false;
        }
    }
}
