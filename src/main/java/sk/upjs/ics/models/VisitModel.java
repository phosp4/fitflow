package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class VisitModel {

    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();
    public ArrayList<String> loadVisitsHistory() {

            // nacitaj arraylist tranzakcii prisluchajucich userovi
            ArrayList<Visit> visits = visitDao.findAll();

            // nechaj len prisluchajuce userovi a ktore su purchases
            for (int i = 0; i < visits.size(); i++) {
                Visit visit = visits.get(i);

                // ak to nie je prisluchajuci user alebo nie je visit, tak ju odstran
                if (!visit.getUser().getId().equals(principal.getId()) ||
                        !visit.getCreditTransaction().getCreditTransactionType().getId().equals(1L)) {
                    visits.remove(visit);
                } else {
                    System.out.println("Visit: " + visit);
                }
            }

            // spracuj ich do spravnej textovej podoby
            ArrayList<String> visitHistory = new ArrayList<>();
            for (Visit visit : visits) {
                double amount = visit.getCreditTransaction().getAmount();
                LocalDateTime checkin = LocalDateTime.ofInstant(visit.getCheckInTime(), ZoneId.systemDefault());
                String formattedCheckin = checkin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

                LocalDateTime checkout = LocalDateTime.ofInstant(visit.getCheckOutTime(), ZoneId.systemDefault());
                String formattedCheckout = checkout.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                visitHistory.add(formattedCheckin + " - " + formattedCheckout + " | -" + amount + "€");
            }

            // Reverse the order of the transaction history
            Collections.reverse(visitHistory);

            return visitHistory;
    }
}
