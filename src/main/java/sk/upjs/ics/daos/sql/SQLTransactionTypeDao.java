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

/**
 * SQLTransactionTypeDao is an implementation of the TransactionTypeDao interface
 * that provides methods to interact with the transaction types in the database.
 */
public class SQLTransactionTypeDao implements TransactionTypeDao {

    private final Connection connection;

    /**
     * Constructs a new SQLTransactionTypeDao with the specified database connection.
     *
     * @param connection the database connection
     */
    public SQLTransactionTypeDao(Connection connection) {
        this.connection = connection;
    }

    private final String selectQuery = "SELECT id, name FROM credit_transaction_types";
    private final String insertQuery = "INSERT INTO credit_transaction_types (name) VALUES (?)";

    /**
     * Loads transaction types from a CSV file and inserts them into the database.
     *
     * @param file the CSV file containing transaction type data
     * @throws CouldNotAccessFileException if the file cannot be accessed
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
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

    /**
     * Creates a new transaction type in the database.
     *
     * @param transactionType the transaction type to create
     * @throws IllegalArgumentException if the transaction type or its name is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void create(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getName() == null) {
            throw new IllegalArgumentException("Transaction type name cannot be null");
        }

        if (findById(transactionType.getId()) != null) {
            throw new IllegalArgumentException("Transaction type with id " + transactionType.getId() + " already exists");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, transactionType.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Deletes a transaction type from the database.
     *
     * @param transactionType the transaction type to delete
     * @throws IllegalArgumentException if the transaction type or its ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void delete(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getId() == null) {
            throw new IllegalArgumentException("The transaction type does not have an id");
        }

        if (findById(transactionType.getId()) == null) {
            throw new NotFoundException("Transaction type with id " + transactionType.getId() + " not found");
        }

        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM credit_transaction_types WHERE id = ?")) {
            pstmt.setLong(1, transactionType.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Updates an existing transaction type in the database.
     *
     * @param transactionType the transaction type to update
     * @throws IllegalArgumentException if the transaction type or any of its required fields are null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     */
    @Override
    public void update(CreditTransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (transactionType.getId() == null) {
            throw new IllegalArgumentException("The transaction type does not have an id");
        }

        if (transactionType.getName() == null) {
            throw new IllegalArgumentException("Transaction type name cannot be null");
        }

        if (findById(transactionType.getId()) == null) {
            throw new NotFoundException("Transaction type with id " + transactionType.getId() + " not found");
        }

        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE credit_transaction_types SET name = ? WHERE id = ?")) {
            pstmt.setString(1, transactionType.getName());
            pstmt.setLong(2, transactionType.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CouldNotAccessDatabaseException("Database not accessible", e);
        }
    }

    /**
     * Finds a transaction type by its ID.
     *
     * @param id the ID of the transaction type to find
     * @return the transaction type with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if the transaction type with the specified ID is not found
     */
    @Override
    public CreditTransactionType findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

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

    /**
     * Finds all transaction types in the database.
     *
     * @return a list of all transaction types
     * @throws CouldNotAccessDatabaseException if the database cannot be accessed
     * @throws NotFoundException if no transaction types are found
     */
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
