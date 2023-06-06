package com.example.librarydatabase.View;
import com.example.librarydatabase.Controller.*;
import com.example.librarydatabase.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class UserScene implements Initializable {
    @FXML
    private TableView<Book> searchTable;
    @FXML
    private TableColumn<Book, String> searchAuthor;
    @FXML
    private TableColumn<Book, String> searchTitle;
    @FXML
    private TableColumn<Book, Double> searchRating;
    @FXML
    private TableColumn<Book, Integer> searchLength;
    @FXML
    private TableColumn<Book, Integer> searchYear;
    @FXML
    private TableColumn<Book, Boolean> searchAvailability;
    @FXML
    private Button userLogoutButton;
    @FXML
    private Button loanButton;
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private DatePicker borrowDatePicker;

    private final UserController userController;

    private Stage stage;

    public UserScene(){
        userController = new UserController();
    }
    @FXML
    public void handleBorrow() {
        // get selected book, get selected dates boom
        // GUI happens
        Book selectedBook = searchTable.getSelectionModel().getSelectedItem();
        LocalDate startDate = borrowDatePicker.getValue();
        LocalDate endDate = returnDatePicker.getValue();

        if (selectedBook != null && startDate != null && endDate != null) {
            // Convert LocalDate to java.sql.Date
            Date sqlStartDate = java.sql.Date.valueOf(startDate);
            Date sqlEndDate = java.sql.Date.valueOf(endDate);
            boolean borrowSuccess = userController.processBorrow(selectedBook.getBookID(), sqlStartDate, sqlEndDate);
            if (!borrowSuccess){
                System.out.println("Loan unsuccessful");
            }
            else {
                // Format the date as "yyyy-MM-dd"
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String startDateString = sdf.format(sqlStartDate);
                String endDateString = sdf.format(sqlEndDate);

                System.out.println("Successfully loaned " + selectedBook.getTitle() + " by " +
                        selectedBook.getAuthor() + " for " + startDateString + " to " + endDateString);
            }
        }
        else {
            System.out.println("You need to select a book, borrow date and return date");
        }




    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        searchTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        searchAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        searchLength.setCellValueFactory(new PropertyValueFactory<>("num_pages"));
        searchYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        searchAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Populate the TableView with data from the book HashMap
        populateTableView();

    }

    private void populateTableView() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        bookList.addAll(userController.library.getBooks().values());
        searchTable.setItems(bookList);
    }
    @FXML
    public void handleReturn() {

    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleLogout() throws IOException {
        // Load Login Scene
        FXMLLoader newLoader = SceneUtils.loadScene(stage, "/com/example/librarydatabase/login_scene.fxml",
                "Login To The Library Database");
        LoginScene loginScene = newLoader.getController();
        loginScene.setStage(stage);

    }
    public void initializeController(AccountList accList, String username){
        userController.setUser(accList, username);
    }

}

