package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
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

    private final Connection connection;

    /**
     * Constructs a new SQLUserDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLUserDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Extracts a list of User objects from the given ResultSet.
     *
     * @param rs the ResultSet to extract users from
     * @return a list of User objects
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<User> extractFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
        HashMap<Long, Specialization> specializationsProcessed = new HashMap<>();

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

            Long specializationId = rs.getLong("s_id");
            Specialization specialization = specializationsProcessed.get(specializationId);
            if (specialization == null) {
                specialization = Specialization.fromResultSet(rs, "s_");
                specializationsProcessed.put(specializationId, specialization);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

            if (user != null && specialization != null) {
                user.getTrainerSpecializationSet().add(specialization);
            }
        }

        if (users.isEmpty()) {
            throw new NotFoundException("No users found");
        }

        return users;
    }

    private final String userColumns = "u.id AS u_id, u.email AS u_email, u.first_name AS u_first_name, " +
            "u.last_name AS u_last_name, u.credit_balance AS u_credit_balance, u.phone AS u_phone, " +
            "u.birth_date AS u_birth_date, u.created_at AS u_created_at, u.updated_at AS u_updated_at, u.active AS u_active";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";
    private final String specializationsColumns = "s.id AS s_id, s.name AS s_name";

    private final String joins = "LEFT JOIN roles r ON u.role_id = r.id " +
            "LEFT JOIN trainers_have_specializations tsp ON u.id = tsp.trainer_id " +
            "LEFT JOIN trainer_specializations s ON tsp.specialization_id = s.id";

    private final String selectQuery = "SELECT " + userColumns + ", " + roleColumns + "," + specializationsColumns + " FROM users u " + joins;
    private final String insertQuery = "INSERT INTO users(role_id, email, salt, password_hash, first_name, last_name, credit_balance, phone, birth_date, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Loads users from a CSV file and inserts them into the database.
     *
     * @param file the CSV file to load users from
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

                String[] parts = line.split(",");

                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setLong(1, Long.parseLong(parts[0]));
                    pstmt.setString(2, parts[1]);
                    pstmt.setString(3, parts[2]);
                    pstmt.setString(4, parts[3]);
                    pstmt.setString(5, parts[4]);
                    pstmt.setString(6, parts[5]);
                    pstmt.setFloat(7, Float.parseFloat(parts[6]));
                    pstmt.setString(8, parts[7]);
                    pstmt.setDate(9, Date.valueOf(parts[8]));
                    pstmt.setBoolean(10, Boolean.parseBoolean(parts[9]));
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
     * Creates a new user in the database.
     *
     * @param user the user to create
     * @param salt the salt for the user's password
     * @param password_hash the hashed password
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(User user, String salt, String password_hash) {
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

        if (user.getCreditBalance() < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }

        if (user.getPhone() == null) {
            throw new IllegalArgumentException("User phone cannot be null");
        }

        if (user.getBirthDate() == null) {
            throw new IllegalArgumentException("User birth date cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, user.getRole().getId());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, salt);
            pstmt.setString(4, password_hash);
            pstmt.setString(5, user.getFirstName());
            pstmt.setString(6, user.getLastName());
            pstmt.setFloat(7, user.getCreditBalance());
            pstmt.setString(8, user.getPhone());
            pstmt.setDate(9, Date.valueOf(user.getBirthDate()));
            pstmt.setBoolean(10, user.isActive());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the user to update
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

        if (user.getPhone() == null) {
            throw new IllegalArgumentException("User phone cannot be null");
        }

        if (user.getBirthDate() == null) {
            throw new IllegalArgumentException("User birth date cannot be null");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        String updateQuery = "UPDATE users SET role_id = ?, email = ?, first_name = ?, last_name = ?, " +
                "credit_balance = ?, phone = ?, birth_date = ?, active = ? WHERE id = ?";

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
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }


    /**
     * Updates the credit balance of an existing user in the database.
     *
     * @param user the user to update
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void updateBalance(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

//        if (user.getRole() != null) {
//            throw new IllegalArgumentException("User role cannot be set");
//        } // todo

//        if (user.getEmail() != null) {
//            throw new IllegalArgumentException("User email cannot be set");
//        }

//        if (user.getFirstName() != null) {
//            throw new IllegalArgumentException("User first name cannot be set");
//        }

//        if (user.getLastName() != null) {
//            throw new IllegalArgumentException("User last name cannot be set");
//        }
//
//        if (user.getPhone() != null) {
//            throw new IllegalArgumentException("User phone cannot be set");
//        }
//
//        if (user.getBirthDate() != null) {
//            throw new IllegalArgumentException("User birth date cannot be set");
//        }

        if (user.getCreditBalance() < 0) {
            throw new IllegalArgumentException("Credit balance cannot be negative");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        String updateQuery = "UPDATE users SET credit_balance = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setFloat(1, user.getCreditBalance());
            pstmt.setLong(2, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Updates the password of an existing user in the database.
     *
     * @param user the user to update
     * @param salt the new salt for the user's password
     * @param password_hash the new hashed password
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void updatePassword(User user, String salt, String password_hash) {
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

        if (user.getPhone() == null) {
            throw new IllegalArgumentException("User phone cannot be null");
        }

        if (user.getBirthDate() == null) {
            throw new IllegalArgumentException("User birth date cannot be null");
        }

        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }

        if (password_hash == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
        }

        if (findById(user.getId()) == null) {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }

        String updateString = "UPDATE users SET salt = ?, password_hash = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateString)) {
            pstmt.setString(1, salt);
            pstmt.setString(2, password_hash);
            pstmt.setLong(3, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes an existing user from the database.
     *
     * @param user the user to delete
     * @throws IllegalArgumentException if any of the parameters are null or invalid
     * @throws NotFoundException if the user is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
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

        String deleteQuery = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return the found user
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if the user is not found
     */
    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE u.id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all users in the database.
     *
     * @return a list of all users
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public ArrayList<User> findAll() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQuery);
            return extractFromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
