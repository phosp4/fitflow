package sk.upjs.ics.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import sk.upjs.ics.security.Auth;
import sk.upjs.ics.security.Principal;

import java.net.URL;
import java.util.ResourceBundle;

public class WalletViewController implements Initializable {

    @Getter
    private int creditChoice = 0;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCreditChoice(creditChoice);
        Principal p = Auth.INSTANCE.getPrincipal();
    }

    /*
    sets the credit variable if it is a positive number and updates the label
     */
    private void setCreditChoice(int val) {
        creditChoice = Math.max(val, 0);
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

    @FXML
    void creditChoiceDown(ActionEvent event) {
        setCreditChoice(getCreditChoice() - 1);
    }

    @FXML
    void creditChoiceUp(ActionEvent event) {
        setCreditChoice(getCreditChoice() + 1);
    }
    @FXML
    void payCredit(ActionEvent event) {

    }
}