package com.library_database.library_app.controller;

import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class AdminController extends MasterController {
    private Admin client;

    public AdminController() throws SQLException, IOException {
        super();
        library = new Library(this);
    }

    public void setClient(Admin client) {
        this.client = client;
    }

    // Process the creation of a new book through given information
    public boolean processCreateBook(int bookID, String title, String author, double rating,
                                     int num_pages, int year, boolean availability) {
        Book newBook = client.createBook(library, bookID, title, author, rating, num_pages, year, availability);

        if (newBook != null) { // Check if book creation was successful
            // Add book to SQL database book table
            updateBookDatabase(newBook, true);

            return true;
        } else {
            return false;
        }

    }

    // Process the removal of a book in the book table
    public boolean processRemoveBook(int bookID) {
        Book removedBook = client.removeBook(library, bookID);
        if (removedBook != null) { // Book removal was successful
            // Remove that book from the SQL book table
            updateBookDatabase(removedBook, false);
            return true;
        } else {
            return false;
        }

    }
    // Process the creation of a loan in the loan table
    public int processCreateLoan(int bookID, String title, String username, Date borrowDate,
                                 Date returnDate, boolean overdueStatus) {
        if (!library.containsAccount(username)){ // Check if account username existed
            return 1;
        }
        else if (library.retrieveAccount(username).getIsAdminRole()){ // Check if that account was an admin
            return 2;
        }
        else if (library.containsBook(bookID)) { // Check if the library contains that book ID
            if (!(library.getBook(bookID).getTitle().equals(title))){
                // Check if book title in library matches title given
                return 3;
            }
        }
        else {
            Loan newLoan = client.createLoan(library, bookID, title, username, borrowDate, returnDate, overdueStatus);

            if (newLoan != null) { // Check if loan creation was successful
                // Add loan to SQL database loan table
                updateLoans(newLoan, 0);

                return newLoan.getLoanID();
            }

        }
        return 0;

    }

    // Process the removal of a loan in the loan table
    public boolean processRemoveLoan(int loanID) {
        Loan removedLoan = client.removeLoan(library, loanID);

        if (removedLoan != null) { // Check if loan removal was successful
            // Remove loan from SQL database loan table
            updateLoans(removedLoan, 1);

            return true;
        } else {
            return false;
        }

    }

    // Process the creation of an account in the account table
    public String processCreateAccount(String username, String password, boolean admin) {
        Account newAccount = client.createAccount(library, username, password, admin);

        if (newAccount != null) { // Check if account creation was successful
            // Add a new account to the SQL database account table
            updateAccounts(newAccount, true);

            return newAccount.getUsername();
        } else {
            return null;
        }
    }

    // Process the removal an account in the account table
    public boolean processRemoveAccount(String username) {
        Account removedAccount = client.removeAccount(library, username);

        if (removedAccount != null) { // Check if account removal was successful
            // Remove account from SQL database account table
            updateAccounts(removedAccount, false);

            return true;
        } else {
            return false;
        }

    }

    // Update SQL database book table by creating or deleting a book through DatabaseManager
    private void updateBookDatabase(Book book, boolean createOrDelete){
        DatabaseManager.adminUpdateBookDatabase(book, createOrDelete);
    }

    // Update the SQL database account table through DatabaseManager
    private void updateAccounts(Account account, boolean createOrDelete){
        DatabaseManager.adminUpdateAccounts(account, createOrDelete);
    }

    // Overloaded helper function that edits SQL database book table with given data through DatabaseManager
    private boolean editSQLDatabase(int bookID, String title, String author, double rating, int num_pages,
                                    int year, boolean available) {
            return DatabaseManager.adminUpdateDB(bookID, title, author, rating, num_pages, year, available);
    }

    // Overloaded helper function that edits SQL database loan table with given data through DatabaseManager
    private boolean editSQLDatabase(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        return DatabaseManager.adminUpdateDB(loanID, borrower, borrowDate, returnDate, overdue);
    }

    // Process the edit of a book and checking for valid input
    public boolean processEditBook(int bookID, String title, String author, double rating, int num_pages, int year,
                                   boolean available) {
        if (!title.isEmpty()) { // Check if the title isn't empty
            // Edit book title
            client.editBookTitle(library, bookID, title);
        }
        if (!author.isEmpty()) { // Check if author isn't empty
            // Edit book author
            client.editBookAuthor(library, bookID, author);
        }
        if (rating != -1) { // Check if rating isn't empty
            // Edit book rating
            client.editBookRating(library, bookID, rating);
        }
        if (num_pages != -1) { // Check if page length isn't empty
            // Edit page length
            client.editBookLength(library, bookID, num_pages);
        }
        if (year != -1) { // Check if year isn't empty
            // Edit year
            client.editBookYear(library, bookID, year);
        }
        // Edit book availability
        client.editBookAvailability(library, bookID, available);

        // update database by passing to helper function
        return editSQLDatabase(bookID, title, author, rating, num_pages, year, available);
    }

    // Process a loan edit and checking for valid input
    public boolean processEditLoan(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        if (!borrower.isEmpty()) { // If borrower field isn't empty
            // Edit the borrower of the loan
            client.editLoanBorrower(library, loanID, borrower);
        }
        if (borrowDate != null) { // If borrow date isn't empty
            // Edit the loan borrow date
            client.editLoanBorrowDate(library, loanID, borrowDate);
        }
        if (returnDate != null) { // If return date isn't empty
            // Edit the loan return date
            client.editLoanReturnDate(library, loanID, returnDate);
        }
        // Edit the loan overdue status
        client.editLoanOverdue(library, loanID, overdue);

        // Update db by passing to helper function with new data of existing loan
        return editSQLDatabase(loanID, borrower, borrowDate, returnDate, overdue);
    }

    // Initialize the admin client accessing the system and populate the library
    public void setAdminAndPopulate(Account client, AccountList accList) {
        this.client = (Admin) client;
        this.library.setAccounts(accList);
        populateLibrary(client);

    }

    // Parse input properly to give to DatabaseManager to search for books in the database
    public boolean processSearchBooks(String title, String author, boolean minOrMax, double rating, String pageLength,
                                      Integer year, boolean availability) {
        int maxRange;
        int minRange;
        if (pageLength != null) {
            if (pageLength.equals("More than 500")) { // Check if page length is more than 500
                // Set minimum page length to 501
                maxRange = -1;
                minRange = 501;
            } else if (pageLength.equals("Any")) {
                // Set invalid page lengths to allow any page to be searched for
                maxRange = -1;
                minRange = -1;
            } else {
                String[] rangeValues = pageLength.split("-");
                minRange = Integer.parseInt(rangeValues[0]);
                maxRange = Integer.parseInt(rangeValues[1]);

            }
        } else { // If input is null also allow any page to be searched for by assigning an invalid range
            minRange = -1;
            maxRange = -1;
        }

        int intYear;
        intYear = Objects.requireNonNullElse(year, -1);
        // Pass data to helper search books function
        return searchBooks(title, author, minOrMax, rating, minRange, maxRange, intYear, availability);
    }

    // Process the loan search through DatabaseManager
    public boolean processSearchLoans(String bookTitle, String borrower, Date sqlBorrowDate, Date sqlReturnDate,
                                      boolean overdue) {
        return DatabaseManager.adminProcessSearchLoans(bookTitle, borrower, sqlBorrowDate, sqlReturnDate, overdue, library);
    }

    // Call DatabaseManager to search for accounts in the database
    public boolean processSearchAccounts(String username, boolean admin, boolean reset){
        return DatabaseManager.adminProcessSearchAccounts(username, admin, reset, library);
    }



}
