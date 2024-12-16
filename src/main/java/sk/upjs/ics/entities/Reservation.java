package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.Factory;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data
public class Reservation {
    private Long id;
    private User customer;
    private ReservationStatus reservationStatus;
    private String noteToTrainer;
    private Instant createdAt;
    private Instant updatedAt;
    private CreditTransaction creditTransaction;

    public static Reservation fromResultSet(ResultSet rs) {
        Reservation reservation = new Reservation();

        try {
            var id = rs.getLong("id");
            if (rs.wasNull()) {
                return null;
            }

            reservation.setId(id);

            User customer = Factory.INSTANCE.getUserDao().findById(rs.getLong("customer_id"));
            reservation.setCustomer(customer);

            ReservationStatus reservationStatus = Factory.INSTANCE.getReservationStatusDao().findById(rs.getLong("reservation_status_id"));
            reservation.setReservationStatus(reservationStatus);

            reservation.setNoteToTrainer(rs.getString("note_to_trainer"));
            reservation.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            reservation.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());

            CreditTransaction creditTransaction = Factory.INSTANCE.getCreditTransactionDao().findById(rs.getLong("credit_transaction_id"));
            reservation.setCreditTransaction(creditTransaction);

            return reservation;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}