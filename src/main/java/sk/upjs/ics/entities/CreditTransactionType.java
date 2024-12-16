package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class CreditTransactionType {
    private Long id;
    private String name;

    public static CreditTransactionType fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    public static CreditTransactionType fromResultSet(ResultSet rs, String prefix) {
        CreditTransactionType creditTransactionType = new CreditTransactionType();

        try {
            var id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            creditTransactionType.setId(id);
            creditTransactionType.setName(rs.getString(prefix + "name"));

            return creditTransactionType;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
