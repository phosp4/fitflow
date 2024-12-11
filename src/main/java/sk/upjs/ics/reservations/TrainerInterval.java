package sk.upjs.ics.reservations;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TrainerInterval {
    private Long id;
    private Long trainerId;
    private LocalDate day;
    private LocalTime startTime;
    private LocalDate endTime;
    private Long reservationId;
}
