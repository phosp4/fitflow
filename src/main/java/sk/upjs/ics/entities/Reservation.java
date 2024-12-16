package sk.upjs.ics.entities;

import lombok.Data;
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
        return fromResultSet(rs, "");
    }

    public static Reservation fromResultSet(ResultSet rs, String prefix) {
        Reservation reservation = new Reservation();

        try {
            Long id = rs.getLong("id");

            if (rs.wasNull()) {
                return null;
            }

            reservation.setId(id);
            reservation.setCustomer(null);
            reservation.setReservationStatus(null);
            reservation.setNoteToTrainer(rs.getString(prefix + "note"));
            reservation.setCreatedAt(rs.getTimestamp(prefix + "created_at").toInstant());
            reservation.setUpdatedAt(rs.getTimestamp(prefix + "updated_at").toInstant());
            reservation.setCreditTransaction(null);

            return reservation;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }

    }
}