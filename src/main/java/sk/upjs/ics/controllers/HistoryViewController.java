package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryViewController implements Initializable {

    @FXML
    private MFXListView<String> historyListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHistory();
    }

    public void loadHistory() {
        //historyListView.setItems(FXCollections.observableArrayList(attendanceDao.findAllSortedByDate()));
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