package sk.upjs.ics.daos.sql;

import sk.upjs.ics.Factory;
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

public class SQLUserDao implements UserDao {

    private final Connection connection;

    public SQLUserDao(Connection connection) {
        this.connection = connection;
    }

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

    @Override
    public void create(User user, String salt, String password_hash) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() != null) {
            throw new IllegalArgumentException("The user already has an id");
        }

        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }

        if (password_hash == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
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

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
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

    @Override
    public void updateBalance(User user) {
        String updateQuery = "UPDATE users SET credit_balance = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, user.getCreditBalance());
            pstmt.setLong(2, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void updatePassword(User user, String salt, String password_hash) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }

        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }

        if (password_hash == null) {
            throw new IllegalArgumentException("Password hash cannot be null");
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

    @Override
    public void delete(User user) {
        if (user == null) {
            throw  new IllegalArgumentException("User cannot be null");
        }

        String deleteQuery = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public User findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE u.id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public ArrayList<User> findAll() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectQuery);
            return extractFromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    public static void main(String[] args) {
       User user =Factory.INSTANCE.getUserDao().findById(1L);
       System.out.println(user);
    }
}
