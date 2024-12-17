package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.TransactionTypeDao;
import sk.upjs.ics.entities.CreditTransactionType;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLTransactionTypeDaoTest {

    private TransactionTypeDao dao;
    private Connection connection;

    @BeforeEach
    void setUp() {
        // Override the DB_URL in Factory to use in-memory database for testing
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        
        // Get connection and dao from Factory
        connection = Factory.INSTANCE.getConnection();
        dao = Factory.INSTANCE.getTransactionTypeDao();
        
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
        ArrayList<CreditTransactionType> types = dao.findAll();
        // We should have at least the 3 types from init.sql
        assertTrue(types.size() >= 3);
        
        // Verify the initial types exist
        boolean hasRefund = false;
        boolean hasVisit = false;
        boolean hasCreditPurchase = false;
        
        for (CreditTransactionType type : types) {
            switch (type.getName().toLowerCase()) {
                case "refund" -> hasRefund = true;
                case "visit" -> hasVisit = true;
                case "credit_purchase" -> hasCreditPurchase = true;
            }
        }
        
        assertTrue(hasRefund && hasVisit && hasCreditPurchase, 
                  "All initial transaction types should be present");
    }

    @Test
    void findById() {
        CreditTransactionType type = dao.findById(1L);
        assertNotNull(type);
        assertEquals("visit", type.getName().toLowerCase());
    }

    @Test
    void findByIdNotFound() {
        assertThrows(NotFoundException.class, () -> dao.findById(999L));
    }

    @Test
    void update() {
        // Get existing type from init.sql (id 1 = 'visit')
        CreditTransactionType type = dao.findById(1L);
        type.setName("UPDATED_VISIT");
        dao.update(type);

        CreditTransactionType updated = dao.findById(1L);
        assertEquals("UPDATED_VISIT", updated.getName());
    }

    @Test
    void createWithNullName() {
        CreditTransactionType type = new CreditTransactionType();
        assertThrows(IllegalArgumentException.class, () -> dao.create(type));
    }

    @Test
    void updateWithNullName() {
        // Use existing type from init.sql
        CreditTransactionType type = dao.findById(1L);
        type.setName(null);
        assertThrows(IllegalArgumentException.class, () -> dao.update(type));
    }

    @Test
    void deleteNonExistent() {
        CreditTransactionType type = new CreditTransactionType();
        type.setId(999L);
        type.setName("NON_EXISTENT");
        
        assertThrows(NotFoundException.class, () -> dao.delete(type));
    }
} 