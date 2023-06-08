package com.example.librarydatabase.View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class EditBookDialog implements Initializable {
    @FXML
    private TextField authorInput;

    @FXML
    private CheckBox availableInput;

    @FXML
    private TextField pageInput;

    @FXML
    private TextField ratingInput;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField yearInput;

    private Dialog dialog;

    private int bookID;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> yearFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, numericFilter);
        yearInput.setTextFormatter(yearFormatter);
        yearInput.setText("");

        TextFormatter<Integer> pageFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, numericFilter);
        pageInput.setTextFormatter(pageFormatter);
        pageInput.setText("");

        UnaryOperator<TextFormatter.Change> ratingFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Double> ratingFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, ratingFilter);
        ratingInput.setTextFormatter(ratingFormatter);
        ratingInput.setText("");
    }

    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        String title = titleInput.getText();
        String author = authorInput.getText();
        double rating;
        if (ratingInput.getText().isEmpty()){
            rating = -1;
        }
        else {
            rating = Double.parseDouble(ratingInput.getText());
        }
        int length;
        if (pageInput.getText().isEmpty()){
            length = -1;
        }
        else {
            length = Integer.parseInt(pageInput.getText());
        }

        int year;
        if (yearInput.getText().isEmpty()){
            year = -1;

        }
        else {
            year = Integer.parseInt(yearInput.getText());
        }
        boolean available = availableInput.isSelected();

        // Pass the data back to the AdminScene
        AdminScene.handleEditBookData(this.bookID, title, author, rating, length, year, available);

        // Close the dialog
        dialog.close();
    }

    public void handleCancelButton(){
        dialog.close();
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getBookID() {
        return bookID;
    }
}


