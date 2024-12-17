package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TransactionModel {

    private final CreditTransactionDao creditTransactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();
    public ArrayList<String> loadTransactionsHistory() {

        // nacitaj arraylist tranzakcii prisluchajucich userovi
        ArrayList<CreditTransaction> creditTransactions = creditTransactionDao.findAll();

        // nechaj len prisluchajuce userovi a ktore su purchases
        Iterator<CreditTransaction> iterator = creditTransactions.iterator();
        while (iterator.hasNext()) {
            CreditTransaction creditTransaction = iterator.next();

            // Remove if it does not belong to the current user or is not a credit_purchase
            if (!creditTransaction.getUser().getId().equals(principal.getId()) ||
                    !(creditTransaction.getCreditTransactionType().getId().equals(2L))) {
                iterator.remove();
            } else {
                System.out.println("CreditTransaction: " + creditTransaction);
            }
        }

        // spracuj ich do spravnej textovej podoby
        ArrayList<String> transactionHistory = new ArrayList<>();
        for (CreditTransaction creditTransaction : creditTransactions) {

            // todo not sure why it has to be also here but now it is working
            if (creditTransaction.getCreditTransactionType().getId() != 2L) {
                continue;
            }

            double amount = creditTransaction.getAmount();
            LocalDateTime time = LocalDateTime.ofInstant(creditTransaction.getCreatedAt(), ZoneId.systemDefault());
            String formattedTime = time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            transactionHistory.add(formattedTime + " | " + amount + "€");
        }

        // Reverse the order of the transaction history
        Collections.reverse(transactionHistory);

        return transactionHistory;
    }
}
