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

public class TransactionModel {

    private final CreditTransactionDao creditTransactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();
    public ArrayList<String> loadTransactionsHistory() {

        // nacitaj arraylist tranzakcii prisluchajucich userovi
        ArrayList<CreditTransaction> creditTransactions = creditTransactionDao.findAll();

        // nechaj len prisluchajuce userovi a ktore su purchases
        for (int i = 0; i < creditTransactions.size(); i++) {
            CreditTransaction creditTransaction = creditTransactions.get(i);

            // ak to nie je prisluchajuci user alebo nie je credit_purchase, tak ju odstran
            if (!creditTransaction.getUser().getId().equals(principal.getId()) ||
                    !creditTransaction.getCreditTransactionType().getId().equals(2L)) {
                creditTransactions.remove(creditTransaction);
            } else {
                System.out.println("CreditTransaction: " + creditTransaction);
            }
        }

        // spracuj ich do spravnej textovej podoby
        ArrayList<String> transactionHistory = new ArrayList<>();
        for (CreditTransaction creditTransaction : creditTransactions) {
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
