package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class Specialization {
    private Long id;
    private String name;

    public static Specialization fromResultSet(java.sql.ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static Specialization fromResultSet(ResultSet rs, String prefix) {
        Specialization specialization = new Specialization();

        try {
            if (rs.wasNull()) {
                return null;
            }

            specialization.setId(rs.getLong(prefix + "id"));
            specialization.setName(rs.getString(prefix + "name"));

            return specialization;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
