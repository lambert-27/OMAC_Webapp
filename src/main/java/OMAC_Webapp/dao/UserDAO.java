package OMAC_Webapp.dao;

import OMAC_Webapp.model.User;
import OMAC_Webapp.util.OmacIdGenerator;

import java.sql.*;

public class UserDAO {

    private static final int ID_GENERATION_ATTEMPTS = 100;

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

    public User insertWithGeneratedOmacId(String firstName, String lastName, String level, String unitLocation) throws SQLException {
        for (int attempt = 0; attempt < ID_GENERATION_ATTEMPTS; attempt++) {
            User user = new User(OmacIdGenerator.generate(), firstName, lastName, level, unitLocation);
            try {
                insert(user);
                return user;
            } catch (SQLIntegrityConstraintViolationException e) {
                if (!isDuplicateKey(e)) {
                    throw e;
                }
            }
        }

        throw new SQLException("Unable to generate a unique OMAC ID. Please try again.");
    }

    public int update(User user) throws SQLException {
        String sql = "UPDATE user SET first_name = ?, last_name = ?, level = ?, unit_location = ? WHERE omac_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getLevel());
            ps.setString(4, user.getUnitLocation());
            ps.setString(5, user.getOmacId());
            return ps.executeUpdate();
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

    private boolean isDuplicateKey(SQLIntegrityConstraintViolationException e) {
        return "23000".equals(e.getSQLState()) || e.getErrorCode() == 1062;
    }
}
