package sk.upjs.ics.security;

import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;

import java.sql.*;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.entities.User;

/**
 * This class is responsible for initializing an admin user in the database.
 */
public class InitAdminGenerator {

    /**
     * The main method that executes the admin initialization process.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Obtain JdbcOperations instance from the Factory
        JdbcOperations jdbcOperations = Factory.INSTANCE.getSQLJdbcOperations();

        // Admin user details
        String adminName = "admin";
        String adminEmail = "admin";
        String adminPassword = "admin";

        // Generate salt and hash the admin password
        String salt = BCrypt.gensalt();
        String adminPasswordHash = BCrypt.hashpw(adminPassword, salt);

        // Check if admin already exists in the database
        boolean adminExists = false;
        ArrayList<User> users = Factory.INSTANCE.getUserDao().findAll();
        for (User user : users) {
            if (user.getEmail().equals(adminEmail)) {
                adminExists = true;
                break;
            }
        }

        // If admin exists, print a message and exit
        if (adminExists) {
            System.out.println("Admin already exists in the database.");
            return;
        }

        // SQL query to insert a new admin user
        String insertQuery = "INSERT INTO users(role_id, email, password_hash, first_name, last_name, credit_balance, birth_date, active, id, salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Execute the insert query using JdbcOperations
        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setLong(1, 1); // TODO: Improve this by reading from a configuration or constants
            pstmt.setString(2, adminEmail);
            pstmt.setString(3, adminPasswordHash);
            pstmt.setString(4, adminName);
            pstmt.setString(5, adminName);
            pstmt.setFloat(6, 1000);
            pstmt.setDate(7, new Date(1990, 1, 1));
            pstmt.setBoolean(8, true);
            pstmt.setLong(9, -1);
            pstmt.setString(10, salt);
            return pstmt;
        });

        // Print a message indicating the admin user has been created
        System.out.printf("Admin created with username `%s`, email `%s` password: `%s`%n", adminName, adminEmail, adminPassword);
    }
}