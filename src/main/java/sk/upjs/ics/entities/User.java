package sk.upjs.ics.entities;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Long roleId;
    private String firstName;
    private String lastName;
    private String email;
    private Long creditBalance;
    private String phone;
    private LocalDate birthDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<Long> trainerSpecializationId;
    private Set<Long> clientIdSet;
}
