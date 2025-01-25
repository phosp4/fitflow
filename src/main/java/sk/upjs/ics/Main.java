package sk.upjs.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.controllers.SignInViewController;
import sk.upjs.ics.security.InitAdminGenerator;
import sk.upjs.ics.utilities.DatabaseUtil;
import sk.upjs.ics.utilities.LocaleManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Main class for the FitFlow application.
 * This class initializes the database, sets up the admin user, and starts the JavaFX application.
 */
public class Main extends Application {
    /**
     * The main entry point for the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     * @param stage the primary stage for this application
     * @throws Exception if an error occurs during application startup
     */
    @Override
    public void start(Stage stage) throws Exception {
        initializeDatabase();
        initializeAdminUser();

        // for internationalization
        ResourceBundle bundle = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", LocaleManager.getLocale());

        var fxmlLoader = new FXMLLoader(getClass().getResource("views/SignInView.fxml"), bundle);
        Parent rootPane = fxmlLoader.load();

        // get the controller and pass the Stage
        SignInViewController signInViewController = fxmlLoader.getController();
        signInViewController.setStage(stage);

        var scene = new Scene(rootPane);
        scene.getStylesheets().add(getClass().getResource("/sk/upjs/ics/style-light.css").toExternalForm());
        stage.setMinWidth(400);
        stage.setMinHeight(480);
        stage.setTitle("FitFlow");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the database. If the database file does not exist, it creates a new one.
     */
    private void initializeDatabase() {
        Path path = Paths.get("fitflow.db");
        if (Files.exists(path)) {
            System.out.println("Database exists.");
        } else {
            System.out.println("Database does not exist. Initializing...");
            try {
                DatabaseUtil.initializeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes the admin user by calling the InitAdminGenerator.
     */
    private void initializeAdminUser() {
        System.out.println("Initializing admin user...");
        InitAdminGenerator.main(new String[0]);
    }
}