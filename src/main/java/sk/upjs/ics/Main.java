package sk.upjs.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        var mainController = new Controller();
        var fxmlLoader = new FXMLLoader(getClass().getResource("views/MainLayout.fxml"));
//        fxmlLoader.setController(mainController);
        Parent rootPane = fxmlLoader.load();

        var scene = new Scene(rootPane);
        //stage.setMinHeight(600);
        //stage.setMinWidth(800);
        stage.setTitle("FitFlow");
        stage.setScene(scene);
        stage.show();
        }
    }