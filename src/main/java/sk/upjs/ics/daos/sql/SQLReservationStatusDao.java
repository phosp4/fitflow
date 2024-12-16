package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.ReservationStatusDao;
import sk.upjs.ics.entities.ReservationStatus;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * SQLReservationStatusDao is an implementation of the ReservationStatusDao interface
 * that provides methods to interact with the reservation statuses in the database.
 */
public class SQLReservationStatusDao implements ReservationStatusDao {

    private final Connection connection;

    /**
     * Constructs a new SQLReservationStatusDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLReservationStatusDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM reservation_statuses";
    private final String insertQuery = "INSERT INTO reservation_statuses (name) VALUES (?)";

    /**
     * Loads reservation statuses from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing reservation status data
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
     * Creates a new reservation status in the database.
     *
     * @param status the reservation status to create
     * @throws IllegalArgumentException if the status or its name is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (status.getName() == null) {
            throw new IllegalArgumentException("Status name cannot be null");
        }

        if (findById(status.getId()) != null) {
            throw new IllegalArgumentException("Reservation status with id " + status.getId() + " already exists");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, status.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a reservation status from the database.
     *
     * @param status the reservation status to delete
     * @throws IllegalArgumentException if the status or its ID is null
     * @throws NotFoundException if the reservation status with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (status.getId() == null) {
            throw new IllegalArgumentException("Status id cannot be null");
        }

        if (findById(status.getId()) == null) {
            throw new NotFoundException("Reservation status with id " + status.getId() + " not found");
        }

        String deleteQuery = "DELETE FROM reservation_statuses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, status.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing reservation status in the database.
     *
     * @param status the reservation status to update
     * @throws IllegalArgumentException if the status or its ID is null or its name is null
     * @throws NotFoundException if the reservation status with the specified ID is not found
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (status.getId() == null) {
            throw new IllegalArgumentException("Status id cannot be null");
        }

        if (status.getName() == null) {
            throw new IllegalArgumentException("Status name cannot be null");
        }

        if (findById(status.getId()) == null) {
            throw new NotFoundException("Reservation status with id " + status.getId() + " not found");
        }

        String updateQuery = "UPDATE reservation_statuses SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, status.getName());
            pstmt.setLong(2, status.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a reservation status by its ID.
     *
     * @param id the ID of the reservation status to find
     * @return the reservation status with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if the reservation status with the specified ID is not found
     */
    @Override
    public ReservationStatus findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try(PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Reservation status with id " + id + " not found");
            }

            return ReservationStatus.fromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds all reservation statuses in the database.
     *
     * @return a list of all reservation statuses
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if no reservation statuses are found
     */
    @Override
    public ArrayList<ReservationStatus> findAll() {
        ArrayList<ReservationStatus> reservationStatuses = new ArrayList<>();

        try(PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservationStatuses.add(ReservationStatus.fromResultSet(rs));
            }

            if (reservationStatuses.isEmpty()) {
                throw new NotFoundException("No reservation statuses found");
            }

            return reservationStatuses;
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
