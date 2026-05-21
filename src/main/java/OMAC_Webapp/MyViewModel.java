package OMAC_Webapp;

import org.zkoss.bind.annotation.*;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class MyViewModel {

	private String firstName;
	private String lastName;
	private String level;
	private Date logDate;
	private double hours;
	private String description;

	private Map<LocalDate, Double> hoursPerDay;

	private final HourLogDAO dao = new HourLogDAO();

	@Init
	public void init() {
		logDate = new Date();
		loadGraph();
	}

	private void loadGraph() {
		try {
			LocalDate to = LocalDate.now();
			LocalDate from = to.minusWeeks(52);
			hoursPerDay = dao.getHoursPerDay(from, to);
		} catch (SQLException e) {
			Messagebox.show("Failed to load graph data: " + e.getMessage());
		}
	}

	@Command
	@NotifyChange({"graphHtml"})
	public void submitLog() {
		try {
			LocalDate date = logDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			HourLog log = new HourLog(0, firstName, lastName, level, date, hours, description);
			dao.insert(log);
			loadGraph();
			clearForm();
		} catch (SQLException e) {
			Messagebox.show("Failed to save log: " + e.getMessage());
		}
	}

	@Command
	@NotifyChange({"firstName", "lastName", "level", "logDate", "hours", "description"})
	public void clearForm() {
		firstName = null;
		lastName = null;
		level = null;
		logDate = new Date();
		hours = 0;
		description = null;
	}

	public String getGraphHtml() {
		LocalDate today = LocalDate.now();
		LocalDate start = today.minusWeeks(52);

		while (start.getDayOfWeek().getValue() != 1) {
			start = start.minusDays(1);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<div style='display:flex;flex-direction:row;gap:3px;padding:20px;background:#ffffff;border-radius:8px;width:fit-content;'>");

		LocalDate colStart = start;
		while (!colStart.isAfter(today)) {
			sb.append("<div style='display:flex;flex-direction:column;gap:3px;'>");
			for (int day = 0; day < 7; day++) {
				LocalDate cell = colStart.plusDays(day);
				if (cell.isAfter(today)) {
					sb.append("<div style='width:13px;height:13px;border-radius:2px;background:transparent;'></div>");
				} else {
					double h = hoursPerDay != null && hoursPerDay.containsKey(cell) ? hoursPerDay.get(cell) : 0;
					String color = getColor(h);
					String tooltip = cell + (h > 0 ? ": " + h + "hrs" : ": no hours");
					sb.append("<div title='").append(tooltip).append("' style='width:13px;height:13px;border-radius:2px;background:").append(color).append(";'></div>");
				}
			}
			sb.append("</div>");
			colStart = colStart.plusWeeks(1);
		}

		sb.append("</div>");
		return sb.toString();
	}

	private String getColor(double hours) {
		if (hours == 0)  return "#e0e0e0";
		if (hours <= 2)  return "#ff9999";
		if (hours <= 4)  return "#ff4444";
		return "#cc0000";
	}

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }

	public Date getLogDate() { return logDate; }
	public void setLogDate(Date logDate) { this.logDate = logDate; }

	public double getHours() { return hours; }
	public void setHours(double hours) { this.hours = hours; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Map<LocalDate, Double> getHoursPerDay() { return hoursPerDay; }
}