package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransactionType;

public interface TransactionTypeDao {

     void create(String transactionType);

     void delete(String transactionType);

     void update(String transactionType);

     CreditTransactionType findById(Long id);

     Iterable<CreditTransactionType> findAll();
}
