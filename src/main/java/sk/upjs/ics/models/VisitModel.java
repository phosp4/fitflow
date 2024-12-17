package sk.upjs.ics.models;

import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class VisitModel {

    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();
    public ArrayList<String> loadVisitsHistory() {

            // nacitaj arraylist tranzakcii prisluchajucich userovi
            ArrayList<Visit> visits = visitDao.findAll();


            // vymaz nevyhovujuce tranzakcie
            Iterator<Visit> iterator = visits.iterator();
            while (iterator.hasNext()) {
                Visit visit = iterator.next();

                // Remove if it does not belong to the current user or is not a visit
                if (!visit.getUser().getId().equals(principal.getId()) ||
                        visit.getCreditTransaction() == null ||
                        !visit.getCreditTransaction().getCreditTransactionType().getId().equals(1L)) {
                    iterator.remove();
                } else {
                    System.out.println("Visit: " + visit);
                }
            }

            // spracuj ich do spravnej textovej podoby
            ArrayList<String> visitHistory = new ArrayList<>();
            for (Visit visit : visits) {
                double amount;
                try {
                    amount = visit.getCreditTransaction().getAmount();
                } catch (NullPointerException e) {
                    amount = 0;
                }
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
