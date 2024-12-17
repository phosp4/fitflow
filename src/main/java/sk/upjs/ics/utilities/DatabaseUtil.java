package sk.upjs.ics.utilities;

import sk.upjs.ics.Factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class DatabaseUtil {
    public static void initializeDatabase() throws SQLException, IOException {
        String sqlFilePath = System.getProperty("user.dir") + "/init.sql";
        String sql = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

        Connection connection = Factory.INSTANCE.getConnection();

        try (Statement stmt = connection.createStatement()) {
            // Split SQL statements by ; because the execute method can only executre one statement at a time
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
