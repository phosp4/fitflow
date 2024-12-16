package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class ReservationStatus {
    private Long id;
    private String name;

    public static ReservationStatus fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static ReservationStatus fromResultSet(ResultSet rs, String prefix) {
        ReservationStatus reservationStatus = new ReservationStatus();

        try {
            Long id = rs.getLong(prefix + "id");
            if (rs.wasNull()) {
                return null;
            }

            reservationStatus.setId(id);
            reservationStatus.setName(rs.getString(prefix + "name"));

            return reservationStatus;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }

    }
}