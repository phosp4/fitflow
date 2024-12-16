package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransactionType;

import java.util.ArrayList;

public interface TransactionTypeDao {

     void create(String transactionType);

     void delete(String transactionType);

     void update(String transactionType);

     CreditTransactionType findById(Long id);

     ArrayList<CreditTransactionType> findAll();
}
