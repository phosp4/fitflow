package sk.upjs.ics.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.LocaleManager;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The `SceneLoader` class is responsible for loading and setting up scenes in the application.
 * It provides a method to load an FXML file, apply the appropriate locale and stylesheet, and configure the stage.
 * With setting up a new scene, current locale is applied together with current theme
 */
public class SceneLoader {

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
        FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath), bundle);
        Parent root = loader.load();

        // scene to parent
        Scene scene = new Scene(root);

        // Apply the current theme
        if (currentStylesheet != null && currentStylesheet.contains("style-dark.css")) {
            scene.getStylesheets().add(SceneLoader.class.getResource("/sk/upjs/ics/style-dark.css").toExternalForm());
        } else {
            scene.getStylesheets().add(SceneLoader.class.getResource("/sk/upjs/ics/style-light.css").toExternalForm());
        }

        // set preferred dimensions
        currentStage.setMinHeight(minHeight);
        currentStage.setMinWidth(minWidth);
        currentStage.setHeight(minHeight);
        currentStage.setWidth(minWidth);

        currentStage.setScene(scene);

        return loader.getController();
    }
}
