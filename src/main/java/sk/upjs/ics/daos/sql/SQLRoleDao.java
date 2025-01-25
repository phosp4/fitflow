package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
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

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLRoleDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLRoleDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Return a list of role objects from the given ResultSet
     */
    private final ResultSetExtractor<ArrayList<Role>> resultSetExtractor = rs -> {
      ArrayList<Role> roles = new ArrayList<>();

      while (rs.next()) {
          roles.add(Role.fromResultSet(rs));
      }

      return roles;
    };

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

                jdbcOperations.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    return pstmt;
                });
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
     */
    @Override
    public void create(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (role.getName() == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, role.getName());
            return pstmt;
        });
    }

    /**
     * Deletes a role from the database.
     *
     * @param role the role to delete
     * @throws IllegalArgumentException if the role or its ID is null
     * @throws NotFoundException if the role with the specified ID is not found
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

        jdbcOperations.update("DELETE FROM roles WHERE id = ?", role.getId());
    }

    /**
     * Updates an existing role in the database.
     *
     * @param role the role to update
     * @throws IllegalArgumentException if the role or its ID is null
     * @throws NotFoundException if the role with the specified ID is not found
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

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setString(1, role.getName());
            pstmt.setLong(2, role.getId());
            return pstmt;
        });
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

        ArrayList<Role> roles = jdbcOperations.query(selectQuery + " WHERE id = ?", resultSetExtractor, id);

        if (roles == null || roles.isEmpty()) {
            throw new NotFoundException("Role with id " + id + " not found!");
        }

        return roles.getFirst();
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
        ArrayList<Role> roles = jdbcOperations.query(selectQuery, resultSetExtractor);

        if (roles == null) {
            throw new NotFoundException("Error while finding roles!");
        }

        return roles;
    }
}
