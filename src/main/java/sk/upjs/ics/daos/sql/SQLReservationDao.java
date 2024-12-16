package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SQLReservationDao is an implementation of the ReservationDao interface
 * that provides methods to interact with the reservations in the database.
 */
public class SQLReservationDao implements ReservationDao {

    private final Connection connection;

    /**
     * Constructs a new SQLReservationDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLReservationDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Extracts reservations from the given ResultSet.
     *
     * @param rs the ResultSet containing reservation data
     * @return a list of reservations
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<Reservation> extractFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Reservation> reservations = new ArrayList<>();

        HashMap<Long, Reservation> reservationsProcessed = new HashMap<>();
        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
        HashMap<Long, Specialization> specializationsProcessed = new HashMap<>();
        HashMap<Long, ReservationStatus> reservationStatusesProcessed = new HashMap<>();
        HashMap<Long, CreditTransaction> creditTransactionsProcessed = new HashMap<>();
        HashMap<Long, CreditTransactionType> creditTransactionTypesProcessed = new HashMap<>();

        while (rs.next()) {
            Long reservationId = rs.getLong("r_id");
            Reservation reservation = reservationsProcessed.get(reservationId);

            if (reservation == null) {
                reservation = Reservation.fromResultSet(rs, "r_");
                reservationsProcessed.put(reservationId, reservation);
                reservations.add(reservation);
            }

            Long userId = rs.getLong("us_id");
            User user = usersProcessed.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "us_");
                usersProcessed.put(userId, user);
            }

            Long roleId = rs.getLong("rl_id");
            Role role = rolesProcessed.get(roleId);
            if (role == null) {
                role = Role.fromResultSet(rs, "rl_");
                rolesProcessed.put(roleId, role);
            }

            Long specializationId = rs.getLong("tsp_id");
            Specialization specialization = specializationsProcessed.get(specializationId);
            if (specialization == null) {
                specialization = Specialization.fromResultSet(rs, "tsp_");
                specializationsProcessed.put(specializationId, specialization);
            }

            Long statusId = rs.getLong("rs_id");
            ReservationStatus reservationStatus = reservationStatusesProcessed.get(statusId);
            if (reservationStatus == null) {
                reservationStatus = ReservationStatus.fromResultSet(rs, "rs_");
                reservationStatusesProcessed.put(statusId, reservationStatus);
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

            if (user != null && specialization != null) {
                user.getTrainerSpecializationSet().add(specialization);
            }

            if (creditTransaction != null && creditTransaction.getUser() == null) {
                creditTransaction.setUser(user);
            }

            if (creditTransaction != null && creditTransaction.getCreditTransactionType() == null) {
                creditTransaction.setCreditTransactionType(creditTransactionType);
            }

            if (reservation != null && user != null) {
                reservation.setCustomer(user);
            }

            if (reservation != null && reservationStatus != null) {
                reservation.setReservationStatus(reservationStatus);
            }

            if (reservation != null && creditTransaction != null) {
                reservation.setCreditTransaction(creditTransaction);
            }

        }

        if (reservations.isEmpty()) {
            throw new NotFoundException("No reservations found");
        }

        return reservations;
    }

    private final String reservationColumns = "r.id AS r_id, r.note AS r_note, r.created_at AS r_created_at," +
            " r.updated_at AS r_updated_at";
    private final String userColumns = "us.id AS us_id, us.email AS us_email, us.first_name AS us_first_name, " +
            "us.last_name AS us_last_name, us.credit_balance AS us_credit_balance, us.phone AS us_phone, " +
            "us.birth_date AS us_birth_date, us.active AS us_active, us.created_at AS us_created_at, us.updated_at AS us_updated_at";
    private final String roleColumns = "rl.id AS rl_id, rl.name AS rl_name";
    private final String specializationColumns = "tsp.id AS tsp_id, tsp.name AS tsp_name";
    private final String statusColumns = "rs.id AS rs_id, rs.name AS rs_name";
    private final String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    private final String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";

    private final String joins = "LEFT JOIN users us ON us.id = r.customer_id " +
            "LEFT JOIN roles rl ON rl.id = us.role_id " +
            "LEFT JOIN trainers_have_specializations ts ON ts.trainer_id = us.id " +
            "LEFT JOIN trainer_specializations tsp ON tsp.id = ts.specialization_id " +
            "LEFT JOIN reservation_statuses rs ON rs.id = r.status " +
            "LEFT JOIN credit_transactions ct ON ct.id = r.credit_transaction_id " +
            "LEFT JOIN credit_transaction_types ctt ON ctt.id = ct.credit_transaction_type_id";

    private final String selectQuery = "SELECT " + reservationColumns + ", " + userColumns + ", " + roleColumns + ", " + specializationColumns + ", " + statusColumns + ", " + creditTransactionColumns + ", "+ creditTransactionTypeColumns + " FROM reservations r " + joins;
    private final String insertQuery = "INSERT INTO reservations (customer_id, status, note, credit_transaction_id) VALUES (?, ?, ?, ?)";

    /**
     * Loads reservations from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing reservation data
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
                    pstmt.setLong(2, Long.parseLong(parts[1]));
                    pstmt.setString(3, parts[2]);
                    pstmt.setLong(4, Long.parseLong(parts[3]));
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
     * Creates a new reservation in the database.
     *
     * @param reservation the reservation to create
     * @throws IllegalArgumentException if the reservation or any of its required fields are null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        if (reservation.getCustomer() == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (reservation.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        if (reservation.getReservationStatus() == null) {
            throw new IllegalArgumentException("Reservation status cannot be null");
        }

        if (reservation.getReservationStatus().getId() == null) {
            throw new IllegalArgumentException("Reservation status ID cannot be null");
        }

        if (reservation.getCreditTransaction() == null) {
            throw new IllegalArgumentException("Credit transaction cannot be null");
        }

        if (reservation.getCreditTransaction().getId() == null) {
            throw new IllegalArgumentException("Credit transaction ID cannot be null");
        }

        if (findById(reservation.getId()) != null) {
            throw new IllegalArgumentException("Reservation with id " + reservation.getId() + " already exists");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, reservation.getId());

            pstmt.setLong(2, reservation.getReservationStatus().getId());
            if (reservation.getNoteToTrainer() != null) {
                pstmt.setString(3, reservation.getNoteToTrainer());
            } else {
                pstmt.setNull(3, Types.VARCHAR); // need to set to VARCHAR, what equals to SQLite TEXT - JBDC needs to know this under the hood
            }

            pstmt.setLong(4, reservation.getCreditTransaction().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a reservation from the database.
     *
     * @param reservation the reservation to delete
     * @throws IllegalArgumentException if the reservation or its ID is null
     * @throws NotFoundException if the reservation with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        if (reservation.getId() == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        if (findById(reservation.getId()) == null) {
            throw new NotFoundException("Reservation with id " + reservation.getId() + " not found");
        }

        String deleteQuery = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing reservation in the database.
     *
     * @param reservation the reservation to update
     * @throws IllegalArgumentException if the reservation or any of its required fields are null
     * @throws NotFoundException if the reservation with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        if (reservation.getId() == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        if (reservation.getCustomer() == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (reservation.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        if (reservation.getReservationStatus() == null) {
            throw new IllegalArgumentException("Reservation status cannot be null");
        }

        if (reservation.getReservationStatus().getId() == null) {
            throw new IllegalArgumentException("Reservation status ID cannot be null");
        }

        if (reservation.getCreditTransaction() == null) {
            throw new IllegalArgumentException("Credit transaction cannot be null");
        }

        if (reservation.getCreditTransaction().getId() == null) {
            throw new IllegalArgumentException("Credit transaction ID cannot be null");
        }

        if (findById(reservation.getId()) == null) {
            throw new NotFoundException("Reservation with id " + reservation.getId() + " not found");
        }

        String updateQuery = "UPDATE reservations SET customer_id = ?, status = ?, note = ?, credit_transaction_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, reservation.getCustomer().getId());
            pstmt.setLong(2, reservation.getReservationStatus().getId());
            pstmt.setString(3, reservation.getNoteToTrainer());
            pstmt.setLong(4, reservation.getCreditTransaction().getId());
            pstmt.setLong(5, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a reservation by its ID.
     *
     * @param id the ID of the reservation to find
     * @return the reservation with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public Reservation findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE r.id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all reservations in the database.
     *
     * @return a list of all reservations
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public ArrayList<Reservation> findAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery);
             ResultSet rs = pstmt.executeQuery()) {
             return extractFromResultSet(rs);
        } catch (SQLException e) {
             throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all reservations of a specific user.
     *
     * @param userId the ID of the user whose reservations to find
     * @return a list of reservations of the specified user
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public ArrayList<Reservation> findAllOfOneUser(Long userId) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " where r.customer_id = ?")) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
