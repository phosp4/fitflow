package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.stage.Stage;
import javafx.scene.control.Hyperlink;
import lombok.Setter;
import sk.upjs.ics.LocaleManager;

import java.io.IOException;
import java.util.ResourceBundle;

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
    private Hyperlink forgotPasswordHyperlink;

    @FXML
    private Hyperlink signUpHereHyperlink;

    @Setter
    private Stage stage;

    @FXML
    void showForgotPasswordView(ActionEvent event) {

    }

    @FXML
    void signIn(ActionEvent event) throws IOException {

        MainLayoutController controller = SceneLoader.loadScene(
                stage,
                "../views/MainLayout.fxml",
                LocaleManager.getLocale(),
                emailTextField.getScene().getStylesheets().toString(),
                600,
                800
        );
        controller.setStage(stage);
    }

    @FXML
    void openForgotPasswordView(TouchEvent event) {

    }

    @FXML
    void openSignUpView(ActionEvent event) throws IOException {
        SignUpViewController controller = SceneLoader.loadScene(
                stage,
                "../views/SignUpView.fxml",
                LocaleManager.getLocale(),
                emailTextField.getScene().getStylesheets().toString(),
                480,
                400
        );
        controller.setStage(stage);
    }
}