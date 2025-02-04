package sk.upjs.ics;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.sqlite.SQLiteDataSource;
import sk.upjs.ics.daos.interfaces.*;
import sk.upjs.ics.daos.sql.*;
import sk.upjs.ics.exceptions.CouldNotConnectToDatabaseException;
import sk.upjs.ics.security.AuthDao;
import sk.upjs.ics.security.SQLAuthDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Factory class to provide singleton instances of DAOs and manage database connections.
 */
public enum Factory {
    INSTANCE;

    /**
     * The URL of the database.
     */
    public static final String DB_URL = System.getProperty("DB_URL", "jdbc:sqlite:fitflow.db");

    private volatile JdbcOperations jdbcOperations;
    private volatile UserDao userDao;
    private volatile TransactionTypeDao transactionTypeDao;
    private volatile CreditTransactionDao creditTransactionDao;
    private volatile RoleDao roleDao;
    private volatile VisitDao visitDao;
    private volatile AuthDao authDao;

    private final Object lock = new Object();

    /**
     * Returns a singleton instance of the jdbc operations object.
     *
     * @return the SQLJdbcOperations object
     * @throws CouldNotConnectToDatabaseException if a database access error occurs
     */
    public JdbcOperations getSQLJdbcOperations() {
        if (jdbcOperations == null) {
            synchronized (lock) {
                System.out.println("Connecting to database...");
                if (jdbcOperations == null) {
                    var dataSource = new SQLiteDataSource();
                    dataSource.setUrl(DB_URL);
                    jdbcOperations = new JdbcTemplate(dataSource);
                    System.out.println("Connection successful!");
                }
            }
        }

        return jdbcOperations;
    }

    /**
     * Returns a singleton instance of the UserDao.
     *
     * @return the UserDao
     */
    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (lock) {
                if (userDao == null) {
                    userDao = new SQLUserDao(getSQLJdbcOperations());
                }
            }
        }
        return userDao;
    }

    /**
     * Returns a singleton instance of the TransactionTypeDao.
     *
     * @return the TransactionTypeDao
     */
    public TransactionTypeDao getTransactionTypeDao() {
        if (transactionTypeDao == null) {
            synchronized (lock) {
                if (transactionTypeDao == null) {
                    transactionTypeDao = new SQLTransactionTypeDao(getSQLJdbcOperations());
                }
            }
        }
        return transactionTypeDao;
    }

    /**
     * Returns a singleton instance of the CreditTransactionDao.
     *
     * @return the CreditTransactionDao
     */
    public CreditTransactionDao getCreditTransactionDao() {
        if (creditTransactionDao == null) {
            synchronized (lock) {
                if (creditTransactionDao == null) {
                    creditTransactionDao = new SQLCreditTransactionDao(getSQLJdbcOperations());
                }
            }
        }
        return creditTransactionDao;
    }

    /**
     * Returns a singleton instance of the RoleDao.
     *
     * @return the RoleDao
     */
    public RoleDao getRoleDao() {
        if (roleDao == null) {
            synchronized (lock) {
                if (roleDao == null) {
                    roleDao = new SQLRoleDao(getSQLJdbcOperations());
                }
            }
        }
        return roleDao;
    }

    /**
     * Returns a singleton instance of the VisitDao.
     *
     * @return the VisitDao
     */
    public VisitDao getVisitDao() {
        if (visitDao == null) {
            synchronized (lock) {
                if (visitDao == null) {
                    visitDao = new SQLVisitDao(getSQLJdbcOperations());
                }
            }
        }
        return visitDao;
    }

    /**
     * Returns a singleton instance of the AuthDao.
     *
     * @return the AuthDao
     */
    public AuthDao getAuthDao() {
        if (authDao == null) {
            synchronized (lock) {
                if (authDao == null) {
                    authDao = new SQLAuthDao(getSQLJdbcOperations());
                }
            }
        }
        return authDao;
    }
}