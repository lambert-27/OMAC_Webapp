package OMAC_Webapp.dao;

import OMAC_Webapp.model.User;

import java.sql.*;

public class UserDAO {

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO user (omac_id, first_name, last_name, level, unit_location) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getOmacId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getLevel());
            ps.setString(5, user.getUnitLocation());
            ps.executeUpdate();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET first_name = ?, last_name = ?, level = ?, unit_location = ? WHERE omac_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getLevel());
            ps.setString(4, user.getUnitLocation());
            ps.setString(5, user.getOmacId());
            ps.executeUpdate();
        }
    }

    public User findById(String omacId) throws SQLException {
        String sql = "SELECT * FROM user WHERE omac_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, omacId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("omac_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("level"),
                        rs.getString("unit_location")
                );
            }
        }
        return null;
    }
}
