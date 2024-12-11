package sk.upjs.ics.users;

import sk.upjs.ics.Exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLUserDao implements UserDao {

    private final Connection connection;

    public SQLUserDao(Connection connection) {
        this.connection = connection;
    }
    private final String selectQuery = "SELECT role, email, first_name, last_name, credit_balance, phone, birth_date, active FROM users";

    @Override
    public void update(User user) {
        String updateQuery = "UPDATE users SET " +
                "role = ?, email = ?, first_name = ?, last_name = ?, credit_balance = ?, phone = ?, birth_date = ?, active = ?" +
                "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
           pstmt.setLong(1, user.getRoleId());
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
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("User with id " + id + " not found");
            }

            return User.fromResultSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
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
