package sk.upjs.ics.daos.sql;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.ics.daos.interfaces.TransactionTypeDao;
import sk.upjs.ics.entities.CreditTransactionType;
import sk.upjs.ics.exceptions.CouldNotAccessDatabaseException;
import sk.upjs.ics.exceptions.CouldNotAccessFileException;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * SQLTransactionTypeDao is an implementation of the TransactionTypeDao interface
 * that provides methods to interact with the transaction types in the database.
 */
public class SQLTransactionTypeDao implements TransactionTypeDao {

    private final JdbcOperations jdbcOperations;

    /**
     * Constructs a new SQLTransactionTypeDao with the specified database connection.
     *
     * @param jdbcOperations the database connection via jdbc
     */
    public SQLTransactionTypeDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private final ResultSetExtractor<ArrayList<CreditTransactionType>> resultSetExtractor = rs -> {
      ArrayList<CreditTransactionType> creditTransactionTypes =  new ArrayList<>();

      while (rs.next()) {
          creditTransactionTypes.add(CreditTransactionType.fromResultSet(rs));
      }

      return creditTransactionTypes;
    };

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

                jdbcOperations.update(connection -> {
                    PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                    pstmt.setString(1, line.trim()); // can set the whole line since it only cotains the names
                    return pstmt;
                });
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

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            pstmt.setString(1, transactionType.getName());
            return pstmt;
        });

    }

    /**
     * Deletes a transaction type from the database.
     *
     * @param transactionType the transaction type to delete
     * @throws IllegalArgumentException if the transaction type or its ID is null
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

        jdbcOperations.update("DELETE FROM credit_transaction_types WHERE id = ?", transactionType.getId());
    }

    /**
     * Updates an existing transaction type in the database.
     *
     * @param transactionType the transaction type to update
     * @throws IllegalArgumentException if the transaction type or any of its required fields are null
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

        jdbcOperations.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE credit_transaction_types SET name = ? WHERE id = ?");
            pstmt.setString(1, transactionType.getName());
            pstmt.setLong(2, transactionType.getId());
            return pstmt;
        });
    }

    /**
     * Finds a transaction type by its ID.
     *
     * @param id the ID of the transaction type to find
     * @return the transaction type with the specified ID
     * @throws IllegalArgumentException if the ID is null
     * @throws NotFoundException if the transaction type with the specified ID is not found
     */
    @Override
    public CreditTransactionType findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        ArrayList<CreditTransactionType> creditTransactionTypes = jdbcOperations.query(selectQuery + " WHERE id = ?", resultSetExtractor, id);

        if (creditTransactionTypes == null || creditTransactionTypes.isEmpty()) {
            throw new NotFoundException("Credit transaction type with id " + id + " not found!");
        }

        return creditTransactionTypes.getFirst();
    }

    /**
     * Finds all transaction types in the database.
     *
     * @return a list of all transaction types
     * @throws NotFoundException if no transaction types are found
     */
    @Override
    public ArrayList<CreditTransactionType> findAll() {
        ArrayList<CreditTransactionType> creditTransactionTypes = jdbcOperations.query(selectQuery, resultSetExtractor);

        if (creditTransactionTypes == null) {
            throw new NotFoundException("Error while finding credit transaction types");
        }

        return creditTransactionTypes;
    }
}
