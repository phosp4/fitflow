package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.NotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SQLVisitDaoTest {

    private SQLVisitDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        
        // Create necessary tables
        connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS roles (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL" +
            ")"
        );

        connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "role_id INTEGER NOT NULL," +
            "email TEXT NOT NULL," +
            "salt TEXT NOT NULL," +
            "password_hash TEXT NOT NULL," +
            "first_name TEXT NOT NULL," +
            "last_name TEXT NOT NULL," +
            "credit_balance REAL NOT NULL," +
            "phone TEXT NOT NULL," +
            "birth_date DATE NOT NULL," +
            "active BOOLEAN NOT NULL," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(role_id) REFERENCES roles(id)" +
            ")"
        );

        connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS credit_transaction_types (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL" +
            ")"
        );

        connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS credit_transactions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER NOT NULL," +
            "amount REAL NOT NULL," +
            "credit_transaction_type_id INTEGER NOT NULL," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(user_id) REFERENCES users(id)," +
            "FOREIGN KEY(credit_transaction_type_id) REFERENCES credit_transaction_types(id)" +
            ")"
        );

        connection.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS visits (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER NOT NULL," +
            "check_in_time TIMESTAMP NOT NULL," +
            "check_out_time TIMESTAMP," +
            "visit_secret TEXT NOT NULL," +
            "credit_transaction_id INTEGER," +
            "FOREIGN KEY(user_id) REFERENCES users(id)," +
            "FOREIGN KEY(credit_transaction_id) REFERENCES credit_transactions(id)" +
            ")"
        );
        
        dao = new SQLVisitDao(connection);
        setupTestData();
    }

    private void setupTestData() throws SQLException {
        // Insert test role
        connection.createStatement().execute(
            "INSERT INTO roles (name) VALUES ('USER')"
        );

        // Insert test user
        connection.createStatement().execute(
            "INSERT INTO users (role_id, email, first_name, last_name, credit_balance, phone, birth_date, active, salt, password_hash) " +
            "VALUES (1, 'test@test.com', 'Test', 'User', 100.0, '+1234567890', '2000-01-01', 1, 'salt', 'hash')"
        );

        // Insert test transaction type
        connection.createStatement().execute(
            "INSERT INTO credit_transaction_types (name) VALUES ('VISIT')"
        );

        // Insert test credit transaction
        connection.createStatement().execute(
            "INSERT INTO credit_transactions (user_id, amount, credit_transaction_type_id) " +
            "VALUES (1, 10.0, 1)"
        );
    }

    @Test
    void createAndFindById() {
        Visit visit = createTestVisit();
        dao.create(visit);
        
        Visit found = dao.findById(1L);
        assertNotNull(found);
        assertEquals(visit.getVisitSecret(), found.getVisitSecret());
        assertEquals(visit.getUser().getId(), found.getUser().getId());
    }

    @Test
    void findByIdNotFound() {
        assertThrows(NotFoundException.class, () -> dao.findById(999L));
    }

    @Test
    void findByVisitSecret() {
        Visit visit = createTestVisit();
        dao.create(visit);
        
        Visit found = dao.findByVisitSecret(visit.getVisitSecret());
        assertNotNull(found);
        assertEquals(visit.getVisitSecret(), found.getVisitSecret());
    }

    @Test
    void update() {
        Visit visit = createTestVisit();
        dao.create(visit);
        visit.setId(1L);
        
        Instant checkOutTime = Instant.now();
        visit.setCheckOutTime(checkOutTime);
        dao.update(visit);

        Visit updated = dao.findById(1L);
        assertNotNull(updated.getCheckOutTime());
        assertEquals(checkOutTime.getEpochSecond(), updated.getCheckOutTime().getEpochSecond());
    }

    @Test
    void delete() {
        Visit visit = createTestVisit();
        dao.create(visit);
        visit.setId(1L);

        dao.delete(visit);

        assertThrows(NotFoundException.class, () -> dao.findById(1L));
    }

    @Test
    void findAll() {
        Visit visit1 = createTestVisit();
        Visit visit2 = createTestVisit();
        
        dao.create(visit1);
        dao.create(visit2);

        ArrayList<Visit> all = dao.findAll();
        assertEquals(2, all.size());
    }

    private Visit createTestVisit() {
        Visit visit = new Visit();
        
        User user = new User();
        user.setId(1L);
        visit.setUser(user);
        
        CreditTransaction transaction = new CreditTransaction();
        transaction.setId(1L);
        visit.setCreditTransaction(transaction);
        
        visit.setCheckInTime(Instant.now());
        visit.setVisitSecret(UUID.randomUUID().toString());
        
        return visit;
    }
} 