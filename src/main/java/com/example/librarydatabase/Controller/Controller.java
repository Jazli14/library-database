package com.example.librarydatabase.Controller;

import com.example.librarydatabase.Model.Book;
import com.example.librarydatabase.Model.Library;
import com.example.librarydatabase.Model.Loan;
import com.example.librarydatabase.Model.Member;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public abstract class Controller {
    public Library library;

    protected static Connection establishConnection() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(
                "src/main/java/com/example/librarydatabase/Controller/config.properties")) {
            props.load(fis);
        }

        String url = props.getProperty("dbUrl");
        String username = props.getProperty("dbUsername");
        String password = props.getProperty("dbPassword");

        return DriverManager.getConnection(url, username, password);
    }


    public void populateLibrary(Member client){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            String selectBooks = "SELECT available2.book_id, available2.title, available2.authors, available2.rating, " +
                    "available2.num_pages, available2.year, available2.ready FROM available2";


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

            if (!client.getRole()) {
                String selectLoans = "SELECT loan_id, book_id, title, borrower, borrow_date, return_date, overdue " +
                        "FROM loans2 WHERE borrower = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                    statement.setString(1, client.getUsername());
                    executeSQLQuery(statement);
                }
            }
            else {
                String selectLoans = "SELECT loan_id, book_id, title, borrower, borrow_date, return_date, overdue " +
                        "FROM loans2";
                try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                    executeSQLQuery(statement);
                }
            }

        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    private void executeSQLQuery(PreparedStatement statement) throws SQLException {
        try (ResultSet loansResultSet = statement.executeQuery()) {
            while (loansResultSet.next()) {
                int loanID = loansResultSet.getInt("loan_id");
                int bookID = loansResultSet.getInt("book_id");
                String username = loansResultSet.getString("borrower");
                String title = loansResultSet.getString("title");
                Date borrowDate = loansResultSet.getDate("borrow_date");
                Date returnDate = loansResultSet.getDate("return_date");
                boolean overdueStatus = loansResultSet.getBoolean("overdue");


                Loan newLoan = new Loan(loanID, bookID, title,
                        username, borrowDate, returnDate, overdueStatus);
                library.addLoan(newLoan);
            }
        }
    }

}
