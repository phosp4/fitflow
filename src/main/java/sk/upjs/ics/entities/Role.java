package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Role {
    private Long id;
    private String name;

    public static Role fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static Role fromResultSet(ResultSet rs, String prefix) {
        Role role = new Role();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            role.setId(id);
            role.setName(rs.getString(prefix + "name"));

            return role;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }

}

