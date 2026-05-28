package OMAC_Webapp.viewmodel;

import OMAC_Webapp.constants.TrainingLevels;
import OMAC_Webapp.constants.UnitLocations;
import OMAC_Webapp.dao.DAOFactory;
import OMAC_Webapp.dao.UserDAO;
import OMAC_Webapp.model.User;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles the My Details page, allowing the logged-in user to view and update
 * their stored profile details.
 */
@Getter
@Setter
public class UserDetailsViewModel {

    private String omacId;
    private String firstName;
    private String lastName;
    private String level;
    private String unitLocation;

    private final UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    public List<String> getLevels() { return TrainingLevels.ALL_LEVELS; }
    public List<String> getUnitLocations() { return UnitLocations.ALL_LOCATIONS; }

    @Init
    public void init() {
        loadCurrentUser();
    }

    @GlobalCommand
    @NotifyChange({"omacId", "firstName", "lastName", "level", "unitLocation"})
    public void onUserLoggedIn() {
        loadCurrentUser();
    }

    @Command
    @NotifyChange({"omacId", "firstName", "lastName", "level", "unitLocation"})
    public void resetDetails() {
        loadCurrentUser();
    }

    @Command
    @NotifyChange({"firstName", "lastName", "level", "unitLocation"})
    public void saveDetails() {
        if (isBlank(omacId)) {
            Messagebox.show("Please log in before updating your details.");
            return;
        }
        if (isBlank(firstName) || isBlank(lastName) || isBlank(level) || isBlank(unitLocation)) {
            Messagebox.show("Please complete all details before saving.");
            return;
        }

        User updatedUser = new User(omacId, firstName, lastName, level, unitLocation);
        try {
            userDAO.update(updatedUser);
            Sessions.getCurrent().setAttribute("currentUser", updatedUser);
            BindUtils.postGlobalCommand(null, null, "onUserDetailsUpdated", null);
            Messagebox.show("Details updated successfully.");
        } catch (SQLException e) {
            Messagebox.show("Failed to update details: " + e.getMessage());
        }
    }

    private void loadCurrentUser() {
        User currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            return;
        }

        omacId = currentUser.getOmacId();
        firstName = currentUser.getFirstName();
        lastName = currentUser.getLastName();
        level = currentUser.getLevel();
        unitLocation = currentUser.getUnitLocation();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
