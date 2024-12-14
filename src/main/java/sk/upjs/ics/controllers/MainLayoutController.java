package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @Setter
    private Stage stage;
    public StackPane contentArea;

    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton menuHistoryButton;

    @FXML
    private MFXButton menuHomeButton;

    @FXML
    private MFXButton menuReservationsButton;

    @FXML
    private MFXButton menuSettingsButton;

    @FXML
    private MFXButton menuWalletButton;

    @FXML
    private Label titleLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadView("../views/HomeView.fxml");
    }

    @FXML
    void logUserOut(ActionEvent event) throws IOException {
        // todo spojit to nejako s tym principalom

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SignInView.fxml"));
        Parent root = loader.load();

        // pass the controller
        SignInViewController signInViewController = loader.getController();
        signInViewController.setStage(stage);

        Scene signUpScene = new Scene(root);

        // passing the information about the theme
        Scene currentScene = borderPane.getScene();
        if (currentScene.getStylesheets().contains(getClass().getResource("/sk/upjs/ics/style-dark.css").toExternalForm())) {
            signUpScene.getStylesheets().add(getClass().getResource("/sk/upjs/ics/style-dark.css").toExternalForm());
        } else {
            signUpScene.getStylesheets().add(getClass().getResource("/sk/upjs/ics/style-light.css").toExternalForm());
        }

        stage.setMinWidth(400);
        stage.setMinHeight(480);
        stage.setWidth(400);
        stage.setHeight(480);
        stage.setScene(signUpScene);

    }

    @FXML
    void showAboutScene(ActionEvent event) {
        loadView("../views/MainLayout.fxml");
    }

    @FXML
    void showHistoryScene(ActionEvent event) {
        loadView("../views/HistoryView.fxml");
    }

    @FXML
    void showHomeScene(ActionEvent event) {
        loadView("../views/HomeView.fxml");
    }

    @FXML
    void showReservationsScene(ActionEvent event) {
        loadView("../views/ReservationView.fxml");
    }

    @FXML
    void showSettingsScene(ActionEvent event) {
        loadView("../views/SettingsView.fxml");
    }

    @FXML
    void showWalletScene(ActionEvent event) {
        loadView("../views/WalletView.fxml");
    }

    private void loadView(String view) {

        var loader = new FXMLLoader(getClass().getResource(view));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + view, e);
        }

        borderPane.setCenter(pane);
    }
}
