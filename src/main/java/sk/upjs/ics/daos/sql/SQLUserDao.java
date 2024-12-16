package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;
import sk.upjs.ics.entities.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SQLUserDao implements UserDao {

    private final Connection connection;

    public SQLUserDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, role_id, email, first_name, last_name, credit_balance, phone, birth_date, active FROM users";

    @Override
    public void loadFromCsv(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // skip header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                String insertQuery = "INSERT INTO users(role_id, email, password_hash, first_name, last_name, credit_balance, phone, birth_date, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstm = connection.prepareStatement(insertQuery)) {
                    pstm.setLong(1, Long.parseLong(parts[0]));
                    pstm.setString(2, parts[1]);
                    pstm.setString(3, parts[2]);
                    pstm.setString(4, parts[3]);
                    pstm.setString(5, parts[4]);
                    pstm.setFloat(6, Float.parseFloat(parts[5]));
                    pstm.setString(7, parts[6]);
                    pstm.setDate(8, Date.valueOf(parts[7]));
                    pstm.setBoolean(9, Boolean.parseBoolean(parts[8]));
                    pstm.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }
    @Override
    public void update(User user) {
        String updateQuery = "UPDATE users SET " +
                "role_id = ?, email = ?, first_name = ?, last_name = ?, credit_balance = ?, phone = ?, birth_date = ?, active = ?" +
                "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
           pstmt.setLong(1, user.getRole().getId());
           pstmt.setString(2, user.getEmail());
           pstmt.setString(3, user.getFirstName());
           pstmt.setString(4, user.getLastName());
           pstmt.setFloat(5, user.getCreditBalance());
           pstmt.setString(6, user.getPhone());
           pstmt.setDate(7, Date.valueOf(user.getBirthDate()));
           pstmt.setBoolean(8, user.isActive());
           pstmt.setLong(9, user.getId());
           pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateBalance(User user) {
        String updateQuery = "UPDATE users SET credit_balance = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, user.getCreditBalance());
            pstmt.setLong(2, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void updatePassword(User user, String password_hash) {
        String updateString = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateString)) {
            pstmt.setString(1, password_hash);
            pstmt.setLong(2, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(User user) {
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            pstmt.setLong(1, id);

            System.out.println("statement set");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("executed");
            if (!rs.next()) {
                throw new NotFoundException("User with id " + id + " not found");
            }
            System.out.println("checked");
            return User.fromResultSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<User> findAll() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQuery);
            ArrayList<User> users = new ArrayList<>();

            while (rs.next()) {
                users.add(User.fromResultSet(rs));
            }

            if (users.isEmpty()) {
                throw new NotFoundException("No users found");
            }

            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
