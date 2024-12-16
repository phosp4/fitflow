package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.TransactionTypeDao;
import sk.upjs.ics.entities.CreditTransactionType;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLTransactionTypeDao implements TransactionTypeDao {

    private final Connection connection;

    public SQLTransactionTypeDao(Connection connection) {
        this.connection = connection;
    }


    private final String selectQuery = "SELECT id, name FROM credit_transaction_types";
    private final String insertQuery = "INSERT INTO credit_transaction_types (name) VALUES (?)";

    @Override
    public void loadFromCsv(File file) {
        try (Scanner scanner = new Scanner(file)) {
            // skip header
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isEmpty()) {
                    continue;
                }

                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    throw new CouldNotAccessDatabaseException("Database not accessible", e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new CouldNotAccessFileException("Could not access file");
        }
    }

    @Override
    public void create(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getId() != null) {
            throw new IllegalArgumentException("The transaction type already has an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, transactionType.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void delete(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getId() == null) {
            throw new IllegalArgumentException("The transaction type does not have an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM credit_transaction_types WHERE id = ?")) {
            pstmt.setLong(1, transactionType.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public void update(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getId() == null) {
            throw new IllegalArgumentException("The transaction type does not have an id");
        }

        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE credit_transaction_types SET name = ? WHERE id = ?")) {
            pstmt.setString(1, transactionType.getName());
            pstmt.setLong(2, transactionType.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public CreditTransactionType findById(Long id) {
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery + " WHERE id = ?")) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("Transaction type with id " + id + " not found");
            }

            return CreditTransactionType.fromResultSet(rs);
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    @Override
    public ArrayList<CreditTransactionType> findAll() {
        ArrayList<CreditTransactionType> transactionTypes = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactionTypes.add(CreditTransactionType.fromResultSet(rs));
            }

            if (transactionTypes.isEmpty()) {
                throw new NotFoundException("No transaction types found");
            }

            return transactionTypes;
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }
}
