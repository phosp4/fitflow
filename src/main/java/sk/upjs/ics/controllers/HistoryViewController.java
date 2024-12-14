package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sk.upjs.ics.Factory;
import sk.upjs.ics.users.SQLUserDao;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<String> historyListView;

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
        historyListView.setItems(FXCollections.observableArrayList(a));
    }
}