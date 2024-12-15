package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationViewController implements Initializable {

    @FXML
    private MFXToggleButton TestToggle;

    @FXML
    private MFXButton createReservationButton;

    @FXML
    private MFXTextField noteToTrainer;

    @FXML
    private MFXDatePicker reserveDatePicker;

    @FXML
    private MFXComboBox<String> reserveTimeComboBox;

    @FXML
    private MFXComboBox<String> reserveTrainerComboBox;

    @FXML
    void createReservation(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // demo
        reserveTimeComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
        Platform.runLater(() -> reserveTimeComboBox.setValue("10:00"));
    }
}