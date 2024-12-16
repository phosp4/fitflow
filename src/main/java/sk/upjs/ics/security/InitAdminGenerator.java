package sk.upjs.ics.security;

import sk.upjs.ics.Factory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;

public class InitAdminGenerator {
    public static void main(String[] args) {
        var connection = Factory.INSTANCE.getConnection();

        var scanner = new Scanner(System.in);

        System.out.println("This program will create an admin user in the database.");
        System.out.println("Please enter new username:");
        var adminName = scanner.nextLine();
        System.out.println("Please enter new email:");
        var adminEmail = scanner.nextLine();
        System.out.println("Please enter new password:");
        var adminPassword = scanner.nextLine();

        var salt = BCrypt.gensalt();
        var adminPasswordHash = BCrypt.hashpw(adminPassword, salt);

        long id = -1;

        // todo this check is not working
//        try (PreparedStatement pstmt = connection.prepareStatement("SELECT MIN(id) FROM users WHERE id < 0")) {
//            ResultSet rs = pstmt.executeQuery();
//
//            var user = User.fromResultSet(rs);
//            var existingMinId = user.getId();
//
//            if (existingMinId != null) {
//                id = existingMinId - 1;
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

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
            pstm.setLong(9, id);
            pstm.setString(10, salt);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }

        System.out.printf("Admin created with username `%s`, email `%s` password: `%s`%n", adminName, adminEmail, adminPassword);
    }
}