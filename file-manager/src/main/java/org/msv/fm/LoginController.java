package org.msv.fm;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    private boolean ok = false;
    private Stage dialogStage;


    public boolean isOk() {
        return ok;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    @FXML
    public void login() {
        ok = true;
        dialogStage.close();
    }


    @FXML
    public void cancel() {
        ok = false;
        dialogStage.close();
    }
}
