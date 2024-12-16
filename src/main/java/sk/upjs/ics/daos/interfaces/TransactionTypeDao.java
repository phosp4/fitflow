package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransactionType;

import java.io.File;
import java.util.ArrayList;

public interface TransactionTypeDao {

     void loadFromCsv(File file);

     void create(CreditTransactionType transactionType);

     void delete(CreditTransactionType transactionType);

     void update(CreditTransactionType transactionType);

     CreditTransactionType findById(Long id);

     ArrayList<CreditTransactionType> findAll();
}
