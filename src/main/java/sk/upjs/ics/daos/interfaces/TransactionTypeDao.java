package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.TransactionType;

public interface TransactionTypeDao {

     void create(String transactionType);

     void delete(String transactionType);

     void update(String transactionType);

     TransactionType findById(Long id);

     Iterable<TransactionType> findAll();
}
