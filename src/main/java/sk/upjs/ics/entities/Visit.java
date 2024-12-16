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
            Long id = rs.getLong("id");

            if (rs.wasNull()) {
                return null;
            }

            visit.setId(id);
            visit.setUser(null);
            visit.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
            visit.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
            visit.setVisitSecret(rs.getString("visit_secret"));
            visit.setCreditTransaction(null);

            return visit;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
