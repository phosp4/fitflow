package sk.upjs.ics;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ReservationViewController {

    @FXML
    private MFXToggleButton TestToggle;

    @FXML
    private MFXButton createReservationButton;

    @FXML
    private MFXTextField noteToTrainer;

    @FXML
    private MFXDatePicker reserveDatePicker;

    @FXML
    private MFXComboBox<?> reserveTimeComboBox;

    @FXML
    private MFXComboBox<?> reserveTrainerComboBox;

    @FXML
    void createReservation(ActionEvent event) {

    }

}