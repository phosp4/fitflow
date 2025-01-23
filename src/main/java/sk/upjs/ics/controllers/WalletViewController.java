package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import sk.upjs.ics.entities.User;
import sk.upjs.ics.models.UserModel;

import java.net.URL;
import java.util.ResourceBundle;

public class WalletViewController implements Initializable {

    @Getter
    private Long creditChoiceInCents = 1000L;

    @FXML
    private MFXTextField addCreditTextField;

    @FXML
    private Label creditChoiceLabel;

    @FXML
    private Label currentCreditLabel;

    @FXML
    private MFXButton minusCreditButton;

    @FXML
    private MFXButton payCreditButton;

    @FXML
    private MFXButton plusCreditButton;

    private UserModel userModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCreditChoiceInCents(creditChoiceInCents);

        // set currentcredit according to the principal
        userModel = new UserModel();

        updateCreditChoiceLabelFromUser();
    }

    /*
    sets the credit variable if it is a positive number and updates the label
     */
    private void setCreditChoiceInCents(Long valInCents) {
        creditChoiceInCents = Math.max(valInCents, 1000);
        double creditChoiceInEuros = creditChoiceInCents / 100.0;
        creditChoiceLabel.setText(creditChoiceInEuros + " €");
    }

    @FXML
    void updateCreditChoiceLabel(KeyEvent event) {
        try {
            // user inputs credit in EUROS, thats why * 100
            setCreditChoiceInCents(Long.parseLong(addCreditTextField.getText()) * 100);
        } catch (NumberFormatException ignored) {
            // ignore input if it contains non numerical values
        }
    }

    void updateCreditChoiceLabelFromUser() {
        User user = userModel.getCurrentUser();
        long priceInCents = user.getCreditBalance();
        currentCreditLabel.setText(String.valueOf(priceInCents / 100.0) + " €");
    }

    @FXML
    void creditChoiceDown(ActionEvent event) {
        setCreditChoiceInCents(getCreditChoiceInCents() - 500);
    }

    @FXML
    void creditChoiceUp(ActionEvent event) {
        setCreditChoiceInCents(getCreditChoiceInCents() + 500);
    }
    @FXML
    void payCredit(ActionEvent event) {
        if (creditChoiceInCents == 0) {
            return;
        }

        if (SceneUtils.showCustomAlert("wallet.payAlertTitle", "wallet.payAlertText")) {
            return;
        }

        // load credit from dbs
        Long newBalance = userModel.getCurrentUser().getCreditBalance() + creditChoiceInCents;

        // update credit
        userModel.updateCurrentUserCreditBalance(creditChoiceInCents);

        // update credit choice
        setCreditChoiceInCents(0L);

        updateCreditChoiceLabelFromUser();
    }
}