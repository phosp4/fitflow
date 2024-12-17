package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLTrainerIntervalDaoTest {

    private TrainerIntervalDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getTrainerIntervalDao();
        
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
    void findAll() {
        ArrayList<TrainerInterval> intervals = dao.findAll();
        // We should have at least the 3 intervals from init.sql
        assertTrue(intervals.size() >= 3);
        
        // Verify the initial intervals exist
        boolean hasInterval1 = false;
        boolean hasInterval2 = false;
        boolean hasInterval3 = false;
        
        for (TrainerInterval interval : intervals) {
            if (interval.getId() == 100L) {
                hasInterval1 = true;
                assertEquals(LocalDate.parse("2023-11-23"), interval.getDay());
                assertEquals(LocalTime.parse("12:34:56"), interval.getStartTime());
                assertEquals(LocalTime.parse("13:45:00"), interval.getEndTime());
                assertEquals(1L, interval.getTrainer().getId());
            } else if (interval.getId() == 200L) {
                hasInterval2 = true;
                assertEquals(LocalDate.parse("2023-11-24"), interval.getDay());
                assertEquals(LocalTime.parse("10:00:00"), interval.getStartTime());
                assertEquals(LocalTime.parse("11:30:00"), interval.getEndTime());
                assertEquals(2L, interval.getTrainer().getId());
            } else if (interval.getId() == 300L) {
                hasInterval3 = true;
                assertEquals(LocalDate.parse("2023-11-25"), interval.getDay());
                assertEquals(LocalTime.parse("14:00:00"), interval.getStartTime());
                assertEquals(LocalTime.parse("15:15:00"), interval.getEndTime());
                assertEquals(1L, interval.getTrainer().getId());
            }
        }
        
        assertTrue(hasInterval1 && hasInterval2 && hasInterval3, 
                  "All initial intervals should be present");
    }

    @Test
    void findById() {
        TrainerInterval interval = dao.findById(100L);
        assertNotNull(interval);
        assertEquals(LocalDate.parse("2023-11-23"), interval.getDay());
        assertEquals(LocalTime.parse("12:34:56"), interval.getStartTime());
        assertEquals(LocalTime.parse("13:45:00"), interval.getEndTime());
        assertEquals(1L, interval.getTrainer().getId());
        assertNotNull(interval.getReservation());
        assertEquals(1L, interval.getReservation().getId());
    }

    @Test
    void findByIdNotFound() {
        assertThrows(NotFoundException.class, () -> dao.findById(999L));
    }

    @Test
    void createWithNullTrainer() {
        TrainerInterval interval = new TrainerInterval();
        interval.setDay(LocalDate.now());
        interval.setStartTime(LocalTime.now());
        interval.setEndTime(LocalTime.now().plusHours(1));
        
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        interval.setReservation(reservation);
        
        assertThrows(IllegalArgumentException.class, () -> dao.create(interval));
    }

    @Test
    void createWithNullDay() {
        TrainerInterval interval = new TrainerInterval();
        interval.setStartTime(LocalTime.now());
        interval.setEndTime(LocalTime.now().plusHours(1));
        
        User trainer = new User();
        trainer.setId(3L);
        interval.setTrainer(trainer);
        
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        interval.setReservation(reservation);
        
        assertThrows(IllegalArgumentException.class, () -> dao.create(interval));
    }
} 