package database;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User registerUser(User user) {
        String sql = "INSERT INTO users (name, password, mobile_number, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getMobileNumber());
            pstmt.setString(4, user.getEmail());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                } catch (SQLException e) {
                    System.out.println("Creating user failed, no ID obtained." + e.getMessage());
                    return null;
                }
                return user;
            } else {
                System.out.println("Creating user failed, no rows affected.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return null;
        }
    }


    public User loginUser(String email, String password) {
        String sql = "SELECT user_id, name, password, mobile_number, email FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (password.equals(storedPassword)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(storedPassword);
                        user.setMobileNumber(rs.getString("mobile_number"));
                        user.setEmail(rs.getString("email"));
                        return user;
                    } else {
                        System.out.println("Incorrect password.");
                        return null;
                    }
                } else {
                    System.out.println("User not found.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
            return null;
        }
    }


    public User getUserById(int userId) {
        String sql = "SELECT user_id, name, password, mobile_number, email FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setMobileNumber(rs.getString("mobile_number"));
                    user.setEmail(rs.getString("email"));
                    return user;
                } else {
                    System.out.println("User not found with ID: " + userId);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }
}
