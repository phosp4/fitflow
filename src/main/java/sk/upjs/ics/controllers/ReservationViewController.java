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
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.ReservationDao;
import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.entities.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ReservationViewController implements Initializable {

//    @FXML
//    private MFXToggleButton TestToggle;
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final TrainerIntervalDao trainerIntervalDao = Factory.INSTANCE.getTrainerIntervalDao();
    private final ReservationDao reservationDao = Factory.INSTANCE.getReservationDao();

    private User selectedTrainer;

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

        System.out.println("The user has selected " + selectedTrainer + " on " + selectedDate + " at " + selectedTime);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reserveTrainerComboBox.getItems().addAll("Trainer 1", "Trainer 2", "Trainer 3", "Trainer 4", "Trainer 5");

        // demo
        reserveTimeComboBox.getItems().addAll("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
        Platform.runLater(() -> reserveTimeComboBox.setValue("10:00"));
    }
}