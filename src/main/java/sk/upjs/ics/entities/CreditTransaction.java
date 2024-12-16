package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data
public class CreditTransaction {
    private Long id;
    private User user;
    private CreditTransactionType creditTransactionType;
    private double amount;
    private Instant createdAt;
    private Instant updatedAt;

    public static CreditTransaction fromResultSet(ResultSet rs) {
        return fromResultSet(rs, "");
    }

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
            creditTransaction.setAmount(rs.getDouble(prefix + "amount"));
            creditTransaction.setCreatedAt(rs.getTimestamp(prefix + "created_at").toInstant());
            creditTransaction.setUpdatedAt(rs.getTimestamp(prefix + "updated_at").toInstant());

            return creditTransaction;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet", e);
        }
    }
}
