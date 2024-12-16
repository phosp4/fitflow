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
    private int creditChoice = 10;

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
        setCreditChoice(creditChoice);

        // set currentcredit according to the principal
        userModel = new UserModel();

        updateCreditChoiceLabelFromUser();
    }

    /*
    sets the credit variable if it is a positive number and updates the label
     */
    private void setCreditChoice(int val) {
        creditChoice = Math.max(val, 10);
        creditChoiceLabel.setText(creditChoice + " €");
    }

    @FXML
    void updateCreditChoiceLabel(KeyEvent event) {
        try {
            setCreditChoice(Integer.parseInt(addCreditTextField.getText()));
        } catch (NumberFormatException ignored) {
            // ignore input if it contains non numerical values
        }
    }

    void updateCreditChoiceLabelFromUser() {
        User user = userModel.getCurrentUser();
        currentCreditLabel.setText(String.valueOf(user.getCreditBalance()) + " €");
    }

    @FXML
    void creditChoiceDown(ActionEvent event) {
        setCreditChoice(getCreditChoice() - 5);
    }

    @FXML
    void creditChoiceUp(ActionEvent event) {
        setCreditChoice(getCreditChoice() + 5);
    }
    @FXML
    void payCredit(ActionEvent event) {
        if (creditChoice == 0) {
            return;
        }

        if (SceneUtils.showCustomAlert("wallet.payAlertTitle", "wallet.payAlertText")) {
            return;
        }

        // load credit from dbs
        Long newBalance = Long.valueOf(userModel.getCurrentUser().getCreditBalance() + creditChoice);

        // update credit
        userModel.updateCurrentUserCreditBalance(creditChoice);

        // update credit choice
        setCreditChoice(0);

        updateCreditChoiceLabelFromUser();
    }
}