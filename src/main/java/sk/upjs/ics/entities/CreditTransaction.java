package sk.upjs.ics.entities;

import lombok.Data;
import sk.upjs.ics.Factory;
import sk.upjs.ics.exceptions.CouldNotAccessResultSetException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data
public class CreditTransaction {
    private Long id;
    private User user;
    private TransactionType transactionType;
    private double amount;
    private Instant createdAt;
    private Instant updatedAt;

    public static CreditTransaction fromResultSet(ResultSet rs) {
        CreditTransaction creditTransaction = new CreditTransaction();

        try {
            if (rs.wasNull()) {
                return null;
            }

            creditTransaction.setId(rs.getLong("id"));

            User user = Factory.INSTANCE.getUserDao().findById(rs.getLong("user_id"));
            creditTransaction.setUser(user);

            TransactionType transactionType = Factory.INSTANCE.getTransactionTypeDao().findById(rs.getLong("transaction_type_id"));
            creditTransaction.setTransactionType(transactionType);

            creditTransaction.setAmount(rs.getDouble("amount"));
            creditTransaction.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            creditTransaction.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());

            return creditTransaction;
        } catch (SQLException e) {
            throw new CouldNotAccessResultSetException("Could not access ResultSet");
        }
    }
}
