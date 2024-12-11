package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HomeViewController {

    @FXML
    private MFXListView<?> activeReservationsList;

    @FXML
    private MFXButton enterButton;

    @FXML
    private ImageView qrImageView;

    @FXML
    private Label timeLabel;
    private Timeline timeline;

    @FXML
    void enter(ActionEvent event) {
        qrImageView.setImage(new Image("sk/upjs/ics/test_image.png"));
        startTimer(); // TODO cannot start more than once
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
