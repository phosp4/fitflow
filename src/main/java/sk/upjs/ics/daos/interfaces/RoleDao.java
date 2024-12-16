package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.Role;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for Role Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface RoleDao {

    /**
     * Loads roles from a CSV file.
     *
     * @param file the CSV file containing roles
     */
    void loadFromCsv(File file);

    /**
     * Creates a new role.
     *
     * @param role the role to create
     */
    void create(Role role);

    /**
     * Deletes an existing role.
     *
     * @param role the role to delete
     */
    void delete(Role role);

    /**
     * Updates an existing role.
     *
     * @param role the role to update
     */
    void update(Role role);

    /**
     * Finds a role by its ID.
     *
     * @param id the ID of the role to find
     * @return the found role, or null if not found
     */
    Role findById(Long id);

    /**
     * Finds all roles.
     *
     * @return a list of all roles
     */
    ArrayList<Role> findAll();
}