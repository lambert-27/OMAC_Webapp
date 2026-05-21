package OMAC_Webapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HourLogDAO {

    public void insert(HourLog log) throws SQLException {
        String sql = "INSERT INTO hour_log (first_name, last_name, level, log_date, hours, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getFirstName());
            ps.setString(2, log.getLastName());
            ps.setString(3, log.getLevel());
            ps.setDate(4, Date.valueOf(log.getLogDate()));
            ps.setDouble(5, log.getHours());
            ps.setString(6, log.getDescription());
            ps.executeUpdate();
        }
    }

    public Map<LocalDate, Double> getHoursPerDay(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT log_date, SUM(hours) as total FROM hour_log WHERE log_date BETWEEN ? AND ? GROUP BY log_date";
        Map<LocalDate, Double> result = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getDate("log_date").toLocalDate(), rs.getDouble("total"));
            }
        }
        return result;
    }
}