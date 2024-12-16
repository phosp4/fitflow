package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.Factory;
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
        TrainerInterval trainerInterval = new TrainerInterval();

        try {
            Long id = rs.getLong("id");

            if (rs.wasNull()) {
                return null;
            }

            trainerInterval.setId(id);
            trainerInterval.setTrainer(null);
            trainerInterval.setDay(rs.getDate("day").toLocalDate());
            trainerInterval.setStartTime(rs.getTime("start_time").toLocalTime());
            trainerInterval.setEndTime(rs.getTime("end_time").toLocalTime());
            trainerInterval.setReservation(null);

            return trainerInterval;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
