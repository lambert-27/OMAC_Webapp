package OMAC_Webapp.dao;

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