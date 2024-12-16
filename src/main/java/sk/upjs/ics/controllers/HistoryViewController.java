package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.Reservation;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<Visit> historyEntriesList;

    @FXML
    private MFXListView<String> historyReservationsList;

    @FXML
    private MFXListView<CreditTransaction> historyTransactionsList;

    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();
    private final CreditTransactionDao creditTransactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final ReservationDao reservationDao = Factory.INSTANCE.getReservationDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHistory();
    }

    public void loadHistory() {
//        List<User> users = userDao.findAll();
//        historyListView.setItems(FXCollections.observableArrayList(userDao.findAll()));
        //List<String> a = new ArrayList<>();
        //a.add("platba 1");
        //historyTransactionsList.setItems(FXCollections.observableArrayList(a));

        // load entries
        ArrayList<Visit> visitHistory = visitDao.findAll();

        // load transactions
        //ArrayList<CreditTransaction> creditTransactionsHistory = creditTransactionDao.findAll();

        // load reservations which are not active
        ArrayList<Reservation> reservationsHistory = reservationDao.findAllOfOneUser(principal.getId());

        if (visitHistory != null) {
            ObservableList<Visit> visitHistoryObservable = FXCollections.observableArrayList(visitHistory);
            historyEntriesList.setItems(visitHistoryObservable);
        }

        List<String> reservationHistoryString = new ArrayList<>();
        for (Reservation reservation : reservationsHistory) {
            reservationHistoryString.add(reservation.getNoteToTrainer());
        }
        // todo
        historyReservationsList.setItems(FXCollections.observableArrayList(reservationHistoryString));
    }
}