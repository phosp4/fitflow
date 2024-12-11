package sk.upjs.ics.reservations;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data
public class Reservation {
    private Long id;
    private Long customerId;
    private Long reservationStatusId;
    private String noteToTrainer;
    private Instant createdAt;
    private Instant updatedAt;
    private Long creditTransactionId;

    public static Reservation fromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();

        if (rs.wasNull()) {
            return null;
        }

        reservation.setId(rs.getLong("id"));
        reservation.setCustomerId(rs.getLong("customer_id"));
        reservation.setReservationStatusId(rs.getLong("reservation_status_id"));
        reservation.setNoteToTrainer(rs.getString("note_to_trainer"));
        reservation.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        reservation.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        reservation.setCreditTransactionId(rs.getLong("credit_transaction_id"));

        return reservation;
    }
}