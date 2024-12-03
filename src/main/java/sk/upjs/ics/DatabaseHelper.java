package sk.upjs.ics;

import java.sql.*;

public class DatabaseHelper {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:fitflow.db";

        try (Connection connection = DriverManager.getConnection(url)) {
            System.out.println("Pripojenie k databáze bolo úspešné.");

            // Vytvor tabuľku
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL
                );
            """;
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Tabuľka 'users' bola vytvorená.");
            }

            // Vlož údaje
            String insertSQL = "INSERT INTO users(name, email) VALUES(?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setString(1, "John Doe");
                pstmt.setString(2, "johndoe@example.com");
                pstmt.executeUpdate();
                System.out.println("Používateľ bol vložený.");
            }

            // Načítaj údaje
            String selectSQL = "SELECT * FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {

                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Meno: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Chyba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
