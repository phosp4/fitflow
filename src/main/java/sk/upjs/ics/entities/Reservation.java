package sk.upjs.ics.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class Reservation {
    private Long id;
    private Long customerId;
    private Long reservationStatusId;
    private String noteToTrainer;
    private Instant createdAt;
    private Instant updatedAt;
    private Long creditTransactionId;
}