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

/**
 * GraphViewModel uses ZK global commands as an Observer,
 * listening for user-login and hours-updated events from other ViewModels.
 */
@Getter
@Setter
public class GraphViewModel {

    private static final String LOG_HOURS_PAGE = "logHours";
    private static final String MY_DETAILS_PAGE = "myDetails";

    private String omacId;
    private Map<LocalDate, Double> hoursPerDay;
    private Integer selectedYear = LocalDate.now().getYear();
    private String activePage = LOG_HOURS_PAGE;
    private String hoursThisWeek = "0";
    private String hoursThisMonth = "0";
    private String hoursThisYear = "0";

    private String displayName;
    private String displayLevel;
    private String displayId;
    private String displayInitial;
    private String unitBadge = "OMAC";

    private final HourLogDAO hourLogDAO = DAOFactory.getInstance().getHourLogDAO();

    @GlobalCommand
    @NotifyChange({"graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear", "displayName", "displayLevel", "displayId", "displayInitial", "unitBadge"})
    public void onUserLoggedIn() {
        loadDisplayUser();
        loadGraph();
        loadTotals();
    }

    @GlobalCommand
    @NotifyChange({"displayName", "displayLevel", "displayId", "displayInitial", "unitBadge"})
    public void onUserDetailsUpdated() {
        loadDisplayUser();
    }

    @GlobalCommand
    @NotifyChange({"graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear"})
    public void onHoursUpdated() {
        loadGraph();
        loadTotals();
    }

    @Command
    @GlobalCommand
    @NotifyChange({"logHoursActive", "myDetailsActive", "logHoursNavClass", "myDetailsNavClass", "pageTitle"})
    public void showLogHours() {
        activePage = LOG_HOURS_PAGE;
    }

    @Command
    @GlobalCommand
    @NotifyChange({"logHoursActive", "myDetailsActive", "logHoursNavClass", "myDetailsNavClass", "pageTitle"})
    public void showMyDetails() {
        activePage = MY_DETAILS_PAGE;
    }

    @Command
    @GlobalCommand
    @NotifyChange({"graphHtml"})
    public void changeYear(@BindingParam("year") Integer year) {
        if (year != null) selectedYear = year;
        loadGraph();
    }

    private void loadGraph() {
        try {
            boolean isCurrentYear = selectedYear.equals(LocalDate.now().getYear());
            LocalDate from = isCurrentYear ? LocalDate.now().minusWeeks(52) : LocalDate.of(selectedYear, 1, 1);
            LocalDate to = isCurrentYear ? LocalDate.now() : LocalDate.of(selectedYear, 12, 31);
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

    public boolean isLogHoursActive() {
        return LOG_HOURS_PAGE.equals(activePage);
    }

    public boolean isMyDetailsActive() {
        return MY_DETAILS_PAGE.equals(activePage);
    }

    public String getLogHoursNavClass() {
        return isLogHoursActive() ? "nav-item active" : "nav-item";
    }

    public String getMyDetailsNavClass() {
        return isMyDetailsActive() ? "nav-item active" : "nav-item";
    }

    public String getPageTitle() {
        return isMyDetailsActive() ? "MY DETAILS" : "HOUR TRACKER";
    }

    public String getGraphHtml() {
        if (hoursPerDay == null) return "";

        boolean isCurrentYear = selectedYear.equals(LocalDate.now().getYear());
        LocalDate today = LocalDate.now();
        LocalDate start = isCurrentYear ? today.minusWeeks(52) : LocalDate.of(selectedYear, 1, 1);
        LocalDate end = isCurrentYear ? today : LocalDate.of(selectedYear, 12, 31);

        LocalDate colStart = start;
        while (colStart.getDayOfWeek().getValue() != 1) {
            colStart = colStart.minusDays(1);
        }

        java.util.List<LocalDate> colStarts = new java.util.ArrayList<>();
        LocalDate col = colStart;
        while (!col.isAfter(end)) {
            colStarts.add(col);
            col = col.plusWeeks(1);
        }

        double totalHours = hoursPerDay.values().stream().mapToDouble(Double::doubleValue).sum();

        StringBuilder sb = new StringBuilder();

        // Summary
        sb.append("<div style='font-size:13px;color:var(--text-secondary);margin-bottom:12px;'>")
                .append(String.format("%.0f", totalHours))
                .append(" hours logged")
                .append(isCurrentYear ? " in the last year" : " in " + selectedYear)
                .append("</div>");

        sb.append("<div style='display:flex;flex-direction:row;gap:4px;'>");

        // Day labels column
        sb.append("<div style='display:flex;flex-direction:column;gap:3px;margin-right:4px;padding-top:18px;'>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'></div>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'>Mon</div>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'></div>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'>Wed</div>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'></div>");
        sb.append("<div style='height:13px;font-size:9px;color:var(--text-secondary);line-height:13px;'>Fri</div>");
        sb.append("<div style='height:13px;'></div>");
        sb.append("</div>");

        // Graph columns wrapper
        sb.append("<div style='display:flex;flex-direction:column;gap:4px;'>");

        // Month labels row
        sb.append("<div style='display:flex;flex-direction:row;gap:3px;'>");
        java.time.Month lastMonth = null;
        for (LocalDate c : colStarts) {
            java.time.Month colMonth = c.getMonth();
            if (!colMonth.equals(lastMonth)) {
                sb.append("<div style='min-width:13px;font-size:9px;color:var(--text-secondary);white-space:nowrap;'>")
                        .append(colMonth.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH))
                        .append("</div>");
                lastMonth = colMonth;
            } else {
                sb.append("<div style='min-width:13px;'></div>");
            }
        }
        sb.append("</div>");

        // Grid
        sb.append("<div style='display:flex;flex-direction:row;gap:3px;'>");
        lastMonth = null;
        for (LocalDate c : colStarts) {
            java.time.Month colMonth = c.getMonth();
            String borderLeft = !colMonth.equals(lastMonth) && lastMonth != null
                    ? "border-left:1px solid var(--border);padding-left:4px;" : "";
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
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");

        // Legend
        sb.append("<div style='display:flex;align-items:center;gap:6px;margin-top:8px;font-size:10px;color:var(--text-secondary);justify-content:flex-end;'>")
                .append("Less")
                .append("<div style='width:13px;height:13px;border-radius:2px;background:#e0e0e0;'></div>")
                .append("<div style='width:13px;height:13px;border-radius:2px;background:#ff9999;'></div>")
                .append("<div style='width:13px;height:13px;border-radius:2px;background:#ff4444;'></div>")
                .append("<div style='width:13px;height:13px;border-radius:2px;background:#cc0000;'></div>")
                .append("More")
                .append("</div>");

        return sb.toString();
    }

    private String getColor(double hours) {
        if (hours == 0)  return "#e0e0e0";
        if (hours <= 2)  return "#ff9999";
        if (hours <= 4)  return "#ff4444";
        return "#cc0000";
    }

    private String buildUnitBadge(String unitLocation) {
        if (unitLocation == null || unitLocation.trim().isEmpty()) {
            return "OMAC";
        }
        return "OMAC - " + unitLocation;
    }

    private void loadDisplayUser() {
        User currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            return;
        }

        String sessionOmacId = (String) Sessions.getCurrent().getAttribute("omacId");
        this.omacId = sessionOmacId != null ? sessionOmacId : currentUser.getOmacId();
        this.displayName = currentUser.getFirstName() + " " + currentUser.getLastName();
        this.displayLevel = currentUser.getLevel();
        this.displayId = currentUser.getOmacId();
        this.displayInitial = getInitial(currentUser.getFirstName());
        this.unitBadge = buildUnitBadge(currentUser.getUnitLocation());
    }

    private String getInitial(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return "";
        }
        return firstName.substring(0, 1);
    }
}
