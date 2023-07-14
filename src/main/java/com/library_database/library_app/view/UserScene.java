package com.library_database.library_app.view;
import com.library_database.library_app.controller.*;
import com.library_database.library_app.model.*;
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
import java.sql.SQLException;
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
    private TextField searchField;
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

    public UserScene() throws SQLException, IOException {
        userController = new UserController();
    }

    // Event handler for the borrow button
    @FXML
    public void handleBorrow() {
        // get selected book, get selected dates
        // GUI retrieves all these values
        Book selectedBook = searchTable.getSelectionModel().getSelectedItem();
        LocalDate startDate = borrowDatePicker.getValue();
        LocalDate endDate = returnDatePicker.getValue();

        if (selectedBook != null && startDate != null && endDate != null) {
            // Convert LocalDate to java.sql.Date
            Date sqlStartDate = java.sql.Date.valueOf(startDate);
            Date sqlEndDate = java.sql.Date.valueOf(endDate);

            // Process the attempted loan using the user controller
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

                // Update the searchTable and loanTable after a successful loan
                populateTableView(0, searchTable, userController, searchTitle);
                populateTableView(1, loanTable, userController, loanTitle);
            }
        }
        else {
            showAlert(UserScenario.LOAN_FAILURE_INCOMPLETE, "You need to select a book and a return period.");
        }


    }

    // Event handler for the Return button
    @FXML
    public void handleReturn() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        boolean returnSuccess = userController.processReturn(selectedLoan.getLoanID());


        if (returnSuccess) {
            populateTableView(0, searchTable, userController, searchTitle);
            populateTableView(1, loanTable, userController, loanTitle);
            showAlert(UserScenario.RETURN_SUCCESS, "You have successfully loaned out " +
                    selectedLoan.getTitle() + ".");
        }
        else {
            showAlert(UserScenario.RETURN_FAILURE, "Something went wrong and " + selectedLoan.getTitle() +
                    " could not be loaned.");

        }
    }

    // Show an alert dialog based on the user scenario and message
    public void showAlert(UserScenario scenario, String userMessage) {
        Alert alert;
        if (scenario == UserScenario.SEARCH_FAILURE){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Search was unsuccessful.");
        }
        else if ((scenario != UserScenario.LOAN_SUCCESS && scenario != UserScenario.RETURN_SUCCESS)){
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
                alert.setHeaderText("Loan was successful.");
            }
        }

        alert.setContentText(userMessage);

        alert.showAndWait();

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table columns
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
        int maxValue = 2024;

        // Initialize the yearSpinner with a custom value factory
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(minValue, maxValue){
            @Override
            public void increment(int steps) {
                Integer value = getValue();
                if (value == null) {
                    setValue(minValue);
                } else {
                    super.increment(steps);
                }
            }

            @Override
            public void decrement(int steps) {
                Integer value = getValue();
                if (value == null) {
                    setValue(maxValue);
                } else {
                    super.decrement(steps);
                }
            }
        };

        yearSpinner.setValueFactory(valueFactory);
        yearSpinner.getValueFactory().setValue(null);

        // Set drop down items
        pageCombo.setItems(FXCollections.observableArrayList(
                "Any", "0-100", "101-200", "201-300", "301-400", "400-500", "More than 500"));
    }

    // Event handler for the search button
    @FXML
    public void handleSearch() {
        // Retrieves search criteria
        String title = searchField.getText();
        String author = searchFieldAuthor.getText();
        String pageLength = pageCombo.getValue();

        Integer year = yearSpinner.getValue();
        boolean availability = availableCheck.isSelected();

        RadioButton selectedButton = (RadioButton) MinMax.getSelectedToggle();
        boolean minOrMax = (selectedButton != minRadio);
        double rating = ratingSlider.getValue();

        // Process the search using the user controller
        boolean successfulSearch = userController.processSearch(title, author, minOrMax, rating,
                pageLength, year, availability);

        if (!successfulSearch){
            showAlert(UserScenario.SEARCH_FAILURE, "Something went wrong and your " +
                    "search couldn't be completed");
        }
        populateTableView(0, searchTable, userController, searchTitle);
    }

    // Set the stage for this scene
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Event handler for the logout button
    public void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Logout?");
        alert.setContentText("Are you sure you want to logout?");


        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform logout operation here
                System.out.println("Logout successful");
                // Load Login Scene
                FXMLLoader newLoader;
                try {
                    newLoader = loadScene(stage, "/com/library_database/library_app/login_scene.fxml",
                            "Login To The Library Database");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LoginScene loginScene = newLoader.getController();
                loginScene.setStage(stage);
            }
        });

    }

    // Initialize the user controller with the account and populate the table views
    public void initializeController(AccountList accList, String username){
        userController.setUserAndPopulate(accList.getAccount(username));
        populateTableView(0, searchTable, userController, searchTitle);
        populateTableView(1, loanTable, userController, loanTitle);
    }


}

