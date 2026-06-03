package OMAC_Webapp.dao;

/**
 * DAOFactory uses the Factory design pattern to focus the creation and
 * access of DAO objects to one Factory, giving the application one place to retrieve shared
 * data access classes such as UserDAO and HourLogDAO.
 */
public class DAOFactory {

    private static DAOFactory instance;

    private final HourLogDAO hourLogDAO;
    private final UserDAO userDAO;

    private DAOFactory() {
        this.hourLogDAO = new HourLogDAO();
        this.userDAO = new UserDAO();
    }

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public HourLogDAO getHourLogDAO() { return hourLogDAO; }
    public UserDAO getUserDAO() { return userDAO; }
}
