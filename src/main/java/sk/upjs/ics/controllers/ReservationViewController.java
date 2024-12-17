package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.models.ReservationModel;
import sk.upjs.ics.models.UserModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReservationViewController implements Initializable {

    ReservationModel reservationModel = new ReservationModel();
    UserModel userModel = new UserModel();

//    @FXML
//    private MFXToggleButton TestToggle;

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
        String selectedTrainer = reserveTrainerComboBox.getValue();
        String selectedTime = reserveTimeComboBox.getValue();
        String selectedDate = reserveDatePicker.getValue().toString();
        String note = noteToTrainer.getText();

        if (selectedTrainer == null) {
            System.out.println("No trainer selected");
            return;
        }

        User user = userModel.getUserByName(selectedTrainer);
        if (user == null) {
            System.out.println("No user found with the name " + selectedTrainer);
            return;
        }

        reservationModel.createReservation(user, selectedDate, selectedTime, note);
        
        System.out.println("The user has selected " + selectedTrainer + " on " + selectedDate + " at " + selectedTime);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> trainers = userModel.getTrainers();
        Platform.runLater(() -> reserveTrainerComboBox.setItems(FXCollections.observableArrayList(trainers)));

        // Select the first trainer if the list is not empty
        if (!trainers.isEmpty()) {
            System.out.println(trainers.getFirst());
            Platform.runLater(() -> reserveTrainerComboBox.setValue(trainers.get(0)));
        }

        // demo
        reserveTimeComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
        Platform.runLater(() -> reserveTimeComboBox.setValue("10:00"));
    }
}