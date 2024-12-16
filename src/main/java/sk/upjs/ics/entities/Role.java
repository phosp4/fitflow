package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a role in the system.
 */
@Data
public class Role {
    private Long id;
    private String name;

    /**
     * Creates a Role object from the given ResultSet.
     *
     * @param rs the ResultSet containing role data
     * @return a Role object
     */
    public static Role fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a Role object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing role data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a Role object
     */
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