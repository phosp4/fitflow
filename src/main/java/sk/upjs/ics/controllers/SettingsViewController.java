package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsViewController implements Initializable {

    @FXML
    private MFXToggleButton darkModeToggle;

    @FXML
    private MFXComboBox<?> languageComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // this ensures that the dark mode toggle is in sync with the current theme
        // we have to do it using listener, because we have to wait for the scene to be set
        darkModeToggle.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (newScene != null) {

                    // if the dark mode stylesheet is present in the scene, set the toggle to selected
                    String darkModePath = "/sk/upjs/ics/style-dark.css";
                    if (newScene.getStylesheets().contains(getClass().getResource(darkModePath).toExternalForm())) {
                        darkModeToggle.setSelected(true);
                    } else {
                        darkModeToggle.setSelected(false);
                    }
                }
            }
        });
    }

    @FXML
    void changeToDarkMode(MouseEvent event) {
        Scene scene = darkModeToggle.getScene();
        String lightModePath = "/sk/upjs/ics/style-light.css";
        String darkModePath = "/sk/upjs/ics/style-dark.css";

        scene.getStylesheets().clear();
//        scene.getStylesheets().add(getClass().getResource(darkModePath).toExternalForm());

        if (darkModeToggle.isSelected()) {
            scene.getStylesheets().add(getClass().getResource(darkModePath).toExternalForm());
            System.out.println("Dark mode enabled");
        } else {
            //scene.getStylesheets().remove(getClass().getResource(darkModePath).toExternalForm());
            scene.getStylesheets().add(getClass().getResource(lightModePath).toExternalForm());
            System.out.println("Dark mode disabled");
        }
    }
}
