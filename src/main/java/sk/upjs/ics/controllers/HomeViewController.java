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
import sk.upjs.ics.Factory;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.entities.Visit;
import com.google.gson.Gson;
import java.util.Base64;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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

    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();

    /*
    * this whole thing is work in progress xD
    * */
    @FXML
    void enter(ActionEvent event) {
        /*
        * posielal by sa asi nejaky takyto json: E({userid,timestamp,visit_secret}}, vytvori sa instancia visit a ulozi sa do databazy
          citacka to desifruje, checkne ci existuje visit s takym visitsecret, skontroluje ci timestamp nie je stara a podla toho ho pusti
          podobne nejako pri odchode, tam by sa mala poriesit aj platba
        * */

        // vygeneruj visitsecret
        String visitSecret = generateVisitSecret();
        Timestamp startTime = new Timestamp(System.currentTimeMillis());

        // vytvorit novu instanciu visit
        // nastavit userid, cas
        Visit visit = new Visit();
        visit.setUser(new User()/*current user*/); // todo zistit usera
        visit.setVisitSecret(visitSecret);
        visit.setCheckInTime(startTime.toLocalDateTime()); // mozno sa da jednoduchsie

        // poslat do databazy
//        visitDao.create(visit); // todo mal by nastavit aj timestamp, alebo stiahnut timestamp z db
//        visit = visitDao.findById(visit.getId());

        // naformatovat json {userid,timestamp,visit_secret}
        // pripadne to skratit s base64
        String payload = generatePayload(visit);

        // zasifrovat ho rsa verejnym klucom - urobit konstantu
        String encryptedPayload = encryptWithRsa(payload); // todo

        // z toho qr kod

        // zobrazit qr kod
        qrImageView.setImage(new Image("sk/upjs/ics/test_image.png"));
        startTimer(); // TODO cannot start more than once
    }

    private String encryptWithRsa(String payload) {
        return null;
    }

    private String generatePayload(Visit visit) {
//        Gson gson = new Gson();
//        String json = gson.toJson(visit);
//        return Base64.getEncoder().encodeToString(json.getBytes());
        return null;
    }

    private String generateVisitSecret() {
        //String visitToken = UUID.randomUUID().toString();
        return null;
    };

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
