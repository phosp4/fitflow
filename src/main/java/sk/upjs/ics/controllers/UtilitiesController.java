package sk.upjs.ics.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

public class UtilitiesController {

    @Setter
    private Stage stage;

    public void switchToScene(String pathToScene, int minHeight, int minWidth) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToScene));
        Parent root = loader.load();

        stage.setMinHeight(minHeight);
        stage.setMinWidth(minWidth);

        Scene MainLayoutScene = new Scene(root);
        stage.setScene(MainLayoutScene);
    }
}