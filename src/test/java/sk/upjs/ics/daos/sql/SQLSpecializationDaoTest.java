package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.SpecializationDao;
import sk.upjs.ics.entities.Specialization;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLSpecializationDaoTest {

    private SpecializationDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getSpecializationDao();
        
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
        // Use existing specialization from init.sql (id 1 = 'Yoga')
        Specialization specialization = dao.findById(1L);
        specialization.setName("UPDATED_YOGA");
        dao.update(specialization);

        Specialization updated = dao.findById(1L);
        assertEquals("UPDATED_YOGA", updated.getName());
    }

    @Test
    void findAll() {
        ArrayList<Specialization> all = dao.findAll();
        // We should have at least the 3 specializations from init.sql
        assertTrue(all.size() >= 3);
        
        // Verify the initial specializations exist
        boolean hasYoga = false;
        boolean hasPilates = false;
        boolean hasCrossFit = false;
        
        for (Specialization spec : all) {
            switch (spec.getName()) {
                case "Yoga" -> hasYoga = true;
                case "Pilates" -> hasPilates = true;
                case "CrossFit" -> hasCrossFit = true;
            }
        }
        
        assertTrue(hasYoga && hasPilates && hasCrossFit, 
                  "All initial specializations should be present");
    }

    @Test
    void createWithNullName() {
        Specialization specialization = new Specialization();
        assertThrows(IllegalArgumentException.class, () -> dao.create(specialization));
    }

    @Test
    void updateWithNullName() {
        // Use existing specialization from init.sql
        Specialization specialization = dao.findById(1L);
        specialization.setName(null);
        assertThrows(IllegalArgumentException.class, () -> dao.update(specialization));
    }

    @Test
    void deleteNonExistent() {
        Specialization specialization = new Specialization();
        specialization.setId(999L);
        specialization.setName("NON_EXISTENT");
        
        assertThrows(NotFoundException.class, () -> dao.delete(specialization));
    }
} 