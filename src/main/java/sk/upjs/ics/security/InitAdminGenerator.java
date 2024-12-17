package sk.upjs.ics.security;

import sk.upjs.ics.Factory;

import java.sql.*;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;

public class InitAdminGenerator {
    public static void main(String[] args) {
        var connection = Factory.INSTANCE.getConnection();

        var adminName = "admin";
        var adminEmail = "admin";
        var adminPassword = "admin";
        var salt = BCrypt.gensalt();
        var adminPasswordHash = BCrypt.hashpw(adminPassword, salt);

        boolean adminExists = false;
        ArrayList<User> users = Factory.INSTANCE.getUserDao().findAll();
        for (User user : users) {
            if (user.getEmail().equals(adminEmail)) {
                adminExists = true;
                break;
            }
        }

        if (adminExists) {
            System.out.println("Admin already exists in the database.");
            return;
        }

        String insertQuery = "INSERT INTO users(role_id, email, password_hash, first_name, last_name, credit_balance, birth_date, active, id, salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(insertQuery)) {
            pstm.setLong(1, 1); // todo ako to urobit lepsie? treba citat z ciselnika...
            pstm.setString(2, adminEmail);
            pstm.setString(3, adminPasswordHash);
            pstm.setString(4, adminName);
            pstm.setString(5, adminName);
            pstm.setFloat(6, 1000);
            pstm.setDate(7, new Date(1990, 1, 1));
            pstm.setBoolean(8, true);
            pstm.setLong(9, -1);
            pstm.setString(10, salt);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }

        System.out.printf("Admin created with username `%s`, email `%s` password: `%s`%n", adminName, adminEmail, adminPassword);
    }
}