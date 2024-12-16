package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a specialization in the system.
 */
@Data
public class Specialization {
    private Long id;
    private String name;

    /**
     * Creates a Specialization object from the given ResultSet.
     *
     * @param rs the ResultSet containing specialization data
     * @return a Specialization object
     */
    public static Specialization fromResultSet(java.sql.ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a Specialization object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing specialization data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a Specialization object
     */
    public static Specialization fromResultSet(ResultSet rs, String prefix) {
        Specialization specialization = new Specialization();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            specialization.setId(id);
            specialization.setName(rs.getString(prefix + "name"));

            return specialization;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}