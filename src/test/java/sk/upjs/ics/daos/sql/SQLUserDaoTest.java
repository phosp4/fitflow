package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.Role;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDaoTest {

    private UserDao dao;
    private JdbcOperations jdbcOperations;

    @BeforeEach
    void setUp() {
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");

        jdbcOperations = Factory.INSTANCE.getSQLJdbcOperations();
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
                    jdbcOperations.update(sqlStatement);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize test database", e);
        }
    }

    @Test
    void findAll() {
        ArrayList<User> users = dao.findAll();
        assertTrue(users.size() >= 2); // We have 2 users in init.sql
        
        // Verify initial users exist
        boolean hasJohn = false;
        boolean hasJane = false;

        for (User user : users) {
            switch (user.getFirstName()) {
                case "John" -> hasJohn = true;
                case "Jane" -> hasJane = true;
            }
        }
        
        assertTrue(hasJohn && hasJane,
                  "All initial users should be present");
    }

    @Test
    void updateBalance() {
        User user = dao.findById(1L); // John Doe from init.sql
        long originalBalance = user.getCreditBalance();
        long newBalance = originalBalance + 50L;
        
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
        user.setCreditBalance(0L);
        user.setPhone("123-456-7890");
        user.setBirthDate(LocalDate.now());
        
        assertThrows(IllegalArgumentException.class, 
                    () -> dao.create(user, "salt", "hash"));
    }

    @Test
    void updateBalanceWithNegativeAmount() {
        User user = new User();
        user.setId(1L);
        user.setCreditBalance(-50L);
        
        assertThrows(IllegalArgumentException.class, 
                    () -> dao.updateBalance(user));
    }

    @Test
    void createUser() {
        User user = new User();
        Role role = new Role();
        role.setId(2L);
        user.setRole(role);
        user.setEmail("newuser@example.com");
        user.setFirstName("New");
        user.setLastName("User");
        user.setCreditBalance(0L);
        user.setPhone("123-456-7890");
        user.setBirthDate(LocalDate.now());
        user.setActive(true);

        Long userId = dao.create(user, "salt", "hash");

        User createdUser = dao.findById(userId);
        assertNotNull(createdUser);
        assertEquals("newuser@example.com", createdUser.getEmail());
    }

    @Test
    void updateUser() {
        User user = dao.findById(1L); // John Doe from init.sql
        user.setFirstName("UpdatedName");

        dao.update(user);

        User updatedUser = dao.findById(1L);
        assertEquals("UpdatedName", updatedUser.getFirstName());
    }

    @Test
    void deleteUser() {
        User user = dao.findById(1L); // John Doe from init.sql

        dao.delete(user);

        assertThrows(NotFoundException.class, () -> dao.findById(1L));
    }

    @Test
    void findById() {
        User user = dao.findById(1L); // John Doe from init.sql
        assertNotNull(user);
        assertEquals("John", user.getFirstName());
    }
}