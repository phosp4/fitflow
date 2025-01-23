package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Represents a credit transaction in the system.
 */
@Data
public class CreditTransaction {
    private Long id;
    private User user;
    private CreditTransactionType creditTransactionType;
    private Long amount;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Creates a CreditTransaction object from the given ResultSet.
     *
     * @param rs the ResultSet containing credit transaction data
     * @return a CreditTransaction object
     */
    public static CreditTransaction fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

    /**
     * Creates a CreditTransaction object from the given ResultSet with a specified prefix.
     *
     * @param rs the ResultSet containing credit transaction data
     * @param prefix the prefix for the column names in the ResultSet
     * @return a CreditTransaction object
     */
    public static CreditTransaction fromResultSet(ResultSet rs, String prefix) {
        CreditTransaction creditTransaction = new CreditTransaction();

        try {
            Long id = rs.getLong(prefix + "id");

            if (rs.wasNull()) {
                return null;
            }

            creditTransaction.setId(id);
            creditTransaction.setUser(null);
            creditTransaction.setCreditTransactionType(null);
            creditTransaction.setAmount(rs.getLong(prefix + "amount"));
            creditTransaction.setCreatedAt(rs.getTimestamp(prefix + "created_at").toInstant());
            creditTransaction.setUpdatedAt(rs.getTimestamp(prefix + "updated_at").toInstant());

            return creditTransaction;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}