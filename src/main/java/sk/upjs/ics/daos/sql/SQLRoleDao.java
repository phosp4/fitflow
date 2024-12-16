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

/**
 * SQLRoleDao is an implementation of the RoleDao interface
 * that provides methods to interact with the roles in the database.
 */
public class SQLRoleDao implements RoleDao {

    private final Connection connection;

    /**
     * Constructs a new SQLRoleDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLRoleDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM roles";
    private final String insertQuery = "INSERT INTO roles (name) VALUES (?)";

    /**
     * Loads roles from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing role data
     * @throws CouldNotAccessFileException if the file cannot be accessed
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
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

    /**
     * Creates a new role in the database.
     *
     * @param role the role to create
     * @throws IllegalArgumentException if the role or its name is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role.getName() == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        if (findById(role.getId()) != null) {
            throw new IllegalArgumentException("Role with id " + role.getId() + " already exists");
        }

        try(PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, role.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a role from the database.
     *
     * @param role the role to delete
     * @throws IllegalArgumentException if the role or its ID is null
     * @throws NotFoundException if the role with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role.getId() == null) {
            throw new IllegalArgumentException("Role id cannot be null");
        }

        if (findById(role.getId()) == null) {
            throw new NotFoundException("Role with id " + role.getId() + " not found");
        }

        String deleteQuery = "DELETE FROM roles WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, role.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing role in the database.
     *
     * @param role the role to update
     * @throws IllegalArgumentException if the role or its ID is null
     * @throws NotFoundException if the role with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role.getId() == null) {
            throw new IllegalArgumentException("Role id cannot be null");
        }

        if (role.getName() == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        if (findById(role.getId()) == null) {
            throw new NotFoundException("Role with id " + role.getId() + " not found");
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

    /**
     * Finds a role by its ID.
     *
     * @param id the ID of the role to find
     * @return the role with the specified ID
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws IllegalArgumentException if the ID is null
     * @throws NotFoundException if the role with the specified ID is not found
     */
    @Override
    public Role findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

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

    /**
     * Finds all roles in the database.
     *
     * @return a list of all roles
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if no roles are found
     */
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
