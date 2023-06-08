package com.example.librarydatabase.Controller;

import com.example.librarydatabase.Model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminController extends Controller {
    private Admin client;
    public AdminController(){
        library = new Library(this);
    }

    public void setClient(Admin client) {
        this.client = client;
    }

    public boolean processCreateBook(int bookID, String title, String author, double rating,
                                     int num_pages, int year, boolean availability) {
        Book newBook = client.createBook(library, bookID, title, author, rating, num_pages, year, availability);

        if (newBook != null){
            updateSQLDatabase(newBook, true);

            return true;
        }
        else {
            System.out.println("Loan failed");
            return false;
        }

    }


    public boolean processRemoveBook(int bookID){
        Book removedBook = client.removeBook(library, bookID);
        if (removedBook != null){
            updateSQLDatabase(removedBook, false);
            return true;
        }
        else {
            return false;
        }

    }

    public boolean processCreateLoan(int bookID, String title, String username, Date borrowDate,
                                     Date returnDate, boolean overdueStatus) {
        Loan newLoan = client.createLoan(library, bookID, title, username, borrowDate, returnDate, overdueStatus);

        if (newLoan != null){
            updateSQLDatabase(newLoan, true);
            return true;
        }
        else {
            return false;
        }

    }
    public boolean processRemoveLoan(int loanID) {
        Loan removedLoan = client.removeLoan(library, loanID);

        if (removedLoan != null){
            updateSQLDatabase(removedLoan, false);

            return true;
        }
        else {
            System.out.println("Loan failed");
            return false;
        }

    }

    private void updateSQLDatabase(Book newBook, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
            if (createOrDelete) {
                String insertBookQuery = "INSERT INTO available2 (book_id, title, authors, rating, num_pages, year, " +
                        "ready) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertBookQuery)) {
                    statement.setInt(1, newBook.getBookID());
                    statement.setString(2, newBook.getTitle());
                    statement.setString(3, newBook.getAuthor());
                    statement.setDouble(4, newBook.getRating());
                    statement.setInt(5, newBook.getNum_pages());
                    statement.setInt(6, newBook.getYear());
                    statement.setBoolean(7, newBook.getAvailability());
                    statement.executeUpdate();

                }
            } else {
                String deleteBookQuery = "DELETE FROM available2 WHERE book_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteBookQuery)) {
                    statement.setInt(1, newBook.getBookID()); // Set the loan_id value

                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }
    private void updateSQLDatabase(Loan newLoan, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
            if (createOrDelete) {
                String insertLoanQuery = "INSERT INTO loans2 (loan_id, book_id, title, borrower, borrow_date, return_date, " +
                        "overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertLoanQuery)) {
                    statement.setInt(2, newLoan.getBookID());
                    statement.setString(3, newLoan.getTitle());
                    statement.setInt(1, newLoan.getLoanID());
                    statement.setString(4, newLoan.getUsername());
                    statement.setDate(5, newLoan.getBorrowDate());
                    statement.setDate(6, newLoan.getReturnDate());
                    statement.setBoolean(7, newLoan.getIsOverdue());
                    statement.executeUpdate();

                }
            } else {
                String deleteLoanQuery = "DELETE FROM loans2 WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteLoanQuery)) {
                    statement.setInt(1, newLoan.getLoanID()); // Set the loan_id value

                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    public void editBook(String field, int bookID){
        switch (field) {
            case "title":
                // Process title property
                System.out.println("Processing title...");
                break;
            case "author":
                // Process author property
                System.out.println("Processing author...");
                break;
            case "rating":
                // Process rating property
                System.out.println("Processing rating...");
                break;
            case "num_pages":
                // Process num_pages property
                System.out.println("Processing num_pages...");
                break;
            case "year":
                // Process year property
                System.out.println("Processing year...");
                break;
            case "availability":
                // Process availability property
                System.out.println("Processing availability...");
                break;
            default:
                // Handle unrecognized property
                System.out.println("Unrecognized property: " + field);
                break;

        }

        return;
    }

    public void editLoan(String field, int loanID){
        return;
    }

    public void setAdminAndPopulate(Member client){
        this.client = (Admin) client;
        populateLibrary(client);

    }


}
