package sk.upjs.ics.users;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Specialization> trainerSpecializationId;

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();

        if (rs.wasNull()) {
            return null;
        }

        user.setId(rs.getLong("id"));
        user.setRoleId(rs.getLong("role_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setCreditBalance(rs.getLong("credit_balance"));
        user.setPhone(rs.getString("phone"));
        user.setBirthDate(rs.getDate("birth_date").toLocalDate());
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        user.setTrainerSpecializationId(new HashSet<>());

        return user;
    }
}
