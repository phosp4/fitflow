package sk.upjs.ics.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSignLayoutController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String defaultView = "../views/SignInView.fxml";
        var loader = new FXMLLoader(getClass().getResource(defaultView));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + defaultView, e);
        }

        borderPane.setCenter(pane);
    }

}
