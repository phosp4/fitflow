package sk.upjs.ics.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class Visit {
    private Long id;
    private Long userId;
    private Instant checkInTime;
    private Instant checkOutTime;
    private String visitSecret;
    private Long creditTransactionId;
}
