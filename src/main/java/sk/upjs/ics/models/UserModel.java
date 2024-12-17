package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.CreditTransactionType;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.time.Instant;

public class UserModel {
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final CreditTransactionDao transactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();

    public User getCurrentUser() {
        Principal principal = Auth.INSTANCE.getPrincipal();
        return userDao.findById(principal.getId());
    }

    public void updateCurrentUserCreditBalance(long addBalance) {
        // create a transaction first
        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setUser(getCurrentUser());
        creditTransaction.setAmount(addBalance);
        creditTransaction.setCreditTransactionType(Factory.INSTANCE.getTransactionTypeDao().findById(2L));
        transactionDao.create(creditTransaction);

        // now update the balance
        User user = getCurrentUser();
        user.setCreditBalance(getCurrentUser().getCreditBalance() + addBalance);
        userDao.updateBalance(user);

    }
}