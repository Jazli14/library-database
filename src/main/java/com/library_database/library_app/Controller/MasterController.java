package com.library_database.library_app.Controller;

import com.library_database.library_app.Model.Book;
import com.library_database.library_app.Model.Library;
import com.library_database.library_app.Model.Loan;
import com.library_database.library_app.Model.Account;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public abstract class MasterController {
    public Library library;

    public static Connection establishConnection() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(
                "src/main/java/com/library_database/library_app/Controller/config.properties")) {
            props.load(fis);
        }

        String url = props.getProperty("dbUrl");
        String username = props.getProperty("dbUsername");
        String password = props.getProperty("dbPassword");

        return DriverManager.getConnection(url, username, password);
    }


    public void populateLibrary(Account client){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            String selectBooks = "SELECT book_id, title, authors, rating, " +
                    "num_pages, year, ready FROM books";


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

            if (!client.getIsAdminRole()) {
                String selectLoans = "SELECT loan_id, book_id, title, borrower, borrow_date, return_date, overdue " +
                        "FROM loans WHERE borrower = ?";
                try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                    statement.setString(1, client.getUsername());
                    executeLoanQuery(statement);
                }
            }
            else {
                String selectLoans = "SELECT loan_id, book_id, title, borrower, borrow_date, return_date, overdue " +
                        "FROM loans";
                try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                    executeLoanQuery(statement);
                }
            }

        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    private void executeLoanQuery(PreparedStatement statement) throws SQLException {
        try (ResultSet loansResultSet = statement.executeQuery()) {
            while (loansResultSet.next()) {
                int loanID = loansResultSet.getInt("loan_id");
                int bookID = loansResultSet.getInt("book_id");
                String username = loansResultSet.getString("borrower");
                String title = loansResultSet.getString("title");
                Date borrowDate = loansResultSet.getDate("borrow_date");
                Date returnDate = loansResultSet.getDate("return_date");
                boolean overdueStatus = loansResultSet.getBoolean("overdue");

                if (!overdueStatus){
                    // if the loan is not overdue it will check if it is today when the user logs in
                    overdueStatus = checkOverdue(returnDate);
                }

                if (overdueStatus) {
                    // if it is overdue it will set the overdueStatus to true and update the database
                    Loan overdueLoan = new Loan(loanID, bookID, title,
                            username, borrowDate, returnDate, true);
                    library.addLoan(overdueLoan);
                    updateLoans(overdueLoan, 2);
                }
                else {
                    // if it is not overdue it will set the overdueStatus to false
                    Loan onTimeLoan = new Loan (loanID, bookID, title, username, borrowDate, returnDate, false);
                    library.addLoan(onTimeLoan);
                }

            }
        }
    }

    public void updateLoans(Loan loan, int action) {
        try (Connection connection = establishConnection()) {
            if (action == 0) {
                String insertLoanQuery = "INSERT INTO loans (loan_id, book_id, title, borrower, borrow_date," +
                        " return_date, overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertLoanQuery)) {
                    statement.setInt(2, loan.getBookID());
                    statement.setString(3, loan.getTitle());
                    statement.setInt(1, loan.getLoanID());
                    statement.setString(4, loan.getUsername());
                    statement.setDate(5, loan.getBorrowDate());
                    statement.setDate(6, loan.getReturnDate());
                    statement.setBoolean(7, loan.getIsOverdue());
                    statement.executeUpdate();

                }
            } else if (action == 1){
                String deleteLoanQuery = "DELETE FROM loans WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteLoanQuery)) {
                    statement.setInt(1, loan.getLoanID()); // Set the loan_id value
                    statement.executeUpdate();
                }
            } else {
                String deleteLoanQuery = "UPDATE loans SET overdue = ? WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteLoanQuery)) {
                    statement.setBoolean(1, loan.getIsOverdue()); // Set the loan_id value
                    statement.setInt(2, loan.getLoanID());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public boolean checkOverdue(Date returnDate){
        LocalDate currentDate = LocalDate.now();
        LocalDate convertedDueDate = returnDate.toLocalDate();
        return currentDate.isAfter(convertedDueDate);

    }


    public boolean searchBooks(String title, String author, boolean minOrMax, double rating,
                               int minLength, int maxLength, int year, boolean isUnavailable) throws SQLException, IOException {
        library.clearBooks();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder selectBooks = new StringBuilder("SELECT * FROM books WHERE 1=1");


        if (!title.isEmpty()) {
            selectBooks.append(" AND LOWER(title) LIKE LOWER(?)");
        }

        if (!author.isEmpty()) {
            selectBooks.append(" AND LOWER(authors) LIKE LOWER(?)");
        }


        if (!(minLength == -1 && maxLength == -1)) {
            if (maxLength == -1) {
                selectBooks.append(" AND num_pages > 500");
            } else {
                selectBooks.append(" AND num_pages BETWEEN ? AND ?");
            }

        }

        if (year != -1) {
            selectBooks.append(" AND year = ?");
        }

        if (!isUnavailable){
            selectBooks.append(" AND ready = ?");
        }
        if (!minOrMax) {
            // False = min
            selectBooks.append(" AND rating >= ?");
        } else {
            selectBooks.append(" AND rating <= ?");

        }


        String query = selectBooks.toString();

        try (Connection connection = establishConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                int parameterIndex = 1;

                if (!title.isEmpty()) {
                    statement.setString(parameterIndex++, "%" + title.toLowerCase() + "%");
                }

                if (!author.isEmpty()) {
                    statement.setString(parameterIndex++, "%" + author.toLowerCase() + "%");
                }


                if (minLength != -1 && maxLength != -1) {
                    statement.setInt(parameterIndex++, minLength);
                    statement.setInt(parameterIndex++, maxLength);
                }

                if (year != -1) {
                    statement.setInt(parameterIndex++, year);
                }

                if (!isUnavailable){
                    statement.setBoolean(parameterIndex++, true);
                }

                statement.setDouble(parameterIndex, rating);

                ResultSet resultSet = statement.executeQuery();

                // Process the search results
                while (resultSet.next()) {
                    // Retrieve data from the result set
                    int bookID = resultSet.getInt("book_id");
                    String bookTitle = resultSet.getString("title");
                    double bookRating = resultSet.getDouble("rating");
                    String bookAuthor = resultSet.getString("authors");
                    int bookNumPages = resultSet.getInt("num_pages");
                    int bookYear = resultSet.getInt("year");
                    boolean bookAvailability = resultSet.getBoolean("ready");
                    Book newBook = new Book(bookID, bookTitle, bookAuthor,
                            bookRating, bookNumPages, bookYear, bookAvailability);
                    library.addBook(newBook);

                }
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return false;
    }
}
