package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.NotFoundException;
import sk.upjs.ics.entities.Reservation;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLReservationDao implements ReservationDao {

    private final Connection connection;

    public SQLReservationDao(Connection connection) {
        this.connection = connection;
    }

    private final String insertQuery = "INSERT INTO reservations (customer_id, status, note, transaction_id) VALUES (?, ?, ?, ?)";

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

                try (PreparedStatement pstm = connection.prepareStatement(insertQuery)) {
                    pstm.setLong(1, Long.parseLong(parts[0]));
                    pstm.setLong(2, Long.parseLong(parts[1]));
                    pstm.setString(3, parts[2]);
                    pstm.setLong(4, Long.parseLong(parts[3]));
                    pstm.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Reservation reservation) {
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
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public void delete(Reservation reservation) {
        String deleteQuery = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public void update(Reservation reservation) {
        String updateQuery = "UPDATE reservations SET customer_id = ?, status = ?, note = ?, transaction_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, reservation.getCustomer().getId());
            pstmt.setLong(2, reservation.getReservationStatus().getId());
            pstmt.setString(3, reservation.getNoteToTrainer());
            pstmt.setLong(4, reservation.getCreditTransaction().getId());
            pstmt.setLong(5, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException _) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public Reservation findById(Long id) {
        String findQuery = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findQuery)) {
            pstmt.setLong(1, id);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NotFoundException("Reservation with id " + id + " not found");
                }
                return Reservation.fromResultSet(resultSet);
            }
        } catch (SQLException _) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public ArrayList<Reservation> findAll() {
        String findAllQuery = "SELECT id, customer_id, status, note, created_at, updated_at, transaction_id FROM reservations";
        try (PreparedStatement pstmt = connection.prepareStatement(findAllQuery);
             ResultSet resultSet = pstmt.executeQuery()) {
             ArrayList<Reservation> reservations = new ArrayList<>();

             while (resultSet.next()) {
                reservations.add(Reservation.fromResultSet(resultSet));
             }

             if (reservations.isEmpty()) {
                throw new NotFoundException("No reservations found");
             }
            return reservations;
        } catch (SQLException _) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }
}
