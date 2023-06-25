package com.library_database.library_app.view;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CreateLoanDialog implements Initializable {
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
        // Ensure that the user only types numbers for the book ID
        UnaryOperator<TextFormatter.Change> newFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> digitFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, newFilter);
        bookIDInput.setTextFormatter(digitFormatter);
        bookIDInput.setText("");
    }
    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        String borrower = borrowerInput.getText();
        Date borrowDate;
        int bookID;
        // Check if no book ID input
        if (bookIDInput.getText().isEmpty()){
            bookID = -1;
        }
        else {
            bookID = Integer.parseInt(bookIDInput.getText());
        }
        String title = titleInput.getText();
        // Check if null borrow date
        if (borrowInput.getValue() != null){
            borrowDate = Date.valueOf(borrowInput.getValue());
        }
        else {
            borrowDate = null;
        }
        Date returnDate;
        // Check if null return date
        if (returnInput.getValue() != null){
            returnDate = Date.valueOf(returnInput.getValue());
        }
        else {
            returnDate = null;
        }

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
