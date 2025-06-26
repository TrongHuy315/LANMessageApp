package src.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import src.security.DeviceUtils;

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
            String sql = "INSERT INTO users (username, mac_address, avatar) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, macAddress);
            stmt.setString(3, ".../images/default_image.jpg");
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println("[DB] Register Failed: " + e.getMessage());

            return false;
        }
    }

    public static String getAvatarUrl(String macAddress) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT avatar FROM users WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, macAddress);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("avatar");
            }

            return ".../images/default_image.jpg";
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());

            return ".../images/default_image.jpg";
        }
    }

    public static boolean setAvatarUrl(String macAddress, String avatarUrl) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "UPDATE users SET avatar = ? WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, avatarUrl);
            stmt.setString(2, macAddress);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());

            return false;
        }
    }

    public static boolean checkUsername(String username) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT 1 FROM users WHERE username = ? AND mac_address != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, DeviceUtils.getMacAddress());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());

            return true;
        }
    }

    public static String getCurrentUsername() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT username FROM users WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, DeviceUtils.getMacAddress());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

            return null;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());

            return null;
        }
    }

    public static boolean setUsername(String username) {   // Da bao gom check username xem co trung khong
        boolean checkUsername = checkUsername(username);
        if (checkUsername) return false;

        try (Connection conn = DBConnection.connect()) {
            String sql = "UPDATE users SET username = ? WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, DeviceUtils.getMacAddress());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());
            
            return false;
        }
    }

    public static String getCurrentBio() {
        try (Connection conn = DBConnection.connect()) {
            String sql = "SELECT bio FROM users WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, DeviceUtils.getMacAddress());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("bio");
            }

            return null;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());

            return null;
        }
    }

    public static boolean setBio(String bio) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "UPDATE users SET bio = ? WHERE mac_address = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, bio);
            stmt.setString(2, DeviceUtils.getMacAddress());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB] Access Error: " + e.getMessage());
            
            return false;
        }
    }
}
