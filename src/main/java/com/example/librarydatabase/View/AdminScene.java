package com.example.librarydatabase.View;

import com.example.librarydatabase.Controller.AdminController;
import com.example.librarydatabase.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminScene extends Scene implements Initializable {
    @FXML
    private Button adminLogoutButton;
    @FXML
    private TableView<Book> adminSearchTable;
    @FXML
    private TableColumn<Book, Integer> adminBookID;
    @FXML
    private TableView<Loan> adminLoanTable;
    @FXML
    private TableColumn<Book, Integer> adminTableLoanID;
    @FXML
    private TableColumn<Loan, Integer> adminTableBookID;


    private Stage stage;
    private final AdminController adminController;

    public AdminScene(){
        adminController = new AdminController();
    }
    @FXML
    public void setStage(Stage stage){
        this.stage = stage;
    }


    @FXML
    private void handleLogout() throws IOException {
        // Load Login Scene
        FXMLLoader newLoader = Scene.loadScene(stage, "/com/example/librarydatabase/login_scene.fxml",
                "Login To The Library Database");

        LoginScene loginScene = newLoader.getController();
        loginScene.setStage(stage);

    }

    public void initializeAdminController(AccountList accList, String username){
        adminController.setAdminAndPopulate(accList.getMember(username));
        populateTableView(true, adminSearchTable, adminController, adminBookID);
        populateTableView(false, adminLoanTable, adminController, adminTableLoanID);
    }

//    public void handleCreateBook(){
//
//        Book selectedBook = adminTable.getSelectionModel().getSelectedItem();
//        LocalDate startDate = borrowDatePicker.getValue();
//        LocalDate endDate = returnDatePicker.getValue();
//
//        if (selectedBook != null && startDate != null && endDate != null) {
//            // Convert LocalDate to java.sql.Date
//            Date sqlStartDate = java.sql.Date.valueOf(startDate);
//            Date sqlEndDate = java.sql.Date.valueOf(endDate);
//            boolean borrowSuccess = userController.processBorrow(selectedBook.getBookID(), sqlStartDate, sqlEndDate);
//            if (!borrowSuccess){
//                System.out.println("Loan unsuccessful");
//            }
//            else {
//                // Format the date as "yyyy-MM-dd"
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String startDateString = sdf.format(sqlStartDate);
//                String endDateString = sdf.format(sqlEndDate);
//
//                System.out.println("Successfully loaned " + selectedBook.getTitle() + " by " +
//                        selectedBook.getAuthor() + " for " + startDateString + " to " + endDateString);
//
//                populateTableView(true);
//                populateTableView(false);
//            }
//        }
//        else {
//            System.out.println("You need to select a book, borrow date and return date");
//        }
//
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        return;
    }

    // Other methods and code...
}

