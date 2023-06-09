package com.example.librarydatabase.Controller;


import com.example.librarydatabase.Model.*;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class UserController extends MasterController {
    private User client;
    public UserController(){
        library = new Library(this);
    }
    public UserScenario processBorrow(int bookID, Date borrowDate, Date returnDate) {
        UserScenario borrowSuccess = client.borrowBook(bookID, library, borrowDate, returnDate);
        if (borrowSuccess == UserScenario.LOAN_SUCCESS){
            updateBookStatus(bookID, false);

        }
        else {
            System.out.println("Loan failed");
        }
        return borrowSuccess;
    }

    public boolean processReturn(int loanID){
        Loan loan = library.getLoan(loanID);

        if (client.returnBook(loanID, library)){
            library.getBooks().get(loan.getBookID()).setAvailability(true);
            updateBookStatus(loan.getBookID(), true);
            updateLoans(loan, false);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean processSearch(String title, String author, boolean minOrMax, double rating, String lengthRange,
                              Integer year, boolean ready) throws SQLException, IOException {
        int minRange;
        int maxRange;
        if (lengthRange != null) {
            if (lengthRange.equals("More than 500")) {
                minRange = 501;
                maxRange = -1;
            }
            else if (lengthRange.equals("Any")){
                minRange = -1;
                maxRange = -1;
            }
            else {
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
        return searchBooks(title, author, minOrMax, rating, minRange, maxRange, intYear, ready);
    }

    public void updateBookStatus(int bookID, boolean status) {

        try (Connection connection = establishConnection()) {
            String updateQuery = "UPDATE available2 SET ready = ? WHERE book_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setBoolean(1, status);
                statement.setInt(2, bookID);
                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLoans(Loan loan, boolean status) {
        try (Connection connection = establishConnection()) {
            if (status) {
                String insertLoanQuery = "INSERT INTO loans2 (loan_id, book_id, title, borrower, borrow_date, return_date, " +
                        "overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertLoanQuery)) {
                    statement.setInt(1, loan.getLoanID());
                    statement.setInt(2, loan.getBookID());
                    statement.setString(3, loan.getTitle());
                    statement.setString(4, loan.getUsername());
                    statement.setDate(5, loan.getBorrowDate());
                    statement.setDate(6, loan.getReturnDate());
                    statement.setBoolean(7, false);
                    statement.executeUpdate();

                }
            } else {
                String deleteLoanQuery = "DELETE FROM loans2 WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteLoanQuery)) {
                    statement.setInt(1, loan.getLoanID()); // Set the loan_id value

                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserAndPopulate(Member member){
        client = (User) member;
        populateLibrary(client);
    }

}
