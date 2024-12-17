package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransaction;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for CreditTransaction Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface CreditTransactionDao {

    /**
     * Loads credit transactions from a CSV file.
     *
     * @param file the CSV file containing credit transactions
     */
    void loadFromCsv(File file);

    /**
     * Creates a new credit transaction.
     *
     * @return the ID of the created credit transaction
     * @param creditTransaction the credit transaction to create
     */
    Long create(CreditTransaction creditTransaction);

    /**
     * Deletes an existing credit transaction.
     *
     * @param creditTransaction the credit transaction to delete
     */
    void delete(CreditTransaction creditTransaction);

    /**
     * Updates an existing credit transaction.
     *
     * @param creditTransaction the credit transaction to update
     */
    void update(CreditTransaction creditTransaction);

    /**
     * Finds a credit transaction by its ID.
     *
     * @param id the ID of the credit transaction to find
     * @return the found credit transaction, or null if not found
     */
    CreditTransaction findById(Long id);

    /**
     * Finds all credit transactions.
     *
     * @return a list of all credit transactions
     */
    ArrayList<CreditTransaction> findAll();
}
