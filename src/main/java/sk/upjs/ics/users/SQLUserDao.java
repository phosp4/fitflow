package sk.upjs.ics.users;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLUserDao implements UserDao {

    private final Connection connection;

    public SQLUserDao(Connection connection) {
        this.connection = connection;
    }

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
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

}
