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
import sk.upjs.ics.daos.interfaces.CreditTransactionDao;
import sk.upjs.ics.daos.interfaces.TransactionTypeDao;
import sk.upjs.ics.daos.interfaces.UserDao;
import sk.upjs.ics.daos.interfaces.VisitDao;
import sk.upjs.ics.entities.CreditTransaction;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.entities.Visit;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;
import sk.upjs.ics.utilities.QRCodeUitl;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.UUID;

public class HomeViewController implements Initializable {

//    @FXML
//    private MFXListView<String> activeReservationsList;

    @FXML
    private MFXButton enterButton;

    @FXML
    private ImageView qrImageView;

    @FXML
    private MFXButton exitButton;

    @FXML
    private MFXButton scanButton;
    private Timeline timeline;
    private Long priceInCents = 0L;
    private static final Long PRICE_PER_MINUTE = 8L; // 8 cents per minute = 4.8e per hour

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
    @FXML
    private Label homeAlertLabel;
    private final VisitDao visitDao = Factory.INSTANCE.getVisitDao();
    private final TransactionTypeDao transactionTypeDao = Factory.INSTANCE.getTransactionTypeDao();
    private final CreditTransactionDao creditTransactionDao = Factory.INSTANCE.getCreditTransactionDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final Principal principal = Auth.INSTANCE.getPrincipal();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitButton.setVisible(false);
        scanButton.setVisible(false);
        qrImageView.setImage(new Image("sk/upjs/ics/dumbbell.png"));
        infoGrid.setVisible(false);
        homeAlertLabel.setVisible(false);
    }
    @FXML
    void enterButtonPressed(ActionEvent event) {

        // check the credit balance
        if (userDao.findById(principal.getId()).getCreditBalance() <= 0) {
            homeAlertLabel.setVisible(true);
            return;
        }

        // vygeneruj visitsecret qr kod
        String uniqueID = UUID.randomUUID().toString();

        System.out.println(uniqueID);

        // vytvor novy zaznam v tabulke a posli ho tam
        Visit visit = new Visit();
        visit.setVisitSecret(uniqueID);
        visit.setUser(userDao.findById(principal.getId()));
        System.out.println("user added to visit:" + userDao.findById(principal.getId()));
        visit.setCheckInTime(new Timestamp(System.currentTimeMillis()).toInstant());
        visitDao.create(visit);

        // zobraz qr kod
        QRCodeUitl.generateQRCode(uniqueID, "src/main/resources/sk/upjs/ics/qrcode.png", 500, 500);
        qrImageView.setImage(new Image("file:src/main/resources/sk/upjs/ics/qrcode.png"));

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

        // skontroluj qr kod - len ako demo samozrejme
        String uid = QRCodeUitl.readQRCode("src/main/resources/sk/upjs/ics/qrcode.png");
        // Visit visit = visitDao.findByVisitSecret(uid);
        // System.out.println("User identified in the database: " + visit.getUser().getEmail());

        // zapni timer a pocitaj estimated price
        timeLabel.setText("00:00:00");
        estimatedPriceLabel.setText("0.0 €");
        priceInCents = 0L;
        startTimer();
    }

    @FXML
    void exitButtonPressed(ActionEvent event) {
        // zobraz dialog sumarizaciu
        if (SceneUtils.showCustomAlert("alert.visitSumUpTitle", "")) {
            return;
        }

        // posli cas vystupu do databazy
        Visit visit = visitDao.findByVisitSecret(QRCodeUitl.readQRCode("src/main/resources/sk/upjs/ics/qrcode.png"));
        System.out.println("found visit" + visit);
        visit.setCheckOutTime(new Timestamp(System.currentTimeMillis()).toInstant());

        // vypocitaj cenu a updatni ju
        long durationInSeconds = visit.getCheckOutTime().getEpochSecond() - visit.getCheckInTime().getEpochSecond();
        Long price = getPriceFromDuration(durationInSeconds);

        // skontroluj ci ma pouzivatel dost penazi
        if (userDao.findById(principal.getId()).getCreditBalance() < price) {
            homeAlertLabel.setVisible(true);
            return;
        }

        // vytvor tranzakciu a pridaj ju do databazy
        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setAmount(price);
        creditTransaction.setUser(visit.getUser());
        creditTransaction.setCreditTransactionType(transactionTypeDao.findById(1L));
        Long newTransactionId = creditTransactionDao.create(creditTransaction);

        // realne odpocitaj kredit
        User user = visit.getUser();
        user.setCreditBalance(user.getCreditBalance() - price);
        userDao.updateBalance(user);

        // navsteve nastav tranzakciu a uloz ju
        visit.setCreditTransaction(creditTransactionDao.findById(newTransactionId));
        visitDao.update(visit);

        // zobraz ilustracny obrazok
        qrImageView.setImage(new Image("sk/upjs/ics/dumbbell.png"));

        // schovaj exit a zobraz enter
        exitButton.setVisible(false);
        enterButton.setVisible(true);
        infoGrid.setVisible(false);
        timeline.stop();
    }

    /*
    * uses ceil to round to nearest minute
    * returns price in cents
     */
    private Long getPriceFromDuration(Long durationInSeconds) {
        return ((long) Math.ceil(durationInSeconds / 60.0)) * PRICE_PER_MINUTE;
    }

    private void startTimer() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        final LocalTime[] startTime = {LocalTime.of(0, 0, 0)};
        final long[] elapsedSeconds = {0};

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(startTime[0].format(formatter));

            // price label
            priceInCents = getPriceFromDuration(elapsedSeconds[0]);
            estimatedPriceLabel.setText((priceInCents / 100.0) + " €"); // (double) Math.round(a * 100) / 100;
            //System.out.println(elapsedSeconds[0]);

            startTime[0] = startTime[0].plusSeconds(1);
            elapsedSeconds[0]++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
