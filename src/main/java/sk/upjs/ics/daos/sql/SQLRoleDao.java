package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.RoleDao;
import sk.upjs.ics.entities.Role;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;

public class SQLRoleDao implements RoleDao {

    private final Connection connection;

    public SQLRoleDao(Connection connection) {
        this.connection = connection;
    }

    String selectQuery = "SELECT id, name FROM roles";

    @Override
    public void create(String role) {
        String insertQuery = "INSERT INTO roles (name) VALUES (?)";
        try(PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(Role role) {
        String deleteQuery = "DELETE FROM roles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, role.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(Role role) {
        String updateQuery = "UPDATE roles SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, role.getName());
            pstmt.setLong(2, role.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public Role findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Role with id " + id + " not found");
            }

            return Role.fromResultSet(rs);

        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public ArrayList<Role> findAll() {
        try (Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(selectQuery);
            ArrayList<Role> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(Role.fromResultSet(rs));
            }

            if (roles.isEmpty()) {
                throw new NotFoundException("No roles found");
            }

            return roles;
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
