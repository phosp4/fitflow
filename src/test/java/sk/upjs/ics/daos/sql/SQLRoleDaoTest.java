package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.RoleDao;
import sk.upjs.ics.entities.Role;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLRoleDaoTest {

    private RoleDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getRoleDao();
        
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
        // Use existing role from init.sql (id 1 = 'admin')
        Role role = dao.findById(1L);
        role.setName("UPDATED_ADMIN");
        dao.update(role);

        Role updated = dao.findById(1L);
        assertEquals("UPDATED_ADMIN", updated.getName());
    }

    @Test
    void findAll() {
        ArrayList<Role> all = dao.findAll();
        // We should have at least the 3 roles from init.sql
        assertTrue(all.size() >= 3);
        
        // Verify the initial roles exist
        boolean hasAdmin = false;
        boolean hasUser = false;
        boolean hasTrainer = false;
        
        for (Role role : all) {
            switch (role.getName().toLowerCase()) {
                case "admin" -> hasAdmin = true;
                case "user" -> hasUser = true;
                case "trainer" -> hasTrainer = true;
            }
        }
        
        assertTrue(hasAdmin && hasUser && hasTrainer, 
                  "All initial roles should be present");
    }

    @Test
    void createWithNullName() {
        Role role = new Role();
        assertThrows(IllegalArgumentException.class, () -> dao.create(role));
    }

    @Test
    void updateWithNullName() {
        // Use existing role from init.sql
        Role role = dao.findById(1L);
        role.setName(null);
        assertThrows(IllegalArgumentException.class, () -> dao.update(role));
    }

    @Test
    void deleteNonExistent() {
        Role role = new Role();
        role.setId(999L);
        role.setName("NON_EXISTENT");
        
        assertThrows(NotFoundException.class, () -> dao.delete(role));
    }
} 