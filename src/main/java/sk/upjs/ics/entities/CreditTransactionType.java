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
        CreditTransactionType creditTransactionType = new CreditTransactionType();

        try {
            if (rs.wasNull()) {
                return null;
            }

            creditTransactionType.setId(rs.getLong("id"));
            creditTransactionType.setName(rs.getString("name"));

            return creditTransactionType;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
