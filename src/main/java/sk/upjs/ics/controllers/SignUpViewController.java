package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import javafx.scene.control.Hyperlink;

import java.io.IOException;

public class SignUpViewController {

    @FXML
    private Hyperlink SignInHereHyperlink;

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

    @Setter
    private Stage stage;

    @FXML
    void openSignInView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SignInView.fxml"));
        Parent root = loader.load();

        // pass the controller
        SignInViewController signInViewController = loader.getController();
        signInViewController.setStage(stage);

        Scene signUpScene = new Scene(root);
        stage.setScene(signUpScene);
    }

    @FXML
    void registerUser(ActionEvent event) {

    }

}
