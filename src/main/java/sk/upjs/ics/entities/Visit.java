package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.Factory;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Visit {
    private Long id;
    private User user;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String visitSecret;
    private CreditTransaction creditTransaction;
    private Instant createdAt;
    private Instant updatedAt;

    public static Visit fromResultSet(ResultSet rs) {
        Visit visit = new Visit();

        try {
            if (rs.wasNull()) {
                return null;
            }

            visit.setId(rs.getLong("id"));

            User user = Factory.INSTANCE.getUserDao().findById(rs.getLong("user_id"));
            visit.setUser(user);

            visit.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
            visit.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
            visit.setVisitSecret(rs.getString("visit_secret"));

            CreditTransaction creditTransaction = Factory.INSTANCE.getCreditTransactionDao().findById(rs.getLong("credit_transaction_id"));
            visit.setCreditTransaction(creditTransaction);

            return visit;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
