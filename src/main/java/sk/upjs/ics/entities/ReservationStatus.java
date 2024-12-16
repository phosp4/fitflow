package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class ReservationStatus {
    private int id;
    private String name;

    public static ReservationStatus fromResultSet(ResultSet rs) {
        ReservationStatus reservationStatus = new ReservationStatus();

        try {
            if (rs.wasNull()) {
                return null;
            }

            reservationStatus.setId(rs.getInt("id"));
            reservationStatus.setName(rs.getString("name"));

            return reservationStatus;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}