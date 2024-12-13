package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.TouchEvent;

public class SignUpViewController {

    @FXML
    private MFXDatePicker birthDateField;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXTextField firstNameField;

    @FXML
    private MFXTextField lastNameField;

    @FXML
    private MFXPasswordField passwordField1;

    @FXML
    private MFXPasswordField passwordField2;

    @FXML
    private MFXTextField phoneField;

    @FXML
    private MFXButton registerButton;

    @FXML
    void openSignInView(TouchEvent event) {

    }

    @FXML
    void registerUser(ActionEvent event) {

    }

}
