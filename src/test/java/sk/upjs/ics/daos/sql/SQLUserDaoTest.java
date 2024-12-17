package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.Role;
import sk.upjs.ics.entities.User;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDaoTest {

    private UserDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getUserDao();
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
        ArrayList<User> users = dao.findAll();
        assertTrue(users.size() >= 4); // We have 4 users in init.sql
        
        // Verify initial users exist
        boolean hasJohn = false;
        boolean hasJane = false;
        boolean hasAlice = false;
        boolean hasBob = false;
        
        for (User user : users) {
            switch (user.getFirstName()) {
                case "John" -> hasJohn = true;
                case "Jane" -> hasJane = true;
                case "Alice" -> hasAlice = true;
                case "Bob" -> hasBob = true;
            }
        }
        
        assertTrue(hasJohn && hasJane && hasAlice && hasBob, 
                  "All initial users should be present");
    }

    @Test
    void updateBalance() {
        User user = dao.findById(1L); // John Doe from init.sql
        float originalBalance = user.getCreditBalance();
        float newBalance = originalBalance + 50.0f;
        
        user = new User(); // Create minimal user for balance update
        user.setId(1L);
        user.setCreditBalance(newBalance);
        
        dao.updateBalance(user);
        
        User updated = dao.findById(1L);
        assertEquals(newBalance, updated.getCreditBalance());
    }

    @Test
    void createWithNullEmail() {
        User user = new User();
        Role role = new Role();
        role.setId(2L);
        user.setRole(role);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCreditBalance(0.0f);
        user.setPhone("123-456-7890");
        user.setBirthDate(LocalDate.now());
        
        assertThrows(IllegalArgumentException.class, 
                    () -> dao.create(user, "salt", "hash"));
    }

    @Test
    void updateBalanceWithNegativeAmount() {
        User user = new User();
        user.setId(1L);
        user.setCreditBalance(-50.0f);
        
        assertThrows(IllegalArgumentException.class, 
                    () -> dao.updateBalance(user));
    }
} 