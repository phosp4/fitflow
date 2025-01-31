package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.EntityCreationFailedException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SQLVisitDao is an implementation of the VisitDao interface
 * that provides methods to interact with the visits in the database.
 */
public class SQLVisitDao implements VisitDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLVisitDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLVisitDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Extracts visits from the given ResultSet.
     *
     */
    private final ResultSetExtractor<ArrayList<Visit>> resultSetExtractor = rs -> {
        ArrayList<Visit> visits = new ArrayList<>();

        HashMap<Long, Visit> visitsProcessed= new HashMap<>();
        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
        HashMap<Long, CreditTransaction> creditTransactionsProcessed = new HashMap<>();
        HashMap<Long, CreditTransactionType> creditTransactionTypesProcessed = new HashMap<>();

        while (rs.next()) {
            Long visitId = rs.getLong("v_id");
            Visit visit = visitsProcessed.get(visitId);
            if (visit == null) {
                visit = Visit.fromResultSet(rs, "v_");
                visitsProcessed.put(visitId, visit);
                visits.add(visit);
            }

            Long userId = rs.getLong("u_id");
            User user = usersProcessed.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "u_");
                usersProcessed.put(userId, user);
            }

            Long roleId = rs.getLong("r_id");
            Role role = rolesProcessed.get(roleId);
            if (role == null) {
                role = Role.fromResultSet(rs, "r_");
                rolesProcessed.put(roleId, role);
            }

            Long creditTransactionId = rs.getLong("ct_id");
            CreditTransaction creditTransaction = creditTransactionsProcessed.get(creditTransactionId);
            if (creditTransaction == null) {
                creditTransaction = CreditTransaction.fromResultSet(rs, "ct_");
                creditTransactionsProcessed.put(creditTransactionId, creditTransaction);
            }

            Long creditTransactionTypeId = rs.getLong("ctt_id");
            CreditTransactionType creditTransactionType = creditTransactionTypesProcessed.get(creditTransactionTypeId);
            if (creditTransactionType == null) {
                creditTransactionType = CreditTransactionType.fromResultSet(rs, "ctt_");
                creditTransactionTypesProcessed.put(creditTransactionTypeId, creditTransactionType);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

            if (creditTransaction != null && creditTransactionType != null) {
                creditTransaction.setCreditTransactionType(creditTransactionType);
            }

            if (creditTransaction != null && user != null) {
                creditTransaction.setUser(user);
            }

            if (visit != null && user != null) {
                visit.setUser(user);
            }

            if (visit != null && creditTransaction != null) {
                visit.setCreditTransaction(creditTransaction);
            }
        }

        return visits;
    };

    private final String visitColumns = "v.id AS v_id, v.check_in_time AS v_check_in_time, v.check_out_time AS v_check_out_time, v.visit_secret AS v_visit_secret";
    private final String userColumns = "u.id AS u_id, u.email AS u_email,  u.first_name AS u_first_name, " +
            "u.last_name AS u_last_name, u.credit_balance AS u_credit_balance, u.phone AS u_phone, " +
            "u.birth_date AS u_birth_date, u.active AS u_active, u.created_at AS u_created_at, " +
            "u.updated_at AS u_updated_at";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";
    private final String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, " +
            "ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    private final String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";

    private final String joins = "LEFT JOIN users u ON v.user_id = u.id " +
            "LEFT JOIN roles r ON u.role_id = r.id " +
            "LEFT JOIN credit_transactions ct ON v.credit_transaction_id = ct.id " +
            "LEFT JOIN credit_transaction_types ctt ON ct.credit_transaction_type_id = ctt.id";

    private final String selectQuery = "SELECT " + visitColumns + ", " + userColumns + ", " + roleColumns + ", " +
             creditTransactionColumns + ", " + creditTransactionTypeColumns +
            " FROM visits v " + joins;
    private final String insertQuery = "INSERT INTO visits(user_id, check_in_time, check_out_time, visit_secret, credit_transaction_id) VALUES (?, ?, ?, ?, ?)";

    /**
     * Loads visits from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing visit data
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
                    pstmt.setTimestamp(2, Timestamp.valueOf(parts[1]));
                    pstmt.setTimestamp(3, Timestamp.valueOf(parts[2]));
                    pstmt.setString(4, parts[3]);
                    pstmt.setLong(5, Long.parseLong(parts[4]));
                    return pstmt;
                });
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }

    }

    /**
     * Creates a new visit in the database.
     *
     * @param visit the visit to create
     * @throws IllegalArgumentException if the visit or any of its required fields are null
     * @throws EntityCreationFailedException if the visit cannot be created
     */
    @Override
    public long create(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Visit cannot be null");
        }

        if (visit.getUser() == null) {
            throw new IllegalArgumentException("Visit user cannot be null");
        }

        if (visit.getUser().getId() == null) {
            throw new IllegalArgumentException("Visit user id cannot be null");
        }


        if (visit.getCheckInTime() == null) {
            throw new IllegalArgumentException("Visit check-in time cannot be null");
        }

        if (visit.getVisitSecret() == null) {
            throw new IllegalArgumentException("Visit secret cannot be null");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setLong(1, visit.getUser().getId());
            pstmt.setTimestamp(2, Timestamp.from(visit.getCheckInTime()));
            pstmt.setTimestamp(3, null);
            pstmt.setString(4, visit.getVisitSecret());
            pstmt.setString(5, null);
            return pstmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().longValue();
        } else {
            throw new EntityCreationFailedException("Creating new visit failed, no ID obtained.");
        }
    }

    /**
     * Deletes a visit from the database.
     *
     * @param visit the visit to delete
     * @throws IllegalArgumentException if the visit or its ID is null
     */
    @Override
    public void delete(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (visit.getId() == null) {
            throw new IllegalArgumentException("Visit id cannot be null");
        }

        if (findById(visit.getId()) == null) {
            throw new NotFoundException("Visit with id " + visit.getId() + " not found");
        }

        jdbcOperations.update("DELETE FROM visits WHERE id = ?", visit.getId());
    }

    /**
     * Updates an existing visit in the database.
     *
     * @param visit the visit to update
     * @throws IllegalArgumentException if the visit or any of its required fields are null
     */
    @Override
    public void update(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Visit cannot be null");
        }

        if (visit.getId() == null) {
            throw new IllegalArgumentException("The visit does not have an id");
        }

        if (visit.getUser() == null) {
            throw new IllegalArgumentException("Visit user cannot be null");
        }

        if (visit.getUser().getId() == null) {
            throw new IllegalArgumentException("Visit user id cannot be null");
        }

        if (visit.getCheckInTime() == null) {
            throw new IllegalArgumentException("Visit check-in time cannot be null");
        }

        if (visit.getCreditTransaction() == null) {
            throw new IllegalArgumentException("Visit credit transaction cannot be null");
        }

        if (visit.getCreditTransaction().getId() == null) {
            throw new IllegalArgumentException("Visit credit transaction id cannot be null");
        }

        if (visit.getVisitSecret() == null) {
            throw new IllegalArgumentException("Visit secret cannot be null");
        }

        if (findById(visit.getId()) == null) {
            throw new NotFoundException("Visit with id " + visit.getId() + " not found");
        }

        String updateQuery = "UPDATE visits SET user_id = ?, check_in_time = ?, check_out_time = ?, visit_secret = ?, credit_transaction_id = ? WHERE id = ?";

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
            pstmt.setLong(1, visit.getUser().getId());
            pstmt.setTimestamp(2, Timestamp.from(visit.getCheckInTime()));
            if (visit.getCheckOutTime() != null) {
                pstmt.setTimestamp(3, Timestamp.from(visit.getCheckOutTime()));
            } else {
                pstmt.setTimestamp(3, null);
            }
            pstmt.setString(4, visit.getVisitSecret());
            pstmt.setLong(5, visit.getCreditTransaction().getId());
            pstmt.setLong(6, visit.getId());
            return pstmt;
        });
    }

    /**
     * Finds a visit by its ID.
     *
     * @param id the ID of the visit to find
     * @return the visit with the specified ID
     * @throws IllegalArgumentException if the ID is null
     */
    @Override
    public Visit findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        ArrayList<Visit> visits = jdbcOperations.query(selectQuery + " WHERE v_id = ?", resultSetExtractor, id);

        if (visits == null || visits.isEmpty()) {
            throw new NotFoundException("Visit with id " + id + " not found!");
        }

        return visits.getFirst();
    }

    /**
     * Finds a visit by its visit secret.
     *
     * @param uid the UID visit secret
     * @return the visit with the specified visit secret UID
     * @throws IllegalArgumentException if the UID is null
     */
    @Override
    public Visit findByVisitSecret(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Visit secret cannot be null");
        }

        ArrayList<Visit> visits = jdbcOperations.query(selectQuery + " WHERE v_visit_secret = ?", resultSetExtractor, uid);

        if (visits == null || visits.isEmpty()) {
            throw new NotFoundException("Visit with visit secret " + uid + " not found!");
        }

        return visits.getFirst();
    }

    /**
     * Finds all visits in the database.
     *
     * @return a list of all visits
     */
    @Override
    public ArrayList<Visit> findAll() {
        ArrayList<Visit> visits = jdbcOperations.query(selectQuery, resultSetExtractor);

        if (visits == null) {
            throw new NotFoundException("Error while finding visits");
        }

        return visits;
    }
}
