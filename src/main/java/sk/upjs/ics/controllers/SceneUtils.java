package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sk.upjs.ics.utilities.LocaleManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.CENTER_RIGHT;

/**
 * The `SceneUils` class is a utility class for various static methods needed in different scenes
 */
public class SceneUtils {

    /**
     * Loads an FXML file, applies the specified locale and stylesheet, and configures the stage.
     *
     * @param currentStage       reference to the current stage to set the scene on
     * @param fxmlPath           the path to the new FXML file to load
     * @param locale             the locale to apply to the scene
     * @param currentStylesheet  the current stylesheet to apply (dark or light mode)
     * @param minHeight          the minimum height of the stage
     * @param minWidth           the minimum width of the stage
     * @param <T>                the type of the controller associated with the FXML file
     * @return                   the controller associated with the loaded FXML file, usually used to pass the stage to it after initialization
     * @throws IOException       if an error occurs during loading the FXML file
     */
    public static <T> T loadScene(Stage currentStage, String fxmlPath, Locale locale, String currentStylesheet,  double minHeight, double minWidth) throws IOException {

        // load new resource bundle according the current locale
        ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());

        // load the scene
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath), bundle);
        Parent root = loader.load();

        // scene to parent
        Scene scene = new Scene(root);

        // Apply the current theme
        if (currentStylesheet != null && currentStylesheet.contains("style-dark.css")) {
            scene.getStylesheets().add(SceneUtils.class.getResource("/sk/upjs/ics/style-dark.css").toExternalForm());
        } else {
            scene.getStylesheets().add(SceneUtils.class.getResource("/sk/upjs/ics/style-light.css").toExternalForm());
        }

        // set preferred dimensions
        currentStage.setMinHeight(minHeight);
        currentStage.setMinWidth(minWidth);
        currentStage.setHeight(minHeight);
        currentStage.setWidth(minWidth);

        currentStage.setScene(scene);

        return loader.getController();
    }

    /**
     * return true if user wants to cancel
     * */
    public static boolean showCustomAlert(String headerTextFromBundle, String contextTextFromBundle) {
        // Retrieve the localized strings
        ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());
        String okButtonText = bundle.getString("alert.okButton");
        String cancelButtonText = bundle.getString("alert.cancelButton");
        String titleText = bundle.getString(headerTextFromBundle);
        String headerText = bundle.getString(headerTextFromBundle);

        String contextText;
        try {
            contextText = bundle.getString(contextTextFromBundle);
        } catch (Exception e) {
            contextText = contextTextFromBundle;
        }

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
}
