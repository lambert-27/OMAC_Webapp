package OMAC_Webapp.dao;

import OMAC_Webapp.model.User;

import java.sql.*;

public class UserDAO {

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO user (omac_id, first_name, last_name, level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getOmacId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getLevel());
            ps.executeUpdate();
        }
    }

    public User findById(String omacId) throws SQLException {
        String sql = "SELECT * FROM user WHERE omac_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, omacId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("omac_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("level")
                );
            }
        }
        return null;
    }
}