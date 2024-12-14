package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransaction;

public interface CreditTransactionDao {

    void create(CreditTransaction creditTransaction);

    void delete(CreditTransaction creditTransaction);

    void update(CreditTransaction creditTransaction);

    CreditTransaction findById(Long id);

    Iterable<CreditTransaction> findAll();
}
