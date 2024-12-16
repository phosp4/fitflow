package sk.upjs.ics.daos.sql;

import sk.upjs.ics.daos.interfaces.TransactionTypeDao;
import sk.upjs.ics.entities.CreditTransactionType;

import java.sql.Connection;
import java.util.ArrayList;

public class SQLTransactionTypeDao implements TransactionTypeDao {

    private final Connection connection;

    public SQLTransactionTypeDao(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void create(CreditTransactionType transactionType) {

    }

    @Override
    public void delete(CreditTransactionType transactionType) {

    }

    @Override
    public void update(CreditTransactionType transactionType) {

    }

    @Override
    public CreditTransactionType findById(Long id) {
        return null;
    }

    @Override
    public ArrayList<CreditTransactionType> findAll() {
        return null;
    }
}
