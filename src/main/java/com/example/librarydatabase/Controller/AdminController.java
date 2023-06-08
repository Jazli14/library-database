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

    public int processCreateLoan(int bookID, String title, String username, Date borrowDate,
                                     Date returnDate, boolean overdueStatus) {
        Loan newLoan = client.createLoan(library, bookID, title, username, borrowDate, returnDate, overdueStatus);

        if (newLoan != null){
            updateSQLDatabase(newLoan, true);

            return newLoan.getLoanID();
        }
        else {
            return 0;
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
    private boolean editSQLDatabase(int bookID, String title, String author, double rating, int num_pages,
                                    int year, boolean available){
        try (Connection connection = establishConnection()) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("UPDATE available2 SET");
            if (!title.isEmpty()){
                sqlBuilder.append(" title = ?,");
            }

            if (!author.isEmpty()) {
                sqlBuilder.append(" authors = ?,");
            }

            if (rating != -1){
                sqlBuilder.append(" rating = ?,");
            }
            if (num_pages != -1){
                sqlBuilder.append(" num_pages = ?,");
            }
            if (year != -1){
                sqlBuilder.append(" year = ?,");
            }

            sqlBuilder.append(" ready = ? WHERE book_id = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int parameterIndex = 1;

                if (!title.isEmpty()) {
                    statement.setString(parameterIndex++, title);
                }
                if (!author.isEmpty()){
                    statement.setString(parameterIndex++, author);
                }

                if (rating != -1){
                    statement.setDouble(parameterIndex++, rating);

                }
                if (num_pages != -1){
                    statement.setInt(parameterIndex++, num_pages);
                }
                if (year != -1) {
                    statement.setInt(parameterIndex++, year);
                }
                statement.setBoolean(parameterIndex++, available);
                statement.setInt(parameterIndex, bookID);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean editSQLDatabase(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue){
        try (Connection connection = establishConnection()) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("UPDATE loans2 SET");
            if (!borrower.isEmpty()){
                sqlBuilder.append(" borrower = ?,");
            }

            if (borrowDate != null) {
                sqlBuilder.append(" borrow_date = ?,");
            }

            if (returnDate != null){
                sqlBuilder.append(" borrow_date = ?,");
            }

            sqlBuilder.append(" overdue = ? WHERE loan_id = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int parameterIndex = 1;
                if (!borrower.isEmpty()){
                    statement.setString(parameterIndex++, borrower);
                }

                if (borrowDate != null) {
                    statement.setDate(parameterIndex++, borrowDate);
                }

                if (borrowDate != null){
                    statement.setDate(parameterIndex++, returnDate);
                }
                statement.setBoolean(parameterIndex++, overdue);
                statement.setInt(parameterIndex, loanID);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean processEditBook(int bookID, String title, String author, double rating, int num_pages, int year, boolean available){
        if (!title.isEmpty()){
            client.editBookTitle(library, bookID, title);
        }
        if (!author.isEmpty()){
            client.editBookAuthor(library, bookID, author);
        }
        if (rating != -1){
            client.editBookRating(library, bookID, rating);
        }
        if (num_pages != -1){
            client.editBookLength(library, bookID, num_pages);
        }
        if (year != -1){
            client.editBookYear(library, bookID, year);
        }
        client.editBookAvailability(library, bookID, available);
        // update db
        return editSQLDatabase(bookID, title, author, rating, num_pages, year, available);

    }

    public boolean processEditLoan(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue){
        if (!borrower.isEmpty()){
            client.editLoanBorrower(library, loanID, borrower);
        }
        if (borrowDate != null){
            client.editLoanBorrowDate(library, loanID, borrowDate);
        }
        if (returnDate != null){
            client.editLoanReturnDate(library, loanID, returnDate);
        }
        client.editLoanOverdue(library, loanID, overdue);

        // update db

        return editSQLDatabase(loanID, borrower, borrowDate, returnDate, overdue);
    }

    public void setAdminAndPopulate(Member client){
        this.client = (Admin) client;
        populateLibrary(client);

    }


}
