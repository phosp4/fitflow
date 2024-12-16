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

public class SQLReservationStatusDao implements ReservationStatusDao {

    private final Connection connection;

    public SQLReservationStatusDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM reservation_statuses";
    private final String insertQuery = "INSERT INTO reservation_statuses (name) VALUES (?)";

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

    @Override
    public void create(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (status.getId() != null) {
            throw new IllegalArgumentException("The status already has an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, status.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        String deleteQuery = "DELETE FROM reservation_statuses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, status.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
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

    @Override
    public ReservationStatus findById(Long id) {
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
