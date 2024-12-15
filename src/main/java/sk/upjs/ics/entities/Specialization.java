package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.SQLException;

@Data
public class Specialization {
    private Long id;
    private String name;

    public static Specialization fromResultSet(java.sql.ResultSet rs) {
        Specialization specialization = new Specialization();

        try {
            if (rs.wasNull()) {
                return null;
            }

            specialization.setId(rs.getLong("id"));
            specialization.setName(rs.getString("name"));

            return specialization;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }

    }
}
