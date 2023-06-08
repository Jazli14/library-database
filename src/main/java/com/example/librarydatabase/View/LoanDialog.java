package com.example.librarydatabase.View;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class LoanDialog implements Initializable {
    @FXML
    private TextField bookIDInput;

    @FXML
    private DatePicker borrowInput;

    @FXML
    private TextField borrowerInput;

    @FXML
    private CheckBox overdueInput;

    @FXML
    private DatePicker returnInput;

    @FXML
    private TextField titleInput;

    private Dialog dialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnaryOperator<TextFormatter.Change> newFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> yearFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, newFilter);
        bookIDInput.setTextFormatter(yearFormatter);
    }
    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        String borrower = borrowerInput.getText();
        int bookID = Integer.parseInt(bookIDInput.getText());
        String title = titleInput.getText();
        Date borrowDate = Date.valueOf(borrowInput.getValue());
        Date returnDate = Date.valueOf(returnInput.getValue());
        boolean overdue = overdueInput.isSelected();

        // Pass the data back to the AdminScene or perform any other required action
        AdminScene.handleLoanData(bookID, title, borrower, borrowDate, returnDate, overdue);

        // Close the dialog
        dialog.close();
    }

    public void handleCancelButton(){
        dialog.close();
    }



}
