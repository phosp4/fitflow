package sk.upjs.ics;

import sk.upjs.ics.reservations.ReservationDao;
import sk.upjs.ics.reservations.SQLReservationDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum Factory {
    INSTANCE;

    public static final String DB_URL = "jdbc:sqlite:fitflow.db";

    private volatile Connection connection;
    private volatile ReservationDao reservationDao;

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
}
