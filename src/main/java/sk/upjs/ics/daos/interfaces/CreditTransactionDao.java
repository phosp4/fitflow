package sk.upjs.ics.daos.interfaces;

import sk.upjs.ics.entities.CreditTransaction;

import java.io.File;
import java.util.ArrayList;

public interface CreditTransactionDao {

    void loadFromCsv(File file);

    Long create(CreditTransaction creditTransaction);

    void delete(CreditTransaction creditTransaction);

    void update(CreditTransaction creditTransaction);

    CreditTransaction findById(Long id);

    ArrayList<CreditTransaction> findAll();
}
