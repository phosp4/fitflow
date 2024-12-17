package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.ReservationStatusDao;
import sk.upjs.ics.entities.ReservationStatus;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLReservationStatusDaoTest {

    private ReservationStatusDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getReservationStatusDao();
        
        // Initialize database from init.sql
        executeSqlFile();
    }

    private void executeSqlFile() {
        try {
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
    void update() {
        // Use an existing status from init.sql (id 1 = 'pending')
        ReservationStatus status = dao.findById(1L);
        status.setName("Updated Status");
        dao.update(status);

        ReservationStatus updated = dao.findById(1L);
        assertEquals("Updated Status", updated.getName());
    }

    @Test
    void findAll() {
        ArrayList<ReservationStatus> all = dao.findAll();
        // We should have at least the 3 statuses from init.sql
        assertTrue(all.size() >= 3);
        
        // Verify the initial statuses exist
        boolean hasPending = false;
        boolean hasConfirmed = false;
        boolean hasCancelled = false;
        
        for (ReservationStatus status : all) {
            switch (status.getName().toLowerCase()) {
                case "pending" -> hasPending = true;
                case "confirmed" -> hasConfirmed = true;
                case "cancelled" -> hasCancelled = true;
            }
        }
        
        assertTrue(hasPending && hasConfirmed && hasCancelled, 
                  "All initial statuses should be present");
    }
} 