package sk.upjs.ics.daos.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SQLVisitDaoTest {

    private VisitDao dao;
    private JdbcOperations jdbcOperations;

    @BeforeEach
    void setUp() {
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");

        jdbcOperations = Factory.INSTANCE.getSQLJdbcOperations();
        dao = Factory.INSTANCE.getVisitDao();

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
        ArrayList<Visit> visits = dao.findAll();
        assertNotNull(visits);
        assertTrue(visits.isEmpty()); // Adjust based on initial data in init.sql
    }

    @Test
    void createVisit() {
        Long visitId = createTestVisit();

        Visit createdVisit = dao.findById(visitId);
        assertNotNull(createdVisit);
        assertEquals(visitId, createdVisit.getUser().getId());
    }

    @Test
    void updateVisit() {
        createTestVisit();
        Visit visit = dao.findById(1L); // Adjust based on initial data in init.sql

        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setId(1L);
        visit.setCreditTransaction(creditTransaction);

        visit.setVisitSecret("updatedSecret");

        dao.update(visit);

        Visit updatedVisit = dao.findById(1L);
        assertEquals("updatedSecret", updatedVisit.getVisitSecret());
    }

    @Test
    void deleteVisit() {
        createTestVisit();
        Visit visit = dao.findById(1L); // Adjust based on initial data in init.sql

        dao.delete(visit);

        assertThrows(NotFoundException.class, () -> dao.findById(1L));
    }

    @Test
    void findById() {
        createTestVisit();
        Visit visit = dao.findById(1L); // Adjust based on initial data in init.sql
        assertNotNull(visit);
        assertEquals(1L, visit.getId());
    }

    private long createTestVisit() {
        Visit visit = new Visit();
        User user = new User();
        user.setId(1L);
        visit.setUser(user);
        visit.setCheckInTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        visit.setVisitSecret("secret");

        return dao.create(visit);
    }
}