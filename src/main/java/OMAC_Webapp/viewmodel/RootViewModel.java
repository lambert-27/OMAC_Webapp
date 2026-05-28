package OMAC_Webapp.viewmodel;

import OMAC_Webapp.model.User;
import lombok.Getter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.BindUtils;

/**
 * RootViewModel observes global login events so application-level state can
 * update when AuthViewModel publishes a successful login.
 */
@Getter
public class RootViewModel {

    private boolean userLoggedIn = false;
    private boolean darkMode = false;
    private User currentUser;

    @GlobalCommand
    @NotifyChange({"userLoggedIn", "currentUser"})
    public void onUserLoggedIn(@org.zkoss.bind.annotation.BindingParam("currentUser") User currentUser) {
        userLoggedIn = true;
        this.currentUser = currentUser;
    }

    @Command
    @NotifyChange({"darkMode", "themeClass"})
    public void toggleDark() {
        darkMode = !darkMode;
    }

    public String getThemeClass() {
        return darkMode ? "dark" : "";
    }
}
