package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLReservationDaoTest {

    private ReservationDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getReservationDao();
        
        // Initialize database from init.sql
        executeSqlFile();
    }

    private void executeSqlFile() {
        try {
            // Read SQL file from resources
            InputStream inputStream = getClass().getResourceAsStream("/init.sql");
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + "/init.sql");
            }
            
            Scanner scanner = new Scanner(inputStream).useDelimiter(";");
            
            while (scanner.hasNext()) {
                String sqlStatement = scanner.next().trim();
                if (!sqlStatement.isEmpty()) {
                    connection.createStatement().execute(sqlStatement);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize test database", e);
        }
    }

    @Test
    void findByIdNotFound() {
        assertThrows(NotFoundException.class, () -> dao.findById(999L));
    }

    @Test
    void findAll() {
        // The init.sql already contains some reservations
        ArrayList<Reservation> all = dao.findAll();
        assertFalse(all.isEmpty());
    }

    private Reservation createTestReservation() {
        Reservation reservation = new Reservation();
        
        // Use existing user from init.sql
        User customer = new User();
        customer.setId(1L); // User 'John Doe' from init.sql
        reservation.setCustomer(customer);
        
        // Use existing reservation status from init.sql
        ReservationStatus status = new ReservationStatus();
        status.setId(1L); // 'pending' status
        reservation.setReservationStatus(status);
        
        // Create a new credit transaction for this reservation
        CreditTransaction transaction = new CreditTransaction();
        transaction.setId(1L); // Using existing transaction from init.sql
        reservation.setCreditTransaction(transaction);
        
        reservation.setNoteToTrainer("Test Note");
        reservation.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        reservation.setUpdatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        
        return reservation;
    }
} 