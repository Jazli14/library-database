package com.example.librarydatabase.View;
import com.example.librarydatabase.Controller.*;
import com.example.librarydatabase.Model.*;
import javafx.collections.FXCollections;
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

public class UserScene extends Scene implements Initializable {
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

    @FXML
    private TableView<Loan> loanTable;
    @FXML
    private TableColumn<Loan, String> loanTitle;
    @FXML
    private TableColumn<Loan, Date> loanBorrow;
    @FXML
    private TableColumn<Loan, Date> loanReturn;
    @FXML
    private TableColumn<Loan, Boolean> loanOverdue;
    @FXML
    private RadioButton minRadio;
    @FXML
    private RadioButton maxRadio;
    @FXML
    private ToggleGroup MinMax;
    @FXML
    private Slider ratingSlider;
    @FXML
    private ComboBox<String> pageCombo;
    @FXML
    private Spinner<Integer> yearSpinner;
    @FXML
    private CheckBox availableCheck;
    @FXML
    private TextField searchFieldAuthor;
    @FXML
    private Button searchButton;
    @FXML
    private Button loansLogoutButton;
    @FXML
    private Button returnButton;
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
            UserScenario borrowSuccess = userController.processBorrow(selectedBook.getBookID(), sqlStartDate, sqlEndDate);
            if (borrowSuccess == UserScenario.LOAN_FAILURE_DATE){
                showAlert(borrowSuccess, "You need to select a valid return period.");
            }
            else {
                // Format the date as "yyyy-MM-dd"
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String startDateString = sdf.format(sqlStartDate);
                String endDateString = sdf.format(sqlEndDate);

                String successMessage = "Successfully loaned " + selectedBook.getTitle() + " by " +
                        selectedBook.getAuthor() + " for " + startDateString + " to " + endDateString + ".";
                showAlert(UserScenario.LOAN_SUCCESS, successMessage);

                populateTableView(true, searchTable, userController, searchTitle);
                populateTableView(false, loanTable, userController, loanTitle);
            }
        }
        else {
            showAlert(UserScenario.LOAN_FAILURE_INCOMPLETE, "You need to select a book and a return period.");
        }


    }

    @FXML
    public void handleReturn() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        boolean returnSuccess = userController.processReturn(selectedLoan.getLoanID());


        if (returnSuccess) {
            populateTableView(true, searchTable, userController, searchTitle);
            populateTableView(false, loanTable, userController, loanTitle);
            showAlert(UserScenario.RETURN_SUCCESS, "You have successfully loaned out " +
                    selectedLoan.getTitle() + ".");
        }
        else {
            showAlert(UserScenario.RETURN_FAILURE, "Something went wrong and " + selectedLoan.getTitle() +
                    " could not be loaned.");

        }
    }

    public void showAlert(UserScenario scenario, String userMessage) {
        Alert alert;
        if ((scenario != UserScenario.LOAN_SUCCESS && scenario != UserScenario.RETURN_SUCCESS)){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            if (scenario == UserScenario.RETURN_FAILURE){
                alert.setHeaderText("Return was unsuccessful.");
            }
            else {
                alert.setHeaderText("Loan was unsuccessful.");
            }
        }
        else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            if (scenario == UserScenario.RETURN_SUCCESS){
                alert.setHeaderText("Return was successful.");
            }
            else {
                alert.setHeaderText("Loan was unsuccessful.");
            }
        }

        alert.setContentText(userMessage);

        alert.showAndWait();

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        searchAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        searchLength.setCellValueFactory(new PropertyValueFactory<>("num_pages"));
        searchYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        searchAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));

        loanTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        loanBorrow.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        loanReturn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        loanOverdue.setCellValueFactory(new PropertyValueFactory<>("isOverdue"));

        int minValue = 1900;
        int maxValue = 2020;
        int initialValue = 2000;

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(minValue, maxValue, initialValue);

        yearSpinner.setValueFactory(valueFactory);

        pageCombo.setItems(FXCollections.observableArrayList(
                "0-100", "101-200", "201-300", "301-400", "400-500", "More than 500"));
    }

    @FXML
    public void handleSearch() {
        String book = searchField.getText();
        userController.search(book);
        populateTableView(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleLogout() throws IOException {
        // Load Login Scene
        FXMLLoader newLoader = Scene.loadScene(stage, "/com/example/librarydatabase/login_scene.fxml",
                "Login To The Library Database");
        LoginScene loginScene = newLoader.getController();
        loginScene.setStage(stage);
    }


    public void initializeController(AccountList accList, String username){
        userController.setUserAndPopulate(accList.getMember(username));
        populateTableView(true, searchTable, userController, searchTitle);
        populateTableView(false, loanTable, userController, loanTitle);
    }


}

