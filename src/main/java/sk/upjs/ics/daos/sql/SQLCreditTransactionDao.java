package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;

public class SQLCreditTransactionDao implements CreditTransactionDao {

    private final Connection connection;

    public SQLCreditTransactionDao(Connection connection) {
        this.connection = connection;
    }

    String selectQuery = "SELECT id, user_id, amount, transaction_type_id, created_at FROM transactions";

    @Override
    public void create(CreditTransaction creditTransaction) {
        String insertQuery = "INSERT INTO transactions (user_id, amount, transaction_type_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getTransactionType().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }

    }

    @Override
    public void delete(CreditTransaction creditTransaction) {
        String deleteQuery = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setLong(1, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public void update(CreditTransaction creditTransaction) {
        String updateQuery = "UPDATE transactions SET user_id = ?, amount = ?, transaction_type_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, creditTransaction.getUser().getId());
            pstmt.setDouble(2, creditTransaction.getAmount());
            pstmt.setLong(3, creditTransaction.getTransactionType().getId());
            pstmt.setLong(4, creditTransaction.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public CreditTransaction findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            CreditTransaction creditTransaction = new CreditTransaction();
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Credit transaction with id " + id + " not found");
            }

            return CreditTransaction.fromResultSet(rs);

        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible");
        }
    }

    @Override
    public ArrayList<CreditTransaction> findAll() {
       try (Statement stmt = connection.createStatement()) {
           ResultSet rs = stmt.executeQuery(selectQuery);
           ArrayList<CreditTransaction> creditTransactions = new ArrayList<>();
           
           while (rs.next()) {
               creditTransactions.add(CreditTransaction.fromResultSet(rs));
           }
           
           if (creditTransactions.isEmpty()) {
               throw new NotFoundException("No credit transactions found");
           }

           return creditTransactions;
       } catch (SQLException e) {
           throw new CouldNotAccessDatabaseException("Database not accessible");
       }
    }
}
