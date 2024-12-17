package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.Setter;
import javafx.scene.control.Hyperlink;
import sk.upjs.ics.utilities.LocaleManager;

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

        SignInViewController controller = SceneUtils.loadScene(
                stage,
                "../views/SignInView.fxml",
                LocaleManager.getLocale(),
                emailField.getScene().getStylesheets().toString(),
                480,
                400
        );
        controller.setStage(stage);
    }

    @FXML
    void registerUser(ActionEvent event) {

    }

}
