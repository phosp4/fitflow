package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.UserDao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<String> historyEntriesList;

    @FXML
    private MFXListView<String> historyReservationsList;

    @FXML
    private MFXListView<String> historyTransactionsList;

    private final UserDao userDao = Factory.INSTANCE.getUserDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHistory();
    }

    public void loadHistory() {
//        List<User> users = userDao.findAll();
//        historyListView.setItems(FXCollections.observableArrayList(userDao.findAll()));
        List<String> a = new ArrayList<>();
        a.add("platba 1");
        a.add("platba 2");
        a.add("platba 3");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        a.add("platba 4");
        historyTransactionsList.setItems(FXCollections.observableArrayList(a));
    }
}