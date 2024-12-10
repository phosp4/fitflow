package sk.upjs.ics;

import sk.upjs.ics.reservations.ReservationDao;
import sk.upjs.ics.reservations.SQLReservationDao;
import sk.upjs.ics.users.SQLUserDao;
import sk.upjs.ics.users.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum Factory {
    INSTANCE;

    public static final String DB_URL = "jdbc:sqlite:fitflow.db";

    private volatile Connection connection;
    private volatile ReservationDao reservationDao;
    private volatile UserDao userDao;

    private final Object lock = new Object();

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            synchronized (lock) {
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Connection successful!");
            }
        }

        return connection;
    }

    public ReservationDao getReservationDao() throws SQLException {
        if (reservationDao == null) {
            synchronized (lock) {
                if (reservationDao == null) {
                    reservationDao = new SQLReservationDao(getConnection());
                }
            }
        }
        return reservationDao;
    }

    public UserDao getUserDao() throws SQLException {
        if (userDao == null) {
            synchronized (lock) {
                if (userDao == null) {
                    userDao = new SQLUserDao(getConnection());
                }
            }
        }
        return userDao;
    }

}
