package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sk.upjs.ics.models.TransactionModel;
import sk.upjs.ics.models.VisitModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<String> historyEntriesList;

    @FXML
    private MFXListView<String> historyTransactionsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHistory();
    }

    public void loadHistory() {
        TransactionModel transactionModel = new TransactionModel();
        ArrayList<String> transactionHistory = transactionModel.loadTransactionsHistory();
        historyTransactionsList.setItems(FXCollections.observableArrayList(transactionHistory));

        VisitModel visitModel = new VisitModel();
        ArrayList<String> visitHistory = visitModel.loadVisitsHistory();
        historyEntriesList.setItems(FXCollections.observableArrayList(visitHistory));
    }
}