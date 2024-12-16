package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.User;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for User Data Access Object (DAO).
 * Provides methods to perform CRUD operations, load data from a CSV file, and update user-specific information.
 */
public interface UserDao {

    /**
     * Loads users from a CSV file.
     *
     * @param file the CSV file containing users
     */
    void loadFromCsv(File file);

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @param salt the salt used for password hashing
     * @param password_hash the hashed password
     */
    void create(User user, String salt, String password_hash);

    /**
     * Deletes an existing user.
     *
     * @param user the user to delete
     */
    void delete(User user);

    /**
     * Updates an existing user.
     *
     * @param user the user to update
     */
    void update(User user);

    /**
     * Updates the balance of an existing user.
     *
     * @param user the user whose balance to update
     */
    void updateBalance(User user);

    /**
     * Updates the password of an existing user.
     *
     * @param user the user whose password to update
     * @param salt the salt used for password hashing
     * @param password_hash the hashed password
     */
    void updatePassword(User user, String salt, String password_hash);

    /**
     * Finds a user by its ID.
     *
     * @param id the ID of the user to find
     * @return the found user, or null if not found
     */
    User findById(Long id);

    /**
     * Finds all users.
     *
     * @return a list of all users
     */
    ArrayList<User> findAll();
}