package com.example.librarydatabase.View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class EditLoanDialog implements Initializable {
    @FXML
    private DatePicker borrowInput;

    @FXML
    private TextField borrowerInput;

    @FXML
    private CheckBox overdueInput;

    @FXML
    private DatePicker returnInput;

    private Dialog dialog;

    private int loanID;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        String borrower = borrowerInput.getText();
        Date borrowDate;
        if (borrowInput.getValue() != null){
            borrowDate = Date.valueOf(borrowInput.getValue());
        }
        else {
            borrowDate = null;
        }
        Date returnDate;
        if (returnInput.getValue() != null){
            returnDate = Date.valueOf(returnInput.getValue());
        }
        else {
            returnDate = null;
        }
        boolean overdue = overdueInput.isSelected();

        // Pass the data back to the AdminScene or perform any other required action
        AdminScene.handleEditLoanData(loanID, borrower, borrowDate, returnDate, overdue);

        // Close the dialog
        dialog.close();
    }

    public void handleCancelButton(){
        dialog.close();
    }
    public void setLoanID(int loanID) {
        this.loanID = loanID;
    }



}
