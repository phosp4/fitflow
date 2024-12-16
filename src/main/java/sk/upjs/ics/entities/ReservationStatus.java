package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the status of a reservation in the system.
 */
@Data
public class ReservationStatus {
    private Long id;
    private String name;

    /**
     * Creates a ReservationStatus object from the given ResultSet.
     *
     * @param rs the ResultSet containing reservation status data
     * @return a ReservationStatus object
     */
    public static ReservationStatus fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a ReservationStatus object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing reservation status data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a ReservationStatus object
     */
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