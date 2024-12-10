package sk.upjs.ics.reservations;

import lombok.Data;

import java.time.Instant;

@Data
public class CreditTransaction {
    private Long id;
    private Long userId;
    private Long transactionTypeId;
    private double amount;
    private Instant createdAt;
}
