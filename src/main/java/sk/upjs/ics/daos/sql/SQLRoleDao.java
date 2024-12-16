package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.RoleDao;
import sk.upjs.ics.entities.Role;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLRoleDao implements RoleDao {

    private final Connection connection;

    public SQLRoleDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM roles";
    private final String insertQuery = "INSERT INTO roles (name) VALUES (?)";

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

                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    throw new CouldNotAccessDatabaseException("Database not accessible", e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }

    @Override
    public void create(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role.getId() != null) {
            throw new IllegalArgumentException("The role already has an id");
        }

        try(PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, role.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

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
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

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
        ArrayList<Role> roles = new ArrayList<>();

        try (Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(selectQuery);

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
