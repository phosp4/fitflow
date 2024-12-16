package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.stage.Stage;
import javafx.scene.control.Hyperlink;
import lombok.Setter;
import sk.upjs.ics.Factory;
import sk.upjs.ics.LocaleManager;
import sk.upjs.ics.exceptions.AuthenticationException;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.AuthDao;
import sk.upjs.ics.security.Principal;

import java.io.IOException;
import java.time.Instant;
import java.util.ResourceBundle;

public class SignInViewController {

    @FXML
    private MFXTextField emailTextField;

//    @FXML
//    private Label forgotPasswordLabel;

    @FXML
    private Label IncorrectLabel;

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

    private final AuthDao authDao = Factory.INSTANCE.getAuthDao();

    @FXML
    void showForgotPasswordView(ActionEvent event) {

    }

    @FXML
    void signIn(ActionEvent event) throws IOException {

        var email = emailTextField.getText();
        var password = passwordField.getText();

        Principal principal;
        try {
            principal = authDao.authenticate(email, password);
        } catch (AuthenticationException e) {
            ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());
            String incorrectMessage = bundle.getString("auth.incorrect");
            IncorrectLabel.setTextFill(javafx.scene.paint.Color.RED);
            IncorrectLabel.setText(incorrectMessage);
            System.out.println("Incorrect password");
            return;
        }

        Auth.INSTANCE.setPrincipal(principal);
//        incorrectPasswordLabel.getScene().getWindow().hide();


        MainLayoutController controller = SceneUtils.loadScene(
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
        SignUpViewController controller = SceneUtils.loadScene(
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