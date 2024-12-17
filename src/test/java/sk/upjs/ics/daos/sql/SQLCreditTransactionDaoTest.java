package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLCreditTransactionDaoTest {

    private CreditTransactionDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getCreditTransactionDao();
        
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
    void createAndFindById() {
        CreditTransaction transaction = createTestTransaction();
        Long id = dao.create(transaction);

        CreditTransaction found = dao.findById(id);
        assertNotNull(found);
        assertEquals(transaction.getAmount(), found.getAmount());
        assertEquals(transaction.getUser().getId(), found.getUser().getId());
    }

    @Test
    void update() {
        CreditTransaction transaction = createTestTransaction();
        Long id = dao.create(transaction);
        transaction.setId(id);
        
        transaction.setAmount(200.0);
        dao.update(transaction);

        CreditTransaction updated = dao.findById(id);
        assertEquals(200.0, updated.getAmount());
    }

    @Test
    void findAll() {
        CreditTransaction transaction1 = createTestTransaction();
        CreditTransaction transaction2 = createTestTransaction();
        
        dao.create(transaction1);
        dao.create(transaction2);

        ArrayList<CreditTransaction> all = dao.findAll();
        assertEquals(2, all.size());
    }

    private CreditTransaction createTestTransaction() {
        CreditTransaction transaction = new CreditTransaction();
        
        User user = new User();
        user.setId(1L); // Using existing user ID from init.sql
        transaction.setUser(user);
        
        CreditTransactionType type = new CreditTransactionType();
        type.setId(1L); // Using existing transaction type ID from init.sql
        transaction.setCreditTransactionType(type);
        
        transaction.setAmount(100.0);
        transaction.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        transaction.setUpdatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        
        return transaction;
    }
} 