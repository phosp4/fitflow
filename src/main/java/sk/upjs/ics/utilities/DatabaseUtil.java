package sk.upjs.ics.utilities;

import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for database operations.
 */
public class DatabaseUtil {

    /**
     * Initializes the database by executing SQL statements from a file.
     *
     * @throws IOException if an I/O error occurs reading the SQL file
     */
    public static void initializeDatabase() throws IOException {
        String sqlFilePath = System.getProperty("user.dir") + "/init.sql";
        String sql = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

        // Split SQL statements by ; because the execute method can only execute one statement at a time
        String[] statements = sql.split(";");

        JdbcOperations jdbcOperations = Factory.INSTANCE.getSQLJdbcOperations();

        for (String statement : statements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                System.out.println("Executing: " + trimmedStatement);
                jdbcOperations.update(trimmedStatement);
            }
        }
    }

    /**
     * Main method to initialize the database.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Initializing database");
            initializeDatabase();
            System.out.println("Database initialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}