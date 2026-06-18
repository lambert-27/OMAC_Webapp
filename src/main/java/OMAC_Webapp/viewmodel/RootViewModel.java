package OMAC_Webapp.viewmodel;

import OMAC_Webapp.model.User;
import lombok.Getter;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;

/**
 * RootViewModel observes global login events so application-level state can
 * update when AuthViewModel publishes a successful login.
 */
@Getter
public class RootViewModel {

    private boolean userLoggedIn = false;
    private boolean darkMode = false;
    private User currentUser;

    @Init
    @NotifyChange({"userLoggedIn", "currentUser"})
    public void init() {
        loadCurrentUser();
    }

    @GlobalCommand
    @NotifyChange({"userLoggedIn", "currentUser"})
    public void onUserLoggedIn() {
        loadCurrentUser();
    }

    @Command
    @NotifyChange({"darkMode", "themeClass"})
    public void toggleDark() {
        darkMode = !darkMode;
    }

    public String getThemeClass() {
        return darkMode ? "dark" : "";
    }

    private void loadCurrentUser() {
        currentUser = (User) Sessions.getCurrent().getAttribute("currentUser");
        userLoggedIn = currentUser != null;
    }
}
