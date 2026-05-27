package OMAC_Webapp.dao;

import OMAC_Webapp.model.HourLog;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HourLogDAO {

    public void insert(HourLog log) throws SQLException {
        String sql = "INSERT INTO hour_log (omac_id, log_date, hours, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getOmacId());
            ps.setDate(2, Date.valueOf(log.getLogDate()));
            ps.setDouble(3, log.getHours());
            ps.setString(4, log.getDescription());
            ps.executeUpdate();
        }
    }

    public Map<LocalDate, Double> getHoursPerDay(String omacId, LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT log_date, SUM(hours) as total FROM hour_log WHERE omac_id = ? AND log_date BETWEEN ? AND ? GROUP BY log_date";
        Map<LocalDate, Double> result = new HashMap<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, omacId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getDate("log_date").toLocalDate(), rs.getDouble("total"));
            }
        }
        return result;
    }

    public double getTotalHours(String omacId, LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT SUM(hours) as total FROM hour_log WHERE omac_id = ? AND log_date BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, omacId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }
}