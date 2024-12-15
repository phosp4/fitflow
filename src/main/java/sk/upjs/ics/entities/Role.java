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
        Role role = new Role();

        try {
            if (rs.wasNull()) {
                return null;
            }

            role.setId(rs.getLong("id"));
            role.setName(rs.getString("name"));

            return role;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }

}

