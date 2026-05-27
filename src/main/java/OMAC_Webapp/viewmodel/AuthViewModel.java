package OMAC_Webapp.viewmodel;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String newOmacId;

    private final UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    private static final List<String> LEVELS = Arrays.asList(
            "CFR - Cardiac First Response",
            "FAR - First Aid Response",
            "EFR - Emergency First Responder",
            "EMT - Emergency Medical Technician",
            "P - Paramedic",
            "AP - Advanced Paramedic"
    );

    public List<String> getLevels() { return LEVELS; }

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
    @NotifyChange({"currentUser", "userFound"})
    public void lookupUser() {
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
    @NotifyChange({"showRegister", "currentUser", "userFound"})
    public void registerUser() {
        try {
            User user = new User(newOmacId, newFirstName, newLastName, newLevel);
            userDAO.insert(user);
            currentUser = user;
            omacId = newOmacId;
            userFound = true;
            showRegister = false;
            newOmacId = null;
            newFirstName = null;
            newLastName = null;
            newLevel = null;
            notifyUserLoggedIn();
        } catch (SQLException e) {
            Messagebox.show("Failed to register user: " + e.getMessage());
        }
    }

    private void notifyUserLoggedIn() {
        Sessions.getCurrent().setAttribute("omacId", omacId);
        Sessions.getCurrent().setAttribute("currentUser", currentUser);
        BindUtils.postGlobalCommand(null, null, "onUserLoggedIn", null);
    }
}