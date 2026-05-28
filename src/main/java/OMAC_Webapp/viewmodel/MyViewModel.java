package OMAC_Webapp.viewmodel;

import OMAC_Webapp.constants.TrainingLevels;
import OMAC_Webapp.constants.UnitLocations;
import OMAC_Webapp.dao.DAOFactory;
import OMAC_Webapp.dao.HourLogDAO;
import OMAC_Webapp.dao.UserDAO;
import OMAC_Webapp.model.HourLog;
import OMAC_Webapp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class MyViewModel {

	private String omacId;
	private User currentUser;
	private Date logDate;
	private double hours;
	private String description;
	private boolean userFound;
	private boolean showRegister;
	private String newFirstName;
	private String newLastName;
	private String newLevel;
	private String newUnitLocation;
	private String newOmacId;
	private Integer selectedYear = LocalDate.now().getYear();

	private double hoursThisWeek;
	private double hoursThisMonth;
	private double hoursThisYear;

	private Map<LocalDate, Double> hoursPerDay;

	private final HourLogDAO hourLogDAO = DAOFactory.getInstance().getHourLogDAO();
	private final UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

	@Init
	public void init() {
		logDate = new Date();
	}

	@Command
	@NotifyChange({"currentUser", "graphHtml", "userFound", "hoursThisWeek", "hoursThisMonth", "hoursThisYear"})
	public void lookupUser() {
		try {
			currentUser = userDAO.findById(omacId);
			if (currentUser == null) {
				userFound = false;
				Messagebox.show("No user found with OMAC ID: " + omacId);
			} else {
				userFound = true;
				loadGraph();
			}
		} catch (SQLException e) {
			Messagebox.show("Failed to lookup user: " + e.getMessage());
		}
	}

	@Command
	@NotifyChange({"graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear"})
	public void submitLog() {
		if (currentUser == null) {
			Messagebox.show("Please look up a user first.");
			return;
		}
		try {
			LocalDate date = logDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			HourLog log = new HourLog(0, omacId, date, hours, description);
			hourLogDAO.insert(log);
			loadGraph();
			clearForm();
		} catch (SQLException e) {
			Messagebox.show("Failed to save log: " + e.getMessage());
		}
	}

	@Command
	@NotifyChange({"logDate", "hours", "description"})
	public void clearForm() {
		logDate = new Date();
		hours = 0;
		description = null;
	}

	@Command
	@NotifyChange({"showRegister"})
	public void toggleRegister() {
		showRegister = !showRegister;
	}

	@Command
	@NotifyChange({"showRegister", "currentUser", "userFound", "graphHtml", "hoursThisWeek", "hoursThisMonth", "hoursThisYear", "newUnitLocation"})
	public void registerUser() {
		try {
			User user = new User(newOmacId, newFirstName, newLastName, newLevel, newUnitLocation);
			userDAO.insert(user);
			currentUser = user;
			omacId = newOmacId;
			userFound = true;
			showRegister = false;
			newOmacId = null;
			newFirstName = null;
			newLastName = null;
			newLevel = null;
			newUnitLocation = null;
			loadGraph();
		} catch (SQLException e) {
			Messagebox.show("Failed to register user: " + e.getMessage());
		}
	}

	@Command
	@NotifyChange({"graphHtml"})
	public void changeYear(@BindingParam("year") Integer year) {
		if (year != null) selectedYear = year;
		try {
			LocalDate from = LocalDate.of(selectedYear, 1, 1);
			LocalDate to = selectedYear.equals(LocalDate.now().getYear())
					? LocalDate.now()
					: LocalDate.of(selectedYear, 12, 31);
			hoursPerDay = hourLogDAO.getHoursPerDay(omacId, from, to);
		} catch (SQLException e) {
			Messagebox.show("Failed to load graph data: " + e.getMessage());
		}
	}

	private void loadGraph() {
		try {
			LocalDate from = LocalDate.of(selectedYear, 1, 1);
			LocalDate to = selectedYear.equals(LocalDate.now().getYear())
					? LocalDate.now()
					: LocalDate.of(selectedYear, 12, 31);
			hoursPerDay = hourLogDAO.getHoursPerDay(omacId, from, to);
			loadTotals();
		} catch (SQLException e) {
			Messagebox.show("Failed to load graph data: " + e.getMessage());
		}
	}

	public java.util.List<String> getLevels() {
		return TrainingLevels.ALL_LEVELS;
	}

	public java.util.List<String> getUnitLocations() {
		return UnitLocations.ALL_LOCATIONS;
	}

	public java.util.List<Integer> getYearOptions() {
		java.util.List<Integer> years = new java.util.ArrayList<>();
		int current = LocalDate.now().getYear();
		for (int y = current; y >= current - 5; y--) {
			years.add(y);
		}
		return years;
	}

	private void loadTotals() {
		try {
			LocalDate today = LocalDate.now();
			LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
			LocalDate monthStart = today.withDayOfMonth(1);
			LocalDate yearStart = today.withDayOfYear(1);

			hoursThisWeek = hourLogDAO.getTotalHours(omacId, weekStart, today);
			hoursThisMonth = hourLogDAO.getTotalHours(omacId, monthStart, today);
			hoursThisYear = hourLogDAO.getTotalHours(omacId, yearStart, today);
		} catch (SQLException e) {
			Messagebox.show("Failed to load totals: " + e.getMessage());
		}
	}

	public String getGraphHtml() {
		if (hoursPerDay == null) return "";

		LocalDate start = LocalDate.of(selectedYear, 1, 1);
		LocalDate end = LocalDate.of(selectedYear, 12, 31);
		LocalDate today = LocalDate.now();

		// rewind to nearest Monday
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

		StringBuilder sb = new StringBuilder();
		sb.append("<div style='display:flex;flex-direction:column;gap:4px;'>");

		// Month labels
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

		// Grid
		sb.append("<div style='display:flex;flex-direction:row;gap:3px;'>");
		lastMonth = null;
		for (LocalDate c : colStarts) {
			java.time.Month colMonth = c.getMonth();
			String borderLeft = !colMonth.equals(lastMonth) && lastMonth != null
					? "border-left:1px solid #2a2a2a;padding-left:4px;"
					: "";
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

	public int getSelectedYearIndex() {
		return getYearOptions().indexOf(selectedYear);
	}

	private String getColor(double hours) {
		if (hours == 0)  return "#e0e0e0";
		if (hours <= 2)  return "#ff9999";
		if (hours <= 4)  return "#ff4444";
		return "#cc0000";
	}
}
