package sk.upjs.ics.entities;

import lombok.Data;
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
    private String email;
    private String firstName;
    private String lastName;
    private Long creditBalance;
    private String phone;
    private LocalDate birthDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<Specialization> trainerSpecializationSet;

    public static User fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static User fromResultSet(ResultSet rs, String prefix) {
        User user = new User();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            user.setId(id);
            //user.setRole(Factory.INSTANCE.getRoleDao().findById(rs.getLong("role_id")));
            user.setRole(null);
            user.setFirstName(rs.getString(prefix + "first_name"));
            user.setLastName(rs.getString(prefix + "last_name"));
            user.setEmail(rs.getString(prefix + "email"));
            user.setCreditBalance(rs.getLong(prefix + "credit_balance"));
            user.setPhone(rs.getString(prefix + "phone"));
//            user.setBirthDate(rs.getDate(prefix + "birth_date").toLocalDate());
            user.setActive(rs.getBoolean(prefix + "active"));
//            user.setCreatedAt(rs.getTimestamp(prefix + "created_at").toInstant()); // todo toto robi problemy pri auth
//            user.setUpdatedAt(rs.getTimestamp(prefix + "updated_at").toInstant());
            user.setTrainerSpecializationSet(new HashSet<>());

            return user;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
