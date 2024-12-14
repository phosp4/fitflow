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

import java.io.IOException;

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

        // TODO ma to overovat prihlasenie
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/MainLayout.fxml"));
        Parent root = loader.load();

        // pass the controller
        MainLayoutController mainLayoutController = loader.getController();
        mainLayoutController.setStage(stage);

        Scene MainLayoutScene = new Scene(root);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setScene(MainLayoutScene);
    }

    @FXML
    void openForgotPasswordView(TouchEvent event) {

    }

    @FXML
    void openSignUpView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SignUpView.fxml"));
        Parent root = loader.load();

        // pass the controller
        SignUpViewController signUpViewController = loader.getController();
        signUpViewController.setStage(stage);

        Scene signUpScene = new Scene(root);
        stage.setScene(signUpScene);
    }

}
