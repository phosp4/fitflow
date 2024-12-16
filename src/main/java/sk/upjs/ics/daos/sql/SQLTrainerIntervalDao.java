package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.entities.*;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.sql.Connection;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * SQLTrainerIntervalDao is an implementation of the TrainerIntervalDao interface
 * that provides methods to interact with the trainer intervals in the database.
 */
public class SQLTrainerIntervalDao implements TrainerIntervalDao {

    private final Connection connection;

    /**
     * Constructs a new SQLTrainerIntervalDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLTrainerIntervalDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Extracts trainer intervals from the given ResultSet.
     *
     * @param rs the ResultSet containing trainer interval data
     * @return a list of trainer intervals
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<TrainerInterval> extractFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<TrainerInterval> intervals = new ArrayList<>();

        HashMap<Long, TrainerInterval> intervalsProcessed= new HashMap<>();
        HashMap<Long, User> usersProcessed = new HashMap<>();
        HashMap<Long, Role> rolesProcessed = new HashMap<>();
        HashMap<Long, Specialization> specializationsProcessed = new HashMap<>();
        HashMap<Long, Reservation> reservationsProcessed = new HashMap<>();
        HashMap<Long, ReservationStatus> reservationStatusesProcessed = new HashMap<>();
        HashMap<Long, CreditTransaction> creditTransactionsProcessed = new HashMap<>();
        HashMap<Long, CreditTransactionType> creditTransactionTypesProcessed = new HashMap<>();

        while (rs.next()) {
            Long intervalId = rs.getLong("ti_id");
            TrainerInterval interval = intervalsProcessed.get(intervalId);
            if (interval == null) {
                interval = TrainerInterval.fromResultSet(rs, "ti_");
                intervalsProcessed.put(intervalId, interval);
                intervals.add(interval);
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

            Long specializationId = rs.getLong("s_id");
            Specialization specialization = specializationsProcessed.get(specializationId);
            if (specialization == null) {
                specialization = Specialization.fromResultSet(rs, "s_");
                specializationsProcessed.put(specializationId, specialization);
            }

            Long reservationId = rs.getLong("re_id");
            Reservation reservation = reservationsProcessed.get(reservationId);
            if (reservation == null) {
                reservation = Reservation.fromResultSet(rs, "re_");
                reservationsProcessed.put(reservationId, reservation);
            }

            Long statusId = rs.getLong("rs_id");
            ReservationStatus status = reservationStatusesProcessed.get(statusId);
            if (status == null) {
                status = ReservationStatus.fromResultSet(rs, "rs_");
                reservationStatusesProcessed.put(statusId, status);
            }

            Long creditTransactionId = rs.getLong("ct_id");
            CreditTransaction creditTransaction = creditTransactionsProcessed.get(creditTransactionId);
            if (creditTransaction == null) {
                creditTransaction = CreditTransaction.fromResultSet(rs, "ct_");
                creditTransactionsProcessed.put(creditTransactionId, creditTransaction);
            }

            Long creditTranscationTypeId = rs.getLong("ctt_id");
            CreditTransactionType creditTransactionType = creditTransactionTypesProcessed.get(creditTranscationTypeId);
            if (creditTransactionType == null) {
                creditTransactionType = CreditTransactionType.fromResultSet(rs, "ctt_");
                creditTransactionTypesProcessed.put(creditTranscationTypeId, creditTransactionType);
            }

            if (user != null && role != null) {
                user.setRole(role);
            }

            if (user != null && specialization != null) {
                user.getTrainerSpecializationSet().add(specialization);
            }

            if (reservation != null && user != null) {
                reservation.setCustomer(user);
            }

            if (reservation != null && status != null) {
                reservation.setReservationStatus(status);
            }

            if (creditTransaction != null && creditTransactionType != null) {
                creditTransaction.setCreditTransactionType(creditTransactionType);
            }

            if (creditTransaction != null && user != null) {
                creditTransaction.setUser(user);
            }

            if (interval != null && user != null) {
                interval.setTrainer(user);
            }

            if (interval != null && reservation != null) {
                interval.setReservation(reservation);
            }
        }

        if (intervals.isEmpty()) {
            throw new NotFoundException("No trainer intervals found");
        }

        return intervals;
    }

    private final String intervalColumns = "ti.id AS ti_id, ti.day AS ti_day, ti.start_time AS ti_start_time, ti.end_time AS ti_end_time";
    private final String userColumns = "u.id AS u_id, u.email AS u_email,  u.first_name AS u_first_name, " +
            "u.last_name AS u_last_name, u.credit_balance AS u_credit_balance, u.phone AS u_phone, " +
            "u.birth_date AS u_birth_date, u.active AS u_active, u.created_at AS u_created_at, " +
            "u.updated_at AS u_updated_at";
    private final String roleColumns = "r.id AS r_id, r.name AS r_name";
    private final String specializationColumns = "s.id AS s_id, s.name AS s_name, ";
    private final String reservationColumns = "re.id AS re_id, re.note AS re_note, " +
            "re.created_at AS re_created_at, re.updated_at AS re_updated_at";
    private final String statusColumns = "rs.id AS rs_id, rs.name AS rs_name";
    private final String creditTransactionColumns = "ct.id AS ct_id, ct.amount AS ct_amount, " +
            "ct.created_at AS ct_created_at, ct.updated_at AS ct_updated_at";
    private final String creditTransactionTypeColumns = "ctt.id AS ctt_id, ctt.name AS ctt_name";


    private final String joins = "LEFT JOIN users u ON ti.trainer_id = u.id " +
            "LEFT JOIN roles r ON u.role_id = r.id " +
            "LEFT JOIN trainers_have_specializations ts ON u.id = ts.trainer_id " +
            "LEFT JOIN trainer_specializations s ON ts.specialization_id = s.id " +
            "LEFT JOIN reservations re ON ti.reservation_id = re.id " +
            "LEFT JOIN reservation_statuses rs ON rs.id = re.status " +
            "LEFT JOIN credit_transactions ct ON ct.id = re.credit_transaction_id " +
            "LEFT JOIN credit_transaction_types ctt ON ctt.id = ct.credit_transaction_type_id";

    private final String selectQuery = "SELECT " + intervalColumns + ", " + userColumns + ", " + roleColumns + ", " + specializationColumns + reservationColumns + ", " + statusColumns + ", " + creditTransactionColumns + ", " + creditTransactionTypeColumns + " FROM trainers_intervals ti " + joins;
    private final String insertQuery = "INSERT INTO trainers_intervals (trainer_id, day, start_time, end_time, reservation_id) VALUES (?, ?, ?, ?, ?)";

    /**
     * Loads trainer intervals from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing trainer interval data
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
                    pstmt.setDate(2, Date.valueOf(parts[1]));
                    pstmt.setTime(3, Time.valueOf(parts[2]));
                    pstmt.setTime(4, Time.valueOf(parts[3]));
                    pstmt.setLong(5, Long.parseLong(parts[4]));
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
     * Creates a new trainer interval in the database.
     *
     * @param interval the trainer interval to create
     * @throws IllegalArgumentException if the interval or any of its required fields are null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(TrainerInterval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("Interval cannot be null");
        }

        if (interval.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (interval.getTrainer().getId() == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        if (interval.getDay() == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }

        if (interval.getStartTime() == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        if (interval.getReservation() == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        if (interval.getReservation().getId() == null) {
            throw new IllegalArgumentException("Reservation id cannot be null");
        }

        if (findById(interval.getId()) != null) {
            throw new IllegalArgumentException("Interval with id " + interval.getId() + " already exists");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, interval.getTrainer().getId());
            pstmt.setDate(2, Date.valueOf(interval.getDay()));
            pstmt.setTime(3, Time.valueOf(interval.getStartTime()));
            pstmt.setTime(4, Time.valueOf(interval.getEndTime()));
            pstmt.setLong(5, interval.getReservation().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a trainer interval from the database.
     *
     * @param interval the trainer interval to delete
     * @throws IllegalArgumentException if the interval or its ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(TrainerInterval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("Interval cannot be null");
        }

        if (interval.getId() == null) {
            throw new IllegalArgumentException("Interval id cannot be null");
        }

        if (findById(interval.getId()) == null) {
            throw new NotFoundException("Interval with id " + interval.getId() + " not found");
        }

        String deleteQuery = "DELETE FROM trainers_intervals WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, interval.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing trainer interval in the database.
     *
     * @param interval the trainer interval to update
     * @throws IllegalArgumentException if the interval or any of its required fields are null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(TrainerInterval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("Interval cannot be null");
        }

        if (interval.getId() == null) {
            throw new IllegalArgumentException("Interval id cannot be null");
        }

        if (interval.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (interval.getTrainer().getId() == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        if (interval.getDay() == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }

        if (interval.getStartTime() == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        if (interval.getReservation() == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        if (interval.getReservation().getId() == null) {
            throw new IllegalArgumentException("Reservation id cannot be null");
        }

        if (findById(interval.getId()) == null) {
            throw new NotFoundException("Interval with id " + interval.getId() + " not found");
        }

        String updateQuery = "UPDATE trainers_intervals SET trainer_id = ?, day = ?, start_time = ?, end_time = ?, reservation_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, interval.getTrainer().getId());
            pstmt.setDate(2, Date.valueOf(interval.getDay()));
            pstmt.setTime(3, Time.valueOf(interval.getStartTime()));
            pstmt.setTime(4, Time.valueOf(interval.getEndTime()));
            pstmt.setLong(5, interval.getReservation().getId());
            pstmt.setLong(6, interval.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a trainer interval by its ID.
     *
     * @param id the ID of the trainer interval to find
     * @return the trainer interval with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public TrainerInterval findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE ti.id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return extractFromResultSet(rs).getFirst();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all trainer intervals in the database.
     *
     * @return a list of all trainer intervals
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public ArrayList<TrainerInterval> findAll() {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            return  extractFromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
