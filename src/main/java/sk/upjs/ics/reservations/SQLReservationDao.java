package sk.upjs.ics.reservations;

import sk.upjs.ics.entities.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLReservationDao implements ReservationDao {

    private final Connection connection;

    public SQLReservationDao(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void create(Reservation reservation) {
        String insertQuery = "INSERT INTO reservations(customer_id, status, note, transaction_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, reservation.getId());
            pstmt.setLong(2, reservation.getReservationStatusId());
            if (reservation.getNoteToTrainer() != null) {
                pstmt.setString(3, reservation.getNoteToTrainer());
            }
            pstmt.setLong(4, reservation.getCreditTransactionId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Reservation reservation) {
        String deleteQuery = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        String updateQuery = "UPDATE reservations SET customer_id = ?, status = ?, note = ?, transaction_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, reservation.getCustomerId());
            pstmt.setLong(2, reservation.getReservationStatusId());
            pstmt.setString(3, reservation.getNoteToTrainer());
            pstmt.setLong(4, reservation.getCreditTransactionId());
            pstmt.setLong(5, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Reservation findById(Long id) {
        String findQuery = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findQuery)) {
            pstmt.setLong(1, id);
            try (var resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(resultSet.getLong("id"));
                    reservation.setCustomerId(resultSet.getLong("customer_id"));
                    reservation.setReservationStatusId(resultSet.getLong("status"));
                    reservation.setNoteToTrainer(resultSet.getString("note"));
                    reservation.setCreditTransactionId(resultSet.getLong("transaction_id"));
                    return reservation;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        String findAllQuery = "SELECT * FROM reservations";
        try (PreparedStatement pstmt = connection.prepareStatement(findAllQuery);
             var resultSet = pstmt.executeQuery()) {
            var reservations = new ArrayList<Reservation>();
            while (resultSet.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(resultSet.getLong("id"));
                reservation.setCustomerId(resultSet.getLong("customer_id"));
                reservation.setReservationStatusId(resultSet.getLong("status"));
                reservation.setNoteToTrainer(resultSet.getString("note"));
                reservation.setCreditTransactionId(resultSet.getLong("transaction_id"));
                reservations.add(reservation);
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
