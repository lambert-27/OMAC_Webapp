package OMAC_Webapp.viewmodel;

import OMAC_Webapp.dao.DAOFactory;
import OMAC_Webapp.dao.HourLogDAO;
import OMAC_Webapp.model.HourLog;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HourLogViewModel {

    private String omacId;
    private Date logDate;
    private double hours;
    private String description;

    private final HourLogDAO hourLogDAO = DAOFactory.getInstance().getHourLogDAO();

    @Init
    public void init() {
        logDate = new Date();
    }

    @GlobalCommand
    @NotifyChange({"omacId"})
    public void onUserLoggedIn(@BindingParam("omacId") String omacId) {
        this.omacId = omacId;
    }

    @Command
    @NotifyChange({"logDate", "hours", "description"})
    public void submitLog() {
        if (omacId == null) {
            Messagebox.show("Please log in first.");
            return;
        }
        try {
            LocalDate date = logDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            HourLog log = new HourLog(0, omacId, date, hours, description);
            hourLogDAO.insert(log);
            clearForm();
            BindUtils.postGlobalCommand(null, null, "onHoursUpdated", new HashMap<>());
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
}