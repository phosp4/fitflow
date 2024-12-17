package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user in the system.
 */
@Data
public class User {
    private Long id;
    private Role role;
    private String email;
    private String firstName;
    private String lastName;
    private Float creditBalance;
    private String phone;
    private LocalDate birthDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<Specialization> trainerSpecializationSet;

    /**
     * Creates a User object from the given ResultSet.
     *
     * @param rs the ResultSet containing user data
     * @return a User object
     */
    public static User fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a User object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing user data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a User object
     */
    public static User fromResultSet(ResultSet rs, String prefix) {
        User user = new User();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            user.setId(id);
            user.setRole(null);
            user.setFirstName(rs.getString(prefix + "first_name"));
            user.setLastName(rs.getString(prefix + "last_name"));
            user.setEmail(rs.getString(prefix + "email"));
            user.setCreditBalance(rs.getFloat(prefix + "credit_balance"));
            user.setPhone(rs.getString(prefix + "phone"));
//            user.setBirthDate(rs.getDate(prefix + "birth_date").toLocalDate()); // todo
            user.setActive(rs.getBoolean(prefix + "active"));
            user.setCreatedAt(rs.getTimestamp(prefix + "created_at").toInstant()); // todo toto robi problemy pri auth
            user.setUpdatedAt(rs.getTimestamp(prefix + "updated_at").toInstant()); // todo
            user.setTrainerSpecializationSet(new HashSet<>());

            return user;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}