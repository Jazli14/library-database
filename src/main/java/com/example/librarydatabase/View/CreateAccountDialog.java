package com.example.librarydatabase.View;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountDialog implements Initializable {
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private CheckBox adminInput;
    private Dialog dialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        boolean admin = adminInput.isSelected();

        // Pass the data back to the AdminScene or perform any other required action
        AdminScene.handleAccountData(username, password, admin);

        // Close the dialog
        dialog.close();
    }

    public void handleCancelButton() {
        dialog.close();
    }
}

