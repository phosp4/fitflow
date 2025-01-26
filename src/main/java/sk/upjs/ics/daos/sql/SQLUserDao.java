package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SQLUserDao is an implementation of the UserDao interface that interacts with a SQL database.
 */
public class SQLUserDao implements UserDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLUserDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLUserDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Extracts a list of User objects from the given ResultSet.
     */
    private final ResultSetExtractor<ArrayList<User>> resultSetExtractor = rs -> {
        ArrayList<User> users = new ArrayList<>();

        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();

        while (rs.next()) {
            Long userId = rs.getLong("u_id");
            User user = usersProcessed.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "u_");
                usersProcessed.put(userId, user);
                users.add(user);
            }

            Long roleId = rs.getLong("r_id");
            Role role = rolesProcessed.get(roleId);
            if (role == null) {
                role = Role.fromResultSet(rs, "r_");
                rolesProcessed.put(roleId, role);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

        }

        return users;
    };

    private final String userColumns = "u.id AS u_id, u.email AS u_email, u.first_name AS u_first_name, " +
            "u.last_name AS u_last_name, u.credit_balance AS u_credit_balance, u.phone AS u_phone, " +
            "u.birth_date AS u_birth_date, u.created_at AS u_created_at, u.updated_at AS u_updated_at, u.active AS u_active";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";

    private final String joins = "LEFT JOIN roles r ON u.role_id = r.id";

    private final String selectQuery = "SELECT " + userColumns + ", " + roleColumns +  " FROM users u " + joins;
    private final String insertQuery = "INSERT INTO users(role_id, email, salt, password_hash, first_name, last_name, credit_balance, phone, birth_date, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Loads users from a CSV file and inserts them into the database.
     *
     * @param file the CSV file to load users from
     * @throws CouldNotAccessFileException if the file cannot be accessed
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

                String[] parts = line.split(",");

                jdbcOperations.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                    pstmt.setLong(1, Long.parseLong(parts[0]));
                    pstmt.setString(2, parts[1]);
                    pstmt.setString(3, parts[2]);
                    pstmt.setString(4, parts[3]);
                    pstmt.setString(5, parts[4]);
                    pstmt.setString(6, parts[5]);
                    pstmt.setLong(7, Long.parseLong(parts[6]));
                    pstmt.setString(8, parts[7]);
                    pstmt.setDate(9, Date.valueOf(parts[8]));
                    pstmt.setBoolean(10, Boolean.parseBoolean(parts[9]));
                    return pstmt;
                });
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }

    /**
     * Creates a new user in the database.
     *
     * @return the ID of the newly created user
     * @param user the user to create
     * @param salt the salt for the user's password
     * @param password_hash the hashed password
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     */
    @Override
    public long create(User user, String salt, String password_hash) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }

        if (password_hash == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        if (user.getEmail() == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("User first name cannot be null");
        }

        if (user.getLastName() == null) {
            throw new IllegalArgumentException("User last name cannot be null");
        }

        if (user.getCreditBalance() != 0) {
            throw new IllegalArgumentException("Credit balance has to be 0");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, user.getRole().getId());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, salt);
            pstmt.setString(4, password_hash);
            pstmt.setString(5, user.getFirstName());
            pstmt.setString(6, user.getLastName());
            pstmt.setLong(7, user.getCreditBalance());
            pstmt.setString(8, user.getPhone());
            pstmt.setDate(9, Date.valueOf(user.getBirthDate()));
            pstmt.setBoolean(10, user.isActive());
            return pstmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Creating new user failed, no ID obtained.");
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the user to update
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     */
    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        if (user.getEmail() == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("User first name cannot be null");
        }

        if (user.getLastName() == null) {
            throw new IllegalArgumentException("User last name cannot be null");
        }

        if (user.getCreditBalance() < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        String updateQuery = "UPDATE users SET role_id = ?, email = ?, first_name = ?, last_name = ?, " +
                "credit_balance = ?, phone = ?, birth_date = ?, active = ? WHERE id = ?";

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setLong(1, user.getRole().getId());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setLong(5, user.getCreditBalance());
            pstmt.setString(6, user.getPhone());
            pstmt.setDate(7, Date.valueOf(user.getBirthDate()));
            pstmt.setBoolean(8, user.isActive());
            pstmt.setLong(9, user.getId());
            return pstmt;
        });
    }


    /**
     * Updates the credit balance of an existing user in the database.
     *
     * @param user the user to update
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     */
    @Override
    public void updateBalance(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        if (user.getEmail() == null) {
            throw new IllegalArgumentException("User email cannot be null");
        }

        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("User first name cannot be null");
        }

        if (user.getLastName() == null) {
            throw new IllegalArgumentException("User last name cannot be null");
        }

        if (user.getCreditBalance() < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        String updateQuery = "UPDATE users SET credit_balance = ? WHERE id = ?";

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setLong(1, user.getCreditBalance());
            pstmt.setLong(2, user.getId());
            return pstmt;
        });
    }

    /**
     * Deletes an existing user from the database.
     *
     * @param user the user to delete
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws NotFoundException if the user is not found
     */
    @Override
    public void delete(User user) {
        if (user == null) {
            throw  new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        jdbcOperations.update("DELETE FROM users WHERE id = ?", user.getId());
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return the found user
     * @throws IllegalArgumentException if the ID is null
     * @throws NotFoundException if the user is not found
     */
    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        ArrayList<User> users = jdbcOperations.query(selectQuery + " WHERE u.id = ?", resultSetExtractor, id);

        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found!");
        }

        return users.getFirst();
    }

    /**
     * Finds all users in the database.
     *
     * @return a list of all users
     */
    @Override
    public ArrayList<User> findAll() {
        ArrayList<User> users = jdbcOperations.query(selectQuery, resultSetExtractor);

        if (users == null) {
            throw new NotFoundException("Error while finding users");
        }

        return users;
    }
}
