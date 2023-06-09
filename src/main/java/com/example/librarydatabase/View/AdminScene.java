package com.example.librarydatabase.View;

import com.example.librarydatabase.Controller.AdminController;
import com.example.librarydatabase.Controller.AdminScenario;
import com.example.librarydatabase.Model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminScene extends Scene implements Initializable {
    public Button editLoan;
    @FXML
    private TableView<Book> adminSearchTable;
    @FXML
    private TableColumn<Book, Integer> adminBookID;
    @FXML
    private TableView<Loan> adminLoanTable;
    @FXML
    private TableColumn<Book, Integer> adminTableLoanID;
    @FXML
    private TableColumn<Book, String> adminTitle;
    @FXML
    private TableColumn<Book, String> adminAuthor;
    @FXML
    private TableColumn<Book, Integer> adminRating;
    @FXML
    private TableColumn<Book, Integer> adminLength;
    @FXML
    private TableColumn<Book, Integer> adminYear;
    @FXML
    private TableColumn<Book, Boolean> adminAvailability;
    @FXML
    private TableColumn<Loan, Integer> adminTableBookID;
    @FXML
    private TableColumn<Loan, String> adminLoanTitle;
    @FXML
    private TableColumn<Loan, String> adminLoanUsername;
    @FXML
    private TableColumn<Loan, Date> adminLoanBorrow;
    @FXML
    private TableColumn<Loan, Date> adminLoanReturn;
    @FXML
    private TableColumn<Loan, Boolean> adminLoanOverdue;
    @FXML
    private Button adminLogoutButton;
    @FXML
    private Button loansLogoutButton;
    @FXML
    private Button createBook;
    @FXML
    private Button removeBook;
    @FXML
    private Button createLoan;
    @FXML
    private Button removeLoan;

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
    private Button editBook;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchLoanTitle;
    @FXML
    private TextField searchFieldBorrow;
    @FXML
    private DatePicker borrowDateSearch;
    @FXML
    private DatePicker returnDateSearch;
    @FXML
    private CheckBox overdueCheck;
    @FXML
    private Button searchLoanButton;

    private Stage stage;
    private static final AdminController adminController = new AdminController();

    public AdminScene(){}

    public static void handleBookData(int bookID, String title, String author, double rating,
                                      int length, int year, boolean available) {
        boolean createBookSuccess;
        if (bookID == -1 || title.isEmpty() || author.isEmpty() || rating == -1 || length == -1 || year == -1){
            createBookSuccess = false;
        }
        else {
            createBookSuccess = adminController.processCreateBook(bookID, title, author,
                    rating, length, year, available);
        }

        if (createBookSuccess){
            showAlert(AdminScenario.BOOK_CREATION_SUCCESS, "Successfully created book #" + bookID + ".");
        }
        else {
            showAlert(AdminScenario.BOOK_CREATION_FAILURE, "The book could not created.");
        }

    }

    public static void handleLoanData(int bookID, String title, String borrower,
                                      Date borrowDate, Date returnDate, boolean overdue) {
        int potentialID;
        if (bookID == -1 || title.isEmpty() || borrower.isEmpty() || borrowDate == null || returnDate == null) {
            potentialID = 0;
        }

        else {
            potentialID = adminController.processCreateLoan(bookID, title, borrower,
                    borrowDate, returnDate, overdue);
        }

        if (potentialID != 0){
            String returnedID = Integer.toString(potentialID);

            showAlert(AdminScenario.LOAN_CREATION_SUCCESS, "Successfully created loan #" +
                     returnedID +  ".");
        }
        else {
            showAlert(AdminScenario.LOAN_CREATION_FAILURE, "Loan creation failed.");
        }


    }

    public static void handleEditBookData(int bookID, String title, String author,
                                          double rating, int length, int year, boolean available) {
        boolean editBookSuccess = adminController.processEditBook(bookID, title, author, rating, length, year, available);

        if (editBookSuccess){
            showAlert(AdminScenario.BOOK_EDIT_SUCCESS, "Successfully edited book #" + bookID + ".");
        }
        else {
            showAlert(AdminScenario.BOOK_EDIT_FAILURE, "Book #" + bookID + " could not edited.");
        }
    }

    public static void handleEditLoanData(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        boolean editLoanSuccess = adminController.processEditLoan(loanID, borrower, borrowDate, returnDate, overdue);

        if (editLoanSuccess){
            showAlert(AdminScenario.LOAN_EDIT_SUCCESS, "Successfully edited loan #" + loanID + ".");
        }
        else {
            showAlert(AdminScenario.LOAN_EDIT_FAILURE, "Book #" + loanID + " could not edited.");
        }
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
    @FXML
    public void handleRemoveBook(){
        Book book = adminSearchTable.getSelectionModel().getSelectedItem();

        boolean removeBookSuccess = adminController.processRemoveBook(book.getBookID());

        if (removeBookSuccess){
            populateTableView(true, adminSearchTable, adminController, adminBookID);
            showAlert(AdminScenario.BOOK_REMOVAL_SUCCESS, "Successfully removed " + book.getTitle() + ".");
        }
        else {
            System.out.println("Could not remove " + book.getTitle());
            showAlert(AdminScenario.BOOK_REMOVAL_FAILURE, "Successfully removed " + book.getTitle() + ".");
        }

    }

    @FXML
    public void handleSearchBooks() throws SQLException, IOException {
        String author = searchFieldAuthor.getText();
        String pageLength = pageCombo.getValue();
        String title = searchField.getText();

        Integer year = yearSpinner.getValue();
        boolean availability = availableCheck.isSelected();

        RadioButton selectedButton = (RadioButton) MinMax.getSelectedToggle();
        boolean minOrMax = (selectedButton == minRadio);
        double rating = ratingSlider.getValue();

        boolean successfulSearch = adminController.processSearchBooks(title, author, minOrMax, rating,
                pageLength, year, availability);

        if (!successfulSearch){
            showAlert(AdminScenario.SEARCH_FAILURE, "Something went wrong and your " +
                    "search couldn't be completed");
        }
        populateTableView(true, adminSearchTable, adminController, adminTitle);

    }

    @FXML
    public void handleSearchLoans() throws SQLException, IOException {
        String borrower = searchFieldBorrow.getText();
        String title = searchLoanTitle.getText();

        boolean overdue = overdueCheck.isSelected();

        Date sqlBorrowDate;
        Date sqlReturnDate;
        if (borrowDateSearch.getValue() != null){
            LocalDate startDate = borrowDateSearch.getValue();
            sqlBorrowDate = java.sql.Date.valueOf(startDate);
        }
        else {
            sqlBorrowDate = null;
        }
        if (returnDateSearch.getValue() != null){
            LocalDate endDate = returnDateSearch.getValue();
            sqlReturnDate = java.sql.Date.valueOf(endDate);
        }
        else{
            sqlReturnDate = null;
        }

        boolean successfulSearch = adminController.processSearchLoans(title, borrower, sqlBorrowDate, sqlReturnDate,
                overdue);

        if (!successfulSearch){
            showAlert(AdminScenario.SEARCH_FAILURE, "Something went wrong and your " +
                    "search couldn't be completed");
        }
        populateTableView(false, adminLoanTable, adminController, adminTableLoanID);

    }

    @FXML
    public void handleRemoveLoan(){
        Loan loan = adminLoanTable.getSelectionModel().getSelectedItem();

        boolean removeBookSuccess = adminController.processRemoveLoan(loan.getLoanID());

        if (removeBookSuccess){
            populateTableView(false, adminLoanTable, adminController, adminTableLoanID);
            showAlert(AdminScenario.LOAN_REMOVAL_SUCCESS, "Successfully removed #" + loan.getLoanID() + ".");
        }
        else {
            showAlert(AdminScenario.LOAN_REMOVAL_FAILURE, "Could not remove #" + loan.getLoanID() + ".");
        }

    }

    @FXML
    public void handleCreateBook() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Load the dialog's content from an FXML file

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/create_book_dialog.fxml"));
        Parent dialogPane = loader.load();

        BookDialog dialogController = loader.getController();
        dialogController.setDialogStage(dialog);

        dialog.setDialogPane((DialogPane) dialogPane);

        // Set the dialog's properties
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        // Clear existing button types before adding new ones
        dialog.getDialogPane().getButtonTypes().clear();

        // Add the desired button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                // handle ok button
                dialogController.handleOkButton();
            } else if (result.get() == ButtonType.CANCEL) {
                // handle cancel button
                dialogController.handleCancelButton();
            }
        }

        populateTableView(true, adminSearchTable, adminController, adminBookID);
    }

    public static void showAlert(AdminScenario scenario, String userMessage) {
        Alert alert;
        if ((scenario != AdminScenario.LOAN_CREATION_SUCCESS && scenario != AdminScenario.BOOK_CREATION_SUCCESS
                && scenario != AdminScenario.LOAN_REMOVAL_SUCCESS && scenario != AdminScenario.BOOK_REMOVAL_SUCCESS)
                && scenario != AdminScenario.BOOK_EDIT_SUCCESS && scenario != AdminScenario.LOAN_EDIT_SUCCESS){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            switch (scenario) {
                case BOOK_CREATION_FAILURE -> alert.setHeaderText("Book creation was unsuccessful.");
                case LOAN_CREATION_FAILURE -> alert.setHeaderText("Loan creation was unsuccessful.");
                case BOOK_REMOVAL_FAILURE -> alert.setHeaderText("Book removal was unsuccessful.");
                case LOAN_REMOVAL_FAILURE -> alert.setHeaderText("Loan removal was unsuccessful.");
                case BOOK_EDIT_FAILURE -> alert.setHeaderText("Book edit was unsuccessful");
                case LOAN_EDIT_FAILURE -> alert.setHeaderText("Loan edit was unsuccessful");
            }
        }
        else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            switch (scenario) {
                case BOOK_CREATION_SUCCESS -> alert.setHeaderText("Book creation was successful.");
                case BOOK_REMOVAL_SUCCESS -> alert.setHeaderText("Book removal was successful.");
                case LOAN_CREATION_SUCCESS -> alert.setHeaderText("Loan creation was successful.");
                case LOAN_REMOVAL_SUCCESS -> alert.setHeaderText("Loan removal was successful.");
                case BOOK_EDIT_SUCCESS -> alert.setHeaderText("Book edit was successful.");
                case LOAN_EDIT_SUCCESS -> alert.setHeaderText("Loan edit was successful.");
            }
        }

        alert.setContentText(userMessage);

        alert.showAndWait();

    }

    @FXML
    public void handleEditBook() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        Book book = adminSearchTable.getSelectionModel().getSelectedItem();

        // Load the dialog's content from an FXML file

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/edit_book_dialog.fxml"));
        Parent dialogPane = loader.load();

        EditBookDialog dialogController = loader.getController();
        dialogController.setDialogStage(dialog);
        dialogController.setBookID(book.getBookID());

        dialog.setDialogPane((DialogPane) dialogPane);

        // Set the dialog's properties
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);

        // Clear existing button types before adding new ones
        dialog.getDialogPane().getButtonTypes().clear();

        // Add the desired button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                // handle ok button
                dialogController.handleOkButton();
            } else if (result.get() == ButtonType.CANCEL) {
                // handle cancel button
                dialogController.handleCancelButton();
            }
        }

        populateTableView(true, adminSearchTable, adminController, adminBookID);
    }


    @FXML
    public void handleCreateLoan() throws IOException {
        Dialog<ButtonType> loanDialog = new Dialog<>();

        // Load the dialog's content from an FXML file

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/create_loan_dialog.fxml"));
        Parent loanDialogPane = loader.load();

        LoanDialog loanDialogController = loader.getController();

        loanDialogController.setDialogStage(loanDialog);
        loanDialog.setDialogPane((DialogPane) loanDialogPane);


        // Set the dialog's properties
        loanDialog.initOwner(stage);
        loanDialog.initModality(Modality.APPLICATION_MODAL);

        // Clear existing button types before adding new ones
        loanDialog.getDialogPane().getButtonTypes().clear();

        // Add the desired button types
        loanDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = loanDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.CANCEL) {
                // handle cancel button
                loanDialogController.handleCancelButton();
            } else if (result.get() == ButtonType.OK) {
                // handle ok button
                loanDialogController.handleOkButton();
            }

            populateTableView(false, adminLoanTable, adminController, adminTableLoanID);

        }
    }

    @FXML
    public void handleEditLoan() throws IOException {
        Dialog<ButtonType> loanDialog = new Dialog<>();
        Loan loan = adminLoanTable.getSelectionModel().getSelectedItem();

        // Load the dialog's content from an FXML file

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/edit_loan_dialog.fxml"));
        Parent loanDialogPane = loader.load();

        EditLoanDialog loanDialogController = loader.getController();

        loanDialogController.setDialogStage(loanDialog);
        loanDialog.setDialogPane((DialogPane) loanDialogPane);
        loanDialogController.setLoanID(loan.getLoanID());

        // Set the dialog's properties
        loanDialog.initOwner(stage);
        loanDialog.initModality(Modality.APPLICATION_MODAL);

        // Clear existing button types before adding new ones
        loanDialog.getDialogPane().getButtonTypes().clear();

        // Add the desired button types
        loanDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = loanDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.CANCEL) {
                // handle cancel button
                loanDialogController.handleCancelButton();
            } else if (result.get() == ButtonType.OK) {
                // handle ok button
                loanDialogController.handleOkButton();
            }

            populateTableView(false, adminLoanTable, adminController, adminTableLoanID);

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        adminBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        adminAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        adminRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        adminAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        adminLength.setCellValueFactory(new PropertyValueFactory<>("num_pages"));
        adminYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        adminTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

        adminTableLoanID.setCellValueFactory(new PropertyValueFactory<>("loanID"));
        adminTableBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        adminLoanTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        adminLoanUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        adminLoanReturn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        adminLoanOverdue.setCellValueFactory(new PropertyValueFactory<>("isOverdue"));
        adminLoanBorrow.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        int maxValue = 2024;
        int minValue = 1900;

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(minValue, maxValue) {
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

        pageCombo.setItems(FXCollections.observableArrayList(
                "Any", "0-100", "101-200", "201-300", "301-400", "400-500", "More than 500"));


    }
}

