package com.example.librarydatabase.Controller;


import com.example.librarydatabase.Model.*;

import java.io.IOException;
import java.sql.*;

public class UserController extends Controller {
    private User client;
    public Library library;

    public UserController(){
        library = new Library();
        populateLibrary();
    }
    public boolean processBorrow(int bookID, Date borrowDate, Date returnDate) {
        boolean borrowSuccess = client.borrowBook(bookID, library, borrowDate, returnDate);

        if (borrowSuccess){
            updateBookStatus(bookID, false);
            return true;
        }
        else {
            System.out.println("Loan failed");
            return false;
        }
    }

    public boolean processReturn(int loanID){
        Loan loan = library.getLoan(loanID);

        if (client.returnBook(loanID, library)){
            library.getBooks().get(loan.getBookID()).setAvailability(true);
            updateBookStatus(loan.getBookID(), true);
            return true;
        }
        else {
            return false;
        }



    }

    private void populateLibrary(){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            String selectBooks = "SELECT available2.book_id, available2.title, available2.authors, available2.rating, " +
                    "available2.num_pages, available2.year, available2.ready FROM available2";
            String selectLoans = "SELECT loans.loan_id, loans.book_id, loans.borrower, loans.borrow_date, " +
                    "loans.return_date, loans.overdue FROM loans";
            try (PreparedStatement statement = connection.prepareStatement(selectBooks)) {
                try (ResultSet booksResultSet = statement.executeQuery()) {
                    while (booksResultSet.next()) {
                        int bookID = booksResultSet.getInt("book_id");
                        String bookTitle = booksResultSet.getString("title");
                        String bookAuthor = booksResultSet.getString("authors");
                        double bookRating = booksResultSet.getDouble("rating");
                        int bookPages = booksResultSet.getInt("num_pages");
                        int bookYear = booksResultSet.getInt("year");
                        boolean availability = booksResultSet.getBoolean("ready");

                        Book newBook = new Book(bookID, bookTitle, bookAuthor,
                                bookRating, bookPages, bookYear, availability);
                        library.addBook(newBook);
                        }
                    }
                }

            try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                try (ResultSet loansResultSet = statement.executeQuery()) {
                    while (loansResultSet.next()) {
                        int loanID = loansResultSet.getInt("loan_id");
                        int bookID = loansResultSet.getInt("book_id");
                        String username = loansResultSet.getString("borrower");
                        Date borrowDate = loansResultSet.getDate("borrow_date");
                        Date returnDate = loansResultSet.getDate("return_date");
                        boolean overdueStatus = loansResultSet.getBoolean("overdue");

                        Loan newLoan = new Loan(loanID, bookID, username, borrowDate, returnDate, overdueStatus);
                        library.addLoan(newLoan);
                    }
                }
            }


        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }


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
    public void setUser(AccountList accList, String username){
        client = (User) accList.getMember(username);
    }

}
