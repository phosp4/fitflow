package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Setter;
import sk.upjs.ics.utilities.LocaleManager;
import sk.upjs.ics.security.Auth;

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

        if (SceneUtils.showCustomAlert("alert.logoutTitle", "alert.logoutContextText")) {
            return;
        }

        Auth.INSTANCE.setPrincipal(null);

        SignInViewController controller = SceneUtils.loadScene(
                stage,
                "../views/SignInView.fxml",
                LocaleManager.getLocale(),
                menuHistoryButton.getScene().getStylesheets().toString(),
                480,
                400
        );
        controller.setStage(stage);
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

        // for internationalization
        ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());

        var loader = new FXMLLoader(getClass().getResource(view), bundle);

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + view, e);
        }

        borderPane.setCenter(pane);
    }
}
