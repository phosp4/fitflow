package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.entities.Visit;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {

    @FXML
    private MFXListView<String> activeReservationsList;

    @FXML
    private MFXButton enterButton;

    @FXML
    private ImageView qrImageView;

    @FXML
    private MFXButton exitButton;

//    @FXML
//    private MFXButton regenerateButton;

    @FXML
    private MFXButton scanButton;
    private Timeline timeline;

    @FXML
    private Label estimatedPriceLabel;

    @FXML
    private Label estimatedPriceLabelText;

    @FXML
    private Label timeLabelText;

    @FXML
    private Label timeLabel;

    @FXML
    private GridPane infoGrid;


    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitButton.setVisible(false);
        scanButton.setVisible(false);
        qrImageView.setImage(new Image("sk/upjs/ics/dumbbell.png"));
        infoGrid.setVisible(false);

    }
    @FXML
    void enterButtonPressed(ActionEvent event) {
        // vygeneruj visitsecret qr kod
        // vytvor novy zaznam v tabulke exit a posli ho tam

        // zobraz qr kod

        // schovaj generate a zobraz tlacidlo scan
        enterButton.setVisible(false);
        scanButton.setVisible(true);
    }

    @FXML
    void scanButtonPressed(ActionEvent event) {
        // schovaj scan a zobraz exit
        scanButton.setVisible(false);
        exitButton.setVisible(true);
        infoGrid.setVisible(true);

        // zapni timer a pocitaj estimated price
        timeLabel.setText("00:00:00");
        startTimer();
    }

    @FXML
    void exitButtonPressed(ActionEvent event) {
        // posli cas vystupu do databazy
        // zobraz ilustracny obrazok
        // schovaj exit a zobraz enter

        SceneUtils.showCustomAlert("alert.visitSumUpTitle", "asdf");

        exitButton.setVisible(false);
        enterButton.setVisible(true);
        infoGrid.setVisible(false);
        timeline.stop();
    }

    private void startTimer() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        final LocalTime[] startTime = {LocalTime.of(0, 0, 0)};

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(startTime[0].format(formatter));
            startTime[0] = startTime[0].plusSeconds(1);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
