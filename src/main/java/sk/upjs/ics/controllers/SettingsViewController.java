package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import sk.upjs.ics.LocaleManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsViewController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private MFXToggleButton darkModeToggle;

    @FXML
    private MFXComboBox<String> languageComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;

        // iterate over the available languages and add them to the combobox
        List<Locale> availableLocales = LocaleManager.getSupportedLocales();

        for (Locale locale : availableLocales) {
            // return the display name of the language in that locale
            languageComboBox.getItems().add(locale.getDisplayLanguage(locale));
        }

        // populate the language combobox when the scene is set
        String currentLanguage = LocaleManager.getLocale().getDisplayLanguage(LocaleManager.getLocale());
        Platform.runLater(() -> languageComboBox.setValue(currentLanguage));

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

        if (darkModeToggle.isSelected()) {
            scene.getStylesheets().add(getClass().getResource(darkModePath).toExternalForm());
            System.out.println("Dark mode enabled");
        } else {
            //scene.getStylesheets().remove(getClass().getResource(darkModePath).toExternalForm());
            scene.getStylesheets().add(getClass().getResource(lightModePath).toExternalForm());
            System.out.println("Dark mode disabled");
        }
    }


    @FXML
    void changeLanguage(ActionEvent event) {

        // get the selected language from the combobox
        String selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();

        if (selectedLanguage == null) return;

        // get the available locales
        List<Locale> availableLocales = LocaleManager.getSupportedLocales();

        // iterate over the available locales and save the selected locale
        for (Locale locale : availableLocales) {

            // if the selected language matches the display language of the locale
            if (selectedLanguage.equals(locale.getDisplayLanguage(locale))) {

                // save the selected locale
                LocaleManager.saveLocale(locale);

                // update the resource bundle and reload the FXML
                this.resources = ResourceBundle.getBundle("sk.upjs.ics.MyResources.MyResources", locale);
                reloadFXML();
                break;
            }
        }
    }

    private void reloadFXML() {
        Stage stage = (Stage) darkModeToggle.getScene().getWindow();
        MainLayoutController controller = null;

        try {
            controller = SceneUtils.loadScene(
                    stage,
                    "../views/MainLayout.fxml",
                    LocaleManager.getLocale(),
                    darkModeToggle.getScene().getStylesheets().toString(),
                    600,
                    800
            );
            controller.setStage(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controller.setStage(stage);
    }
}