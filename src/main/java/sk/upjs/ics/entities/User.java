package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.Factory;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private Long creditBalance;
    private String phone;
    private LocalDate birthDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<Specialization> trainerSpecializationSet;

    public static User fromResultSet(ResultSet rs) {
        User user = new User();

        try {
            if (rs.wasNull()) {
                return null;
            }

            user.setId(rs.getLong("id"));

            Role role = Factory.INSTANCE.getRoleDao().findById(rs.getLong("role_id"));
            user.setRole(role);

            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setCreditBalance(rs.getLong("credit_balance"));
            user.setPhone(rs.getString("phone"));
            user.setBirthDate(rs.getDate("birth_date").toLocalDate());
            user.setActive(rs.getBoolean("active"));
            user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
            user.setTrainerSpecializationSet(new HashSet<>());

            return user;
        } catch (SQLException _) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
