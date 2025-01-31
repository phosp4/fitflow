package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.TrainerIntervalDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.daos.sql.SQLUserDao;
import sk.upjs.ics.entities.TrainerInterval;
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
    private MFXComboBox<TrainerInterval> reserveDateCombobox;

    @FXML
    private MFXComboBox<TrainerInterval> reserveTimeComboBox;

    @FXML
    private MFXComboBox<User> reserveTrainerComboBox;
    @FXML
    private MFXToggleButton TestToggle;

    @FXML
    private MFXButton createReservationButton;

    @FXML
    private MFXTextField noteToTrainer;

    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final TrainerIntervalDao trainerIntervalDao = Factory.INSTANCE.getTrainerIntervalDao();

    @FXML
    void createReservation(ActionEvent event) {
//        String selectedTrainer = reserveTrainerComboBox.getValue();
//        String selectedTime = reserveTimeComboBox.getValue();
//        String selectedDate = reserveDateCombobox.getValue();
//        String note = noteToTrainer.getText();
//
//        if (selectedTrainer == null) {
//            System.out.println("No trainer selected");
//            return;
//        }
//
//        User user = userModel.getUserByName(selectedTrainer);
//        if (user == null) {
//            System.out.println("No user found with the name " + selectedTrainer);
//            return;
//        }
//
//        reservationModel.createReservation(user, selectedDate, selectedTime, note);
//
//        System.out.println("The user has selected " + selectedTrainer + " on " + selectedDate + " at " + selectedTime);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<User> trainers = userModel.getTrainers();
        Platform.runLater(() -> reserveTrainerComboBox.setItems(FXCollections.observableArrayList(trainers)));

        // Select the first trainer if the list is not empty
        if (!trainers.isEmpty()) {
            System.out.println(trainers.getFirst());
            Platform.runLater(() -> reserveTrainerComboBox.setValue(trainers.get(0)));
        }
    }

    @FXML
    void reservationTrainerPicked(ActionEvent event) {

        // podla trenera nacitaj intervaly
        User selectedTrainer = reserveTrainerComboBox.getValue();
        // poziadaj dao o intervaly ktore prisluchaju danemu trenerovi
        ArrayList<TrainerInterval> intervals = trainerIntervalDao.findByTrainer(selectedTrainer);

        //Platform.runLater(() -> reserveDateCombobox.setItems(FXCollections.observableArrayList(intervals)));

        reserveDateCombobox.setItems(FXCollections.observableArrayList(intervals));

        // TODO - tu som skoncil - ide to, ale potrebujem "dva rozne toStringy" a to sa da pomocou cell factory...
//        reserveTimeComboBox.setCellFactory(comboBox -> new MFXComboBoxCell<TrainerInterval>() {
//            protected void updateItem(TrainerInterval item, boolean empty) {
//                super.updateItem(item);
//                if (empty || item == null) {
//                    reserveDateCombobox.setText("");
//                } else {
//                    reserveDateCombobox.setText(item.getStartTime() + " - " + item.getEndTime());
//                }
//            }
//        });
    }

    @FXML
    void ReservationDatePicked(ActionEvent event) {

    }

    @FXML
    void reservationTimePicked(ActionEvent event) {

    }

}