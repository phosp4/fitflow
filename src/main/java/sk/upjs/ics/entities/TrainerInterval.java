package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TrainerInterval {
    private Long id;
    private User trainer;
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Reservation reservation;

    public static TrainerInterval fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static TrainerInterval fromResultSet(ResultSet rs, String prefix) {
        TrainerInterval trainerInterval = new TrainerInterval();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            trainerInterval.setId(id);
            trainerInterval.setTrainer(null);
            trainerInterval.setDay(LocalDate.parse(rs.getString(prefix + "day")));
            trainerInterval.setStartTime(LocalTime.parse(rs.getString(prefix + "start_time")));
            trainerInterval.setEndTime(LocalTime.parse(rs.getString(prefix + "end_time")));
            trainerInterval.setReservation(null);

            return trainerInterval;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
