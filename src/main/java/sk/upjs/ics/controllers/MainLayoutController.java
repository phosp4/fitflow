package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Setter;
import sk.upjs.ics.LocaleManager;
import sk.upjs.ics.security.Auth;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.*;

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

        if (showLogoutAlert()) {
            return;
        }

        Auth.INSTANCE.setPrincipal(null);

        SignInViewController controller = SceneLoader.loadScene(
                stage,
                "../views/SignInView.fxml",
                LocaleManager.getLocale(),
                menuHistoryButton.getScene().getStylesheets().toString(),
                480,
                400
        );
        controller.setStage(stage);
    }

    /*
    * return true if user wants to cancel logout
    * */
    private boolean showLogoutAlert() {
        // Retrieve the localized strings
        ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());
        String okButtonText = bundle.getString("alert.okButton");
        String cancelButtonText = bundle.getString("alert.cancelButton");
        String contextText = bundle.getString("alert.logoutContextText");
        String titleText = bundle.getString("alert.logoutTitle");
        String headerText = bundle.getString("alert.logoutTitle");

        // Create the custom dialog
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(titleText);
        dialog.setHeaderText(titleText);

        // Create the content for the dialog
        VBox content = new VBox();
        content.getChildren().add(new Label(contextText));
        content.setAlignment(CENTER_LEFT);

        // Create the custom buttons
        MFXButton okButton = new MFXButton(okButtonText);
        MFXButton cancelButton = new MFXButton(cancelButtonText);

        // Set button actions
        okButton.setOnAction(e -> dialog.setResult(true));
        cancelButton.setOnAction(e -> dialog.setResult(false));

        // Add buttons to an HBox
        HBox buttonBox = new HBox(10, cancelButton, okButton);
        buttonBox.setHgrow(okButton, Priority.ALWAYS);
        buttonBox.setHgrow(cancelButton, Priority.ALWAYS);
        buttonBox.setMinWidth(300);
        buttonBox.setAlignment(CENTER_RIGHT);

        // Add content and buttons to the dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().clear(); // Remove default buttons
        dialogPane.setContent(new VBox(content, buttonBox));

        // Show the dialog and wait for the user's response
        Optional<Boolean> result = dialog.showAndWait();

        return result.isPresent() && !result.get();
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
