package sk.upjs.ics;

import sk.upjs.ics.daos.interfaces.*;
import sk.upjs.ics.daos.sql.*;
import sk.upjs.ics.exceptions.CouldNotConnectToDatabaseException;
import sk.upjs.ics.security.AuthDao;
import sk.upjs.ics.security.SQLAuthDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum Factory {
    INSTANCE;

    public static final String DB_URL = "jdbc:sqlite:fitflow.db";

    private volatile Connection connection;
    private volatile ReservationDao reservationDao;
    private volatile UserDao userDao;
    private volatile TransactionTypeDao transactionTypeDao;
    private volatile CreditTransactionDao creditTransactionDao;
    private volatile RoleDao roleDao;
    private volatile ReservationStatusDao reservationStatusDao;
    private volatile VisitDao visitDao;
    private volatile AuthDao authDao;
    private volatile SpecializationDao specializationDao;
    private volatile TrainerIntervalDao trainerIntervalDao;

    private final Object lock = new Object();

    public Connection getConnection() {
        if (connection == null) {
            synchronized (lock) {
                System.out.println("Connecting to database...");
                try {
                    connection = DriverManager.getConnection(DB_URL);
                } catch (SQLException e) {
                    throw new CouldNotConnectToDatabaseException("Could not connect to the database.");
                }
                System.out.println("Connection successful!");
            }
        }

        return connection;
    }

    public ReservationDao getReservationDao() {
        if (reservationDao == null) {
            synchronized (lock) {
                if (reservationDao == null) {
                    reservationDao = new SQLReservationDao(getConnection());
                }
            }
        }
        return reservationDao;
    }

    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (lock) {
                if (userDao == null) {
                    userDao = new SQLUserDao(getConnection());
                }
            }
        }
        return userDao;
    }

    public TransactionTypeDao getTransactionTypeDao() {
        if (transactionTypeDao == null) {
            synchronized (lock) {
                if (transactionTypeDao == null) {
                    transactionTypeDao = new SQLTransactionTypeDao(getConnection());
                }
            }
        }
        return transactionTypeDao;
    }

    public CreditTransactionDao getCreditTransactionDao() {
        if (creditTransactionDao == null) {
            synchronized (lock) {
                if (creditTransactionDao == null) {
                    creditTransactionDao = new SQLCreditTransactionDao(getConnection());
                }
            }
        }
        return creditTransactionDao;
    }

    public RoleDao getRoleDao() {
        if (roleDao == null) {
            synchronized (lock) {
                if (roleDao == null) {
                    roleDao = new SQLRoleDao(getConnection());
                }
            }
        }
        return roleDao;
    }

    public ReservationStatusDao getReservationStatusDao() {
        if (reservationStatusDao == null) {
            synchronized (lock) {
                if (reservationStatusDao == null) {
                    reservationStatusDao = new SQLReservationStatusDao(getConnection());
                }
            }
        }
        return reservationStatusDao;
    }

    public VisitDao getVisitDao() {
        if (visitDao == null) {
            synchronized (lock) {
                if (visitDao == null) {
                    visitDao = new SQLVisitDao(getConnection());
                }
            }
        }
        return visitDao;
    }

    public AuthDao getAuthDao() {
        if (authDao == null) {
            synchronized (lock) {
                if (authDao == null) {
                    authDao = new SQLAuthDao(getConnection());
                }
            }
        }
        return authDao;
    }

    public SpecializationDao getSpecializationDao() {
        if (specializationDao == null) {
            synchronized (lock) {
                if (specializationDao == null) {
                    specializationDao = new SQLSpecializationDao(getConnection());
                }
            }
        }
        return specializationDao;
    }

    public TrainerIntervalDao getTrainerIntervalDao() {
        if (trainerIntervalDao == null) {
            synchronized (lock) {
                if (trainerIntervalDao == null) {
                    trainerIntervalDao = new SQLTrainerIntervalDao(getConnection());
                }
            }
        }
        return trainerIntervalDao;
    }

}
