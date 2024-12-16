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
import sk.upjs.ics.models.TransactionModel;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<String> historyEntriesList;

    @FXML
    private MFXListView<String> historyTransactionsList;

    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();
    private final CreditTransactionDao creditTransactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final ReservationDao reservationDao = Factory.INSTANCE.getReservationDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHistory();
    }

    public void loadHistory() {
        TransactionModel transactionModel = new TransactionModel();
        ArrayList<String> transactionHistory = transactionModel.loadTransactionsHistory();
        historyTransactionsList.setItems(FXCollections.observableArrayList(transactionHistory));
        //// nacitaj tranzakcie
        // nacitaj arraylist tranzakcii prisluchajucich userovi
        // vyfiltruj len tie, ktore su purchases
        // spracuj ich do spravnej textovej podoby
        // nahod ich to mfxListu
        //// to iste s entries, ak uz maju aj vystup

//        // load transactions
//        //ArrayList<CreditTransaction> creditTransactionsHistory = creditTransactionDao.findAll();
//
//        // load reservations which are not active
//        ArrayList<Reservation> reservationsHistory = reservationDao.findAllOfOneUser(principal.getId());
//
//        if (reservationsHistory != null) {
//
//        }
//
////        if (visitHistory != null) {
////            ObservableList<Visit> visitHistoryObservable = FXCollections.observableArrayList(visitHistory);
////            historyEntriesList.setItems(visitHistoryObservable);
////        }
//
//        List<String> reservationHistoryString = new ArrayList<>();
//        for (Reservation reservation : reservationsHistory) {
//            reservationHistoryString.add(reservation.getNoteToTrainer());
//        }
//        historyReservationsList.setItems(FXCollections.observableArrayList(reservationHistoryString));
    }
}