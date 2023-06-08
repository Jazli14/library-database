package com.example.librarydatabase.View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class BookDialog implements Initializable {
    @FXML
    private TextField authorInput;

    @FXML
    private CheckBox availableInput;

    @FXML
    private TextField idInput;

    @FXML
    private TextField pageInput;

    @FXML
    private TextField ratingInput;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField yearInput;

    private Dialog dialog;
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

        TextFormatter<Integer> idFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, numericFilter);
        idInput.setTextFormatter(idFormatter);

        TextFormatter<Integer> pageFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, numericFilter);
        pageInput.setTextFormatter(pageFormatter);

        UnaryOperator<TextFormatter.Change> ratingFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Double> ratingFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, ratingFilter);
        ratingInput.setTextFormatter(ratingFormatter);
    }

    public void setDialogStage(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void handleOkButton() {
        // Retrieve the entered data
        int bookID = Integer.parseInt(idInput.getText());
        String title = titleInput.getText();
        String author = authorInput.getText();
        double rating = Double.parseDouble(ratingInput.getText());
        int length = Integer.parseInt(pageInput.getText());
        int year = Integer.parseInt(yearInput.getText());
        boolean available = availableInput.isSelected();

        // Pass the data back to the AdminScene or perform any other required action
        AdminScene.handleBookData(bookID, title, author, rating, length, year, available);

        // Close the dialog
        dialog.close();
    }

    public void handleCancelButton(){
        dialog.close();
    }
}
