package OMAC_Webapp.viewmodel;

import OMAC_Webapp.dao.DAOFactory;
import OMAC_Webapp.dao.HourLogDAO;
import OMAC_Webapp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GraphViewModel {

    private String omacId;
    private Map<LocalDate, Double> hoursPerDay;
    private Integer selectedYear = LocalDate.now().getYear();
    private String hoursThisWeek = "0";
    private String hoursThisMonth = "0";
    private String hoursThisYear = "0";

    private String displayName;
    private String displayLevel;
    private String displayId;
    private String displayInitial;

    private final HourLogDAO hourLogDAO = DAOFactory.getInstance().getHourLogDAO();

    @GlobalCommand
    @NotifyChange({"graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear", "displayName", "displayLevel", "displayId", "displayInitial"})
    public void onUserLoggedIn() {
        User currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        this.omacId = (String) Sessions.getCurrent().getAttribute("omacId");
        this.displayName = currentUser.getFirstName() + " " + currentUser.getLastName();
        this.displayLevel = currentUser.getLevel();
        this.displayId = currentUser.getOmacId();
        this.displayInitial = currentUser.getFirstName().substring(0, 1);
        loadGraph();
        loadTotals();
    }

    @GlobalCommand
    @NotifyChange({"graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear"})
    public void onHoursUpdated() {
        loadGraph();
        loadTotals();
    }

    @Command
    @NotifyChange({"graphHtml"})
    public void changeYear(@BindingParam("year") Integer year) {
        if (year != null) selectedYear = year;
        loadGraph();
    }

    private void loadGraph() {
        try {
            LocalDate from = LocalDate.of(selectedYear, 1, 1);
            LocalDate to = selectedYear.equals(LocalDate.now().getYear())
                    ? LocalDate.now()
                    : LocalDate.of(selectedYear, 12, 31);
            hoursPerDay = hourLogDAO.getHoursPerDay(omacId, from, to);
        } catch (SQLException e) {
            Messagebox.show("Failed to load graph: " + e.getMessage());
        }
    }

    private void loadTotals() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate yearStart = today.withDayOfYear(1);
            hoursThisWeek = String.valueOf(hourLogDAO.getTotalHours(omacId, weekStart, today));
            hoursThisMonth = String.valueOf(hourLogDAO.getTotalHours(omacId, monthStart, today));
            hoursThisYear = String.valueOf(hourLogDAO.getTotalHours(omacId, yearStart, today));
        } catch (SQLException e) {
            Messagebox.show("Failed to load totals: " + e.getMessage());
        }
    }

    public List<Integer> getYearOptions() {
        List<Integer> years = new ArrayList<>();
        int current = LocalDate.now().getYear();
        for (int y = current; y >= current - 5; y--) {
            years.add(y);
        }
        return years;
    }

    public int getSelectedYearIndex() {
        return getYearOptions().indexOf(selectedYear);
    }

    public String getGraphHtml() {
        if (hoursPerDay == null) return "";

        LocalDate start = LocalDate.of(selectedYear, 1, 1);
        LocalDate end = LocalDate.of(selectedYear, 12, 31);
        LocalDate today = LocalDate.now();

        LocalDate colStart = start;
        while (colStart.getDayOfWeek().getValue() != 1) {
            colStart = colStart.minusDays(1);
        }

        List<LocalDate> colStarts = new ArrayList<>();
        LocalDate col = colStart;
        while (!col.isAfter(end)) {
            colStarts.add(col);
            col = col.plusWeeks(1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='display:flex;flex-direction:column;gap:4px;'>");

        sb.append("<div style='display:flex;flex-direction:row;gap:3px;margin-bottom:4px;'>");
        java.time.Month lastMonth = null;
        for (LocalDate c : colStarts) {
            java.time.Month colMonth = c.getMonth();
            if (!colMonth.equals(lastMonth)) {
                sb.append("<div style='min-width:13px;font-size:10px;color:#555;font-family:DM Sans,sans-serif;letter-spacing:1px;'>")
                        .append(colMonth.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH))
                        .append("</div>");
                lastMonth = colMonth;
            } else {
                sb.append("<div style='min-width:13px;'></div>");
            }
        }
        sb.append("</div>");

        sb.append("<div style='display:flex;flex-direction:row;gap:3px;'>");
        lastMonth = null;
        for (LocalDate c : colStarts) {
            java.time.Month colMonth = c.getMonth();
            String borderLeft = !colMonth.equals(lastMonth) && lastMonth != null
                    ? "border-left:1px solid #2a2a2a;padding-left:4px;" : "";
            lastMonth = colMonth;

            sb.append("<div style='display:flex;flex-direction:column;gap:3px;").append(borderLeft).append("'>");
            for (int day = 0; day < 7; day++) {
                LocalDate cell = c.plusDays(day);
                if (cell.isBefore(start) || cell.isAfter(end) || cell.isAfter(today)) {
                    sb.append("<div style='width:13px;height:13px;border-radius:2px;background:transparent;'></div>");
                } else {
                    double h = hoursPerDay.containsKey(cell) ? hoursPerDay.get(cell) : 0;
                    String color = getColor(h);
                    String tooltip = cell + (h > 0 ? ": " + h + "hrs" : ": no hours");
                    sb.append("<div title='").append(tooltip)
                            .append("' style='width:13px;height:13px;border-radius:2px;background:")
                            .append(color).append(";'></div>");
                }
            }
            sb.append("</div>");
        }
        sb.append("</div></div>");

        return sb.toString();
    }

    private String getColor(double hours) {
        if (hours == 0)  return "#e0e0e0";
        if (hours <= 2)  return "#ff9999";
        if (hours <= 4)  return "#ff4444";
        return "#cc0000";
    }
}