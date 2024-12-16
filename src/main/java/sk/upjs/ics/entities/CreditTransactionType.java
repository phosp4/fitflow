package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a type of credit transaction in the system.
 */
@Data
public class CreditTransactionType {
    private Long id;
    private String name;

    /**
     * Creates a CreditTransactionType object from the given ResultSet.
     *
     * @param rs the ResultSet containing credit transaction type data
     * @return a CreditTransactionType object
     */
    public static CreditTransactionType fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a CreditTransactionType object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing credit transaction type data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a CreditTransactionType object
     */
    public static CreditTransactionType fromResultSet(ResultSet rs, String prefix) {
        CreditTransactionType creditTransactionType = new CreditTransactionType();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            creditTransactionType.setId(id);
            creditTransactionType.setName(rs.getString(prefix + "name"));

            return creditTransactionType;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}