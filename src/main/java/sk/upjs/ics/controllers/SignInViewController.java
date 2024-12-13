package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;

public class SignInViewController {

    @FXML
    private MFXTextField emailTextField;

    @FXML
    private Label forgotPasswordLabel;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private MFXButton signInButton;

    @FXML
    private Label signUpLabel;

    @FXML
    void openForgotPasswordView(TouchEvent event) {

    }

    @FXML
    void openSignUpview(TouchEvent event) {
    }

    @FXML
    void signIn(ActionEvent event) {

    }

}
