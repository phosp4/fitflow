package sk.upjs.ics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Utility class for database operations.
 */
public class DatabaseUtil {

    /**
     * Initializes the database by executing SQL statements from a file.
     *
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs reading the SQL file
     */
    public static void initializeDatabase() throws SQLException, IOException {
        String sqlFilePath = System.getProperty("user.dir") + "/init.sql";
        String sql = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

        Connection connection = Factory.INSTANCE.getConnection();

        try (Statement stmt = connection.createStatement()) {
            // Split SQL statements by ; because the execute method can only execute one statement at a time
            String[] statements = sql.split(";");

            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty()) {
                    System.out.println("Executing: " + trimmedStatement);
                    stmt.execute(trimmedStatement);
                }
            }
            System.out.println("Successfully initialized database");
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
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}