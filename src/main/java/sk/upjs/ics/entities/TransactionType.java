package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class TransactionType {
    private Long id;
    private String name;

    public static TransactionType fromResultSet(ResultSet rs) {
        TransactionType transactionType = new TransactionType();

        try {
            if (rs.wasNull()) {
                return null;
            }

            transactionType.setId(rs.getLong("id"));
            transactionType.setName(rs.getString("name"));

            return transactionType;
        } catch (SQLException _) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
