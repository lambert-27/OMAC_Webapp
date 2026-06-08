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
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class AuthViewModel {

    private String omacId;
    private User currentUser;
    private boolean userFound;
    private boolean showRegister;
    private String newFirstName;
    private String newLastName;
    private String newLevel;
    private String newUnitLocation;

    private final UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    public List<String> getLevels() { return TrainingLevels.ALL_LEVELS; }
    public List<String> getUnitLocations() { return UnitLocations.ALL_LOCATIONS; }

    @Init
    @NotifyChange({"userFound", "currentUser"})
    public void init() {
        User sessionUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        if (sessionUser != null) {
            currentUser = sessionUser;
            omacId = sessionUser.getOmacId();
            userFound = true;
            notifyUserLoggedIn();
        }
    }

    @Command
    @NotifyChange({"omacId", "currentUser", "userFound"})
    public void lookupUser() {
        omacId = normalizeOmacId(omacId);
        try {
            currentUser = userDAO.findById(omacId);
            if (currentUser == null) {
                userFound = false;
                Messagebox.show("No user found with OMAC ID: " + omacId);
            } else {
                userFound = true;
                notifyUserLoggedIn();
            }
        } catch (SQLException e) {
            Messagebox.show("Failed to lookup user: " + e.getMessage());
        }
    }

    @Command
    @NotifyChange({"showRegister"})
    public void toggleRegister() {
        showRegister = !showRegister;
    }

    @Command
    @NotifyChange({"showRegister", "currentUser", "userFound", "omacId", "newFirstName", "newLastName", "newLevel", "newUnitLocation"})
    public void registerUser() {
        try {
            User user = userDAO.insertWithGeneratedOmacId(newFirstName, newLastName, newLevel, newUnitLocation);
            currentUser = user;
            omacId = user.getOmacId();
            userFound = true;
            showRegister = false;
            newFirstName = null;
            newLastName = null;
            newLevel = null;
            newUnitLocation = null;
            notifyUserLoggedIn();
        } catch (SQLException e) {
            Messagebox.show("Failed to register user: " + e.getMessage());
        }
    }

    /**
     * Publishes a login event through ZK's global command event bus so other
     * ViewModels can observe and react without being directly coupled to auth.
     */
    private void notifyUserLoggedIn() {
        Sessions.getCurrent().setAttribute("omacId", omacId);
        Sessions.getCurrent().setAttribute("currentUser", currentUser);
        BindUtils.postGlobalCommand(null, null, "onUserLoggedIn", null);
    }

    private String normalizeOmacId(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
