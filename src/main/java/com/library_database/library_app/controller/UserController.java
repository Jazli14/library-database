package com.library_database.library_app.controller;


import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class UserController extends MasterController {
    private User client;
    public UserController(){
        library = new Library(this);
    }

    // Process borrow operation
    public UserScenario processBorrow(int bookID, Date borrowDate, Date returnDate) {
        UserScenario borrowSuccess = client.borrowBook(bookID, library, borrowDate, returnDate);
        if (borrowSuccess == UserScenario.LOAN_SUCCESS){ // Check if the borrow was successful
            // Set the book's availability to false
            updateBookStatus(bookID, false);
        }
        else {
            System.out.println("Loan failed");
        }
        return borrowSuccess;
    }


    // Process the attempted return of a book
    public boolean processReturn(int loanID){
        // Retrieve loan ID
        Loan loan = library.getLoan(loanID);

        if (client.returnBook(loanID, library)){ // If book return was successful
            // Reset book availability to true
            library.getBooks().get(loan.getBookID()).setAvailability(true);
            // Update book availability to true
            updateBookStatus(loan.getBookID(), true);
            // Update loan table by deleting the loan
            updateLoans(loan, 1);
            return true;
        }
        else {
            return false;
        }
    }

    // Process the search through
    public boolean processSearch(String title, String author, boolean minOrMax, double rating, String lengthRange,
                              Integer year, boolean isUnavailable) throws SQLException, IOException {
        int minRange;
        int maxRange;
        if (lengthRange != null) { // Check if the page length range is null
            if (lengthRange.equals("More than 500")) {
                // Set minimum page length to 501
                minRange = 501;
                maxRange = -1;
            }
            else if (lengthRange.equals("Any")){
                // Set range to invalid numbers to allow any page length
                minRange = -1;
                maxRange = -1;
            }
            else {
                // Parse through the inputted length range
                String[] rangeValues = lengthRange.split("-");
                minRange = Integer.parseInt(rangeValues[0]);
                maxRange = Integer.parseInt(rangeValues[1]);

            }
        }
        else {
            minRange = -1;
            maxRange = -1;
        }

        int intYear;
        intYear = Objects.requireNonNullElse(year, -1);


        // Pass given data to search books function
        return searchBooks(title, author, minOrMax, rating, minRange, maxRange, intYear, isUnavailable);
    }

    // Update SQL database book table when it is returned or loaned out
    public void updateBookStatus(int bookID, boolean status) {
        try (Connection connection = establishConnection()) {
            String updateQuery = "UPDATE books SET ready = ? WHERE book_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setBoolean(1, status);
                statement.setInt(2, bookID);
                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize the account of the controller and populate the library
    public void setUserAndPopulate(Account account){
        client = (User) account;
        populateLibrary(client);
    }

}
