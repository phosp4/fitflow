package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransactionType;

import java.io.File;
import java.util.ArrayList;

/**
 * Interface for TransactionType Data Access Object (DAO).
 * Provides methods to perform CRUD operations and load data from a CSV file.
 */
public interface TransactionTypeDao {

     /**
      * Loads transaction types from a CSV file.
      *
      * @param file the CSV file containing transaction types
      */
     void loadFromCsv(File file);

     /**
      * Creates a new transaction type.
      *
      * @param transactionType the transaction type to create
      */
     void create(CreditTransactionType transactionType);

     /**
      * Deletes an existing transaction type.
      *
      * @param transactionType the transaction type to delete
      */
     void delete(CreditTransactionType transactionType);

     /**
      * Updates an existing transaction type.
      *
      * @param transactionType the transaction type to update
      */
     void update(CreditTransactionType transactionType);

     /**
      * Finds a transaction type by its ID.
      *
      * @param id the ID of the transaction type to find
      * @return the found transaction type, or null if not found
      */
     CreditTransactionType findById(Long id);

     /**
      * Finds all transaction types.
      *
      * @return a list of all transaction types
      */
     ArrayList<CreditTransactionType> findAll();
}