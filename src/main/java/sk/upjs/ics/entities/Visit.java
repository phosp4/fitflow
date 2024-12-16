package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data
public class Visit {
    private Long id;
    private User user;
    private Instant checkInTime;
    private Instant checkOutTime;
    private String visitSecret;
    private CreditTransaction creditTransaction;
    private Instant createdAt;
    private Instant updatedAt;

    public static Visit fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static Visit fromResultSet(ResultSet rs, String prefix) {
        Visit visit = new Visit();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            visit.setId(id);
            visit.setUser(null);
            visit.setCheckInTime(rs.getTimestamp(prefix + "check_in_time").toInstant());
            visit.setCheckOutTime(rs.getTimestamp(prefix + "check_out_time").toInstant());
            visit.setVisitSecret(rs.getString(prefix + "visit_secret"));
            visit.setCreditTransaction(null);

            return visit;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
