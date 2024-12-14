package sk.upjs.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.upjs.ics.controllers.SignInViewController;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        var fxmlLoader = new FXMLLoader(getClass().getResource("views/SignInView.fxml"));
        Parent rootPane = fxmlLoader.load();

        // get the controller and pass the Stage
        SignInViewController signInViewController = fxmlLoader.getController();
        signInViewController.setStage(stage);

        var scene = new Scene(rootPane);
        //stage.setMinHeight(600);
        //stage.setMinWidth(800);
        stage.setTitle("FitFlow");
        stage.setScene(scene);
        stage.show();
        }
    }