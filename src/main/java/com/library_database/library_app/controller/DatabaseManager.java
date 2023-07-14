package com.library_database.library_app.controller;
import com.library_database.library_app.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class DatabaseManager {
    // Has ability to establish connection to the SQL database and perform actions on it
    // Static methods are used to access the database
    private static final Connection connection;

    static {
        try {
            connection = establishConnection();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Establish a connection to the PostgreSQL database through a static method
    static Connection establishConnection() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/java/com/library_database/library_app/controller/config.properties")) {
            props.load(fis);
        }

        String url = props.getProperty("dbUrl");
        String username = props.getProperty("dbUsername");
        String password = props.getProperty("dbPassword");

        return DriverManager.getConnection(url, username, password);
    }

    // Insert a loan, delete a loan or update overdue status of a loan in the SQL database's loan table
    public static void updateLoans(Loan loan, int action) {
        try {
            if (action == 0) {
                // Insert a new loan to loan table
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
                // Delete a loan from the loan table with the given loan ID
                String deleteLoanQuery = "DELETE FROM loans WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteLoanQuery)) {
                    statement.setInt(1, loan.getLoanID()); // Set the loan_id value
                    statement.executeUpdate();
                }
            } else {
                // Update a specific loan's overdue state
                String overdueLoanQuery = "UPDATE loans SET overdue = ? WHERE loan_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(overdueLoanQuery)) {
                    statement.setBoolean(1, loan.getIsOverdue()); // Set the loan_id value
                    statement.setInt(2, loan.getLoanID());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Update SQL database book table by creating or deleting a book
    public static void adminUpdateBookDatabase(Book book, boolean createOrDelete) {
        try {
            if (createOrDelete) { // Check for create or delete
                // Create a new book
                String insertBookQuery = "INSERT INTO books (book_id, title, authors, rating, num_pages, year, " +
                        "ready) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertBookQuery)) {
                    // Add the attributes to the new book
                    statement.setInt(1, book.getBookID());
                    statement.setString(2, book.getTitle());
                    statement.setString(3, book.getAuthor());
                    statement.setDouble(4, book.getRating());
                    statement.setInt(5, book.getNum_pages());
                    statement.setInt(6, book.getYear());
                    statement.setBoolean(7, book.getAvailability());
                    statement.executeUpdate();

                }
            } else {
                // Delete a book corresponding to the book ID
                String deleteBookQuery = "DELETE FROM books WHERE book_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteBookQuery)) {
                    statement.setInt(1, book.getBookID()); // Set the book ID value
                    statement.executeUpdate();

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Update the SQL database account table through admin access
    public static void adminUpdateAccounts(Account account, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
            if (createOrDelete) { // Check if creation or deletion
                // Create account
                String insertAccountQuery = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertAccountQuery)) {
                    statement.setString(1, account.getUsername());
                    statement.setString(2, account.getPassword());
                    statement.setBoolean(3, account.getIsAdminRole());
                    statement.executeUpdate();

                }
            } else {
                // Delete account with the given username
                String deleteAccountQuery = "DELETE FROM accounts WHERE username = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteAccountQuery)) {
                    statement.setString(1, account.getUsername()); // Set the account username
                    statement.executeUpdate();
                }
                // Delete all loans the account had
                String deleteAccountLoansQuery = "DELETE FROM loans WHERE borrower = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteAccountLoansQuery)) {
                    statement.setString(1, account.getUsername());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }



    // Handle search books function for both User and Admin through database
    public static Library searchBooks(String title, String author, boolean minOrMax, double rating,
                                      int minLength, int maxLength, int year, boolean isUnavailable, Library curLibrary) {
        // Clear library of books for new SQL search query
        curLibrary.clearBooks();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder selectBooks = new StringBuilder("SELECT * FROM books WHERE 1=1");

        if (!title.isEmpty()) { // If title input isn't empty then add the filter
            selectBooks.append(" AND LOWER(title) LIKE LOWER(?)");
        }

        if (!author.isEmpty()) { // If author input isn't empty then add the filter
            selectBooks.append(" AND LOWER(authors) LIKE LOWER(?)");
        }


        if (!(minLength == -1 && maxLength == -1)) { // If page length isn't empty then add filter
            if (maxLength == -1) {
                selectBooks.append(" AND num_pages > 500");
            } else {
                selectBooks.append(" AND num_pages BETWEEN ? AND ?");
            }

        }

        if (year != -1) { // If year input isn't empty then add filter
            selectBooks.append(" AND year = ?");
        }

        if (!isUnavailable){ // If book unavailability isn't empty then add filter
            selectBooks.append(" AND ready = ?");
        }
        if (!minOrMax) { // Check if the user wanted a minimum or maximum rating
            // False = min
            selectBooks.append(" AND rating >= ?");
        } else {
            selectBooks.append(" AND rating <= ?");

        }

        String query = selectBooks.toString();

        try { // Connect to database
            try (PreparedStatement statement = connection.prepareStatement(query)) { // Create SQL query

                int parameterIndex = 1;

                if (!title.isEmpty()) {
                    // Insert title
                    statement.setString(parameterIndex++, "%" + title.toLowerCase() + "%");
                }

                if (!author.isEmpty()) {
                    // Insert author
                    statement.setString(parameterIndex++, "%" + author.toLowerCase() + "%");
                }


                if (minLength != -1 && maxLength != -1) {
                    // Insert page length
                    statement.setInt(parameterIndex++, minLength);
                    statement.setInt(parameterIndex++, maxLength);
                }

                if (year != -1) {
                    // Insert year
                    statement.setInt(parameterIndex++, year);
                }

                if (!isUnavailable) {
                    // Insert availability
                    statement.setBoolean(parameterIndex++, true);
                }

                // Insert rating
                statement.setDouble(parameterIndex, rating);

                ResultSet resultSet = statement.executeQuery();

                // Process the search results
                while (resultSet.next()) { // Loop through each of the results
                    // Retrieve data from the result set
                    int bookID = resultSet.getInt("book_id");
                    String bookTitle = resultSet.getString("title");
                    double bookRating = resultSet.getDouble("rating");
                    String bookAuthor = resultSet.getString("authors");
                    int bookNumPages = resultSet.getInt("num_pages");
                    int bookYear = resultSet.getInt("year");
                    boolean bookAvailability = resultSet.getBoolean("ready");

                    // Create a new Book with the given data
                    Book newBook = new Book(bookID, bookTitle, bookAuthor,
                            bookRating, bookNumPages, bookYear, bookAvailability);
                    // Add to library
                    curLibrary.addBook(newBook);

                }
                return curLibrary;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Process the loan search with the given information
    public static boolean adminProcessSearchLoans(String bookTitle, String borrower, Date sqlBorrowDate,
                                                  Date sqlReturnDate, boolean overdue, Library library) {
        // Clear library of loans for new SQL query
        library.clearLoans();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder selectLoans = new StringBuilder("SELECT * FROM loans WHERE 1=1");

        if (!borrower.isEmpty()) { // Check if account name isn't empty
            selectLoans.append(" AND LOWER(borrower) LIKE LOWER(?)");
        }

        if (!bookTitle.isEmpty()) { // Check if book title isn't empty
            selectLoans.append(" AND LOWER(title) LIKE LOWER(?)");
        }

        if (sqlBorrowDate != null) { // Check if borrow date isn't empty
            selectLoans.append(" AND borrow_date >= ?");
        }
        if (sqlReturnDate != null) { // Check if return date isn't empty
            selectLoans.append(" AND return_date <= ?");
        }

        // If any of the search criteria is activated then also add the loan overdue parameter
        boolean searchCriteriaExists = !bookTitle.isEmpty() || !borrower.isEmpty() || sqlBorrowDate != null ||
                sqlReturnDate != null || overdue;
        if (searchCriteriaExists) {
            selectLoans.append(" AND overdue = ?");
        }

        String query = selectLoans.toString();

        try {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Insert the given search criteria data
                int parameterIndex = 1;

                if (!borrower.isEmpty()) {
                    statement.setString(parameterIndex++, "%" + borrower.toLowerCase() + "%");
                }

                if (!bookTitle.isEmpty()) {
                    statement.setString(parameterIndex++, "%" + bookTitle.toLowerCase() + "%");
                }

                if (sqlBorrowDate != null) {
                    statement.setDate(parameterIndex++, sqlBorrowDate);
                }
                if (sqlReturnDate != null) {
                    statement.setDate(parameterIndex++, sqlReturnDate);
                }

                if (searchCriteriaExists) {
                    statement.setBoolean(parameterIndex, overdue);
                }

                ResultSet resultSet = statement.executeQuery();

                // Process the search results
                while (resultSet.next()) {
                    // Retrieve data from the result set
                    int loanID = resultSet.getInt("loan_id");
                    int bookID = resultSet.getInt("book_id");
                    String loanTitle = resultSet.getString("title");
                    String loanBorrower = resultSet.getString("borrower");
                    Date returnDate = resultSet.getDate("return_date");
                    Date borrowDate = resultSet.getDate("borrow_date");
                    boolean loanOverdue = resultSet.getBoolean("overdue");

                    // Create a new loan with the retrieved data
                    Loan newLoan = new Loan(loanID, bookID, loanTitle, loanBorrower, borrowDate, returnDate,
                            loanOverdue);
                    // Add that loan to the library to display
                    library.addLoan(newLoan);

                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Search for accounts in the database with given information
    public static boolean adminProcessSearchAccounts(String username, boolean admin, boolean reset, Library library) {
        library.clearAccountsList();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder selectAccounts = new StringBuilder("SELECT * FROM accounts WHERE 1=1");

        if (!username.isEmpty() && !reset) { // Check if the username isn't empty and reset button not pressed
            selectAccounts.append(" AND LOWER(username) LIKE LOWER(?)");
        }
        if (!reset){ // If reset button is pressed then add the criteria if they are an admin
            selectAccounts.append(" AND admin = ?");
        }

        String query = selectAccounts.toString();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Insert the account search criteria data
            int parameterIndex = 1;

            if (!username.isEmpty() && !reset) {
                statement.setString(parameterIndex++, "%" + username.toLowerCase() + "%");
            }

            if (!reset) {
                statement.setBoolean(parameterIndex, admin);
            }

            ResultSet resultSet = statement.executeQuery();

            // Process the search results
            while (resultSet.next()) { // Loop through the queried results
                // Retrieve data from the result set
                String newAccountUsername = resultSet.getString("username");
                String newAccountPassword = resultSet.getString("password");
                boolean newAccountAdmin = resultSet.getBoolean("admin");

                // Add that account to the library to display
                library.addAccount(newAccountUsername, newAccountPassword, newAccountAdmin);

            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // Initialize the newly created account list on startup by accessing SQL database
    public static void authenticatorPopulateAccountListDB(AccountList accList){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            // Create the select query
            String selectSQL = "SELECT username, password, admin FROM accounts";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Retrieve username, password and if they are an admin
                        String newUsername = resultSet.getString("username");
                        String newPassword = resultSet.getString("password");
                        boolean isAdmin = resultSet.getBoolean("admin");
                        if (isAdmin){ // If account is an admin create an Admin class
                            Admin account = new Admin(newUsername, newPassword);
                            accList.add(account);
                        }
                        else { // If account is not an admin create a User class
                            User account = new User(newUsername, newPassword);
                            accList.add(account);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Update SQL database book table when it is returned or loaned out
    public static void changeBookStatus(int bookID, boolean status) {
        try {
            String updateQuery = "UPDATE books SET ready = ? WHERE book_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setBoolean(1, status);
                statement.setInt(2, bookID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate local library of books, loans and accounts
    public static void populateLibrary(Account client, Library library){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
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
                    executeLoanQuery(statement, library);
                }
            }
            else {
                String selectLoans = "SELECT loan_id, book_id, title, borrower, borrow_date, return_date, overdue " +
                        "FROM loans";
                try (PreparedStatement statement = connection.prepareStatement(selectLoans)) {
                    executeLoanQuery(statement, library);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update the SQL database when a new account is registered
    public static void authenticatorUpdateAccountDB(Account account, String password){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            // Create SQL insert query to accounts
            String insertSQL = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                // Insert the given account's username, password and if they are admin
                statement.setString(1, account.getUsername());
                statement.setString(2, password);
                statement.setBoolean(3, account.getIsAdminRole());
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Helper function to add a new loan with the given data to populate the library
    private static void executeLoanQuery(PreparedStatement statement, Library library) throws SQLException {
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

    // Check if the loan is overdue
    public static boolean checkOverdue(Date returnDate){
        LocalDate currentDate = LocalDate.now();
        LocalDate convertedDueDate = returnDate.toLocalDate();
        return currentDate.isAfter(convertedDueDate);

    }


    // Overloaded helper function that edits SQL database book table with given data
    public static boolean adminUpdateDB(int bookID, String title, String author, double rating, int num_pages,
                                        int year, boolean available) {
        try (Connection connection = establishConnection()) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("UPDATE books SET");
            if (!title.isEmpty()) {
                sqlBuilder.append(" title = ?,");
            }

            if (!author.isEmpty()) {
                sqlBuilder.append(" authors = ?,");
            }

            if (rating != -1) {
                sqlBuilder.append(" rating = ?,");
            }
            if (num_pages != -1) {
                sqlBuilder.append(" num_pages = ?,");
            }
            if (year != -1) {
                sqlBuilder.append(" year = ?,");
            }

            sqlBuilder.append(" ready = ? WHERE book_id = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int parameterIndex = 1;

                if (!title.isEmpty()) {
                    statement.setString(parameterIndex++, title);
                }
                if (!author.isEmpty()) {
                    statement.setString(parameterIndex++, author);
                }

                if (rating != -1) {
                    statement.setDouble(parameterIndex++, rating);

                }
                if (num_pages != -1) {
                    statement.setInt(parameterIndex++, num_pages);
                }
                if (year != -1) {
                    statement.setInt(parameterIndex++, year);
                }
                statement.setBoolean(parameterIndex++, available);
                statement.setInt(parameterIndex, bookID);

                // Execute SQL query
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Overloaded helper function that edits SQL database loan table with given data
    public static boolean adminUpdateDB(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        try (Connection connection = establishConnection()) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("UPDATE loans SET");
            if (!borrower.isEmpty()) { // Check if borrower input isn't empty
                sqlBuilder.append(" borrower = ?,");
            }

            if (borrowDate != null) { // Check if borrow date isn't empty
                sqlBuilder.append(" borrow_date = ?,");
            }

            if (returnDate != null) { // Check if return date isn't empty
                sqlBuilder.append(" borrow_date = ?,");
            }

            sqlBuilder.append(" overdue = ? WHERE loan_id = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set all the parameters to the string builder
                int parameterIndex = 1;
                if (!borrower.isEmpty()) {
                    statement.setString(parameterIndex++, borrower);
                }

                if (borrowDate != null) {
                    statement.setDate(parameterIndex++, borrowDate);
                }

                if (borrowDate != null) {
                    statement.setDate(parameterIndex++, returnDate);
                }
                statement.setBoolean(parameterIndex++, overdue);
                statement.setInt(parameterIndex, loanID);

                // Execute SQL query
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
