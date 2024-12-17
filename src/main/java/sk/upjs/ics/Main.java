package sk.upjs.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.controllers.SignInViewController;
import sk.upjs.ics.security.InitAdminGenerator;
import sk.upjs.ics.utilities.LocaleManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

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

    private void initializeDatabase() {
        Path path = Paths.get("fitflow.db");
        if (Files.exists(path)) {
            System.out.println("Database exists.");
        } else {
            System.out.println("Database does not exist. Initializing...");
            try {
                DatabaseUtil.initializeDatabase();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initializeAdminUser() {
        System.out.println("Initializing admin user...");
        InitAdminGenerator.main(new String[0]);
    }
    }