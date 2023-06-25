package com.library_database.library_app.controller;

import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class AdminController extends MasterController {
    private Admin client;

    public AdminController() {
        library = new Library(this);
    }

    public void setClient(Admin client) {
        this.client = client;
    }

    // Process the creation of a new book
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

    // Update SQL database book table by creating or deleting a book
    private void updateBookDatabase(Book book, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
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
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    // Update the SQL database account table
    private void updateAccounts(Account account, boolean createOrDelete) {
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


    // Overloaded helper function that edits SQL database book table with given data
    private boolean editSQLDatabase(int bookID, String title, String author, double rating, int num_pages,
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
    private boolean editSQLDatabase(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
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

    // Process the edit of a book
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

    // Process a loan edit
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

    // Process the search of books
    public boolean processSearchBooks(String title, String author, boolean minOrMax, double rating, String pageLength,
                                      Integer year, boolean availability) throws SQLException, IOException {
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

    // Process the loan search
    public boolean processSearchLoans(String bookTitle, String borrower, Date sqlBorrowDate, Date sqlReturnDate,
                                      boolean overdue) throws SQLException, IOException {
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

        try (Connection connection = establishConnection()) {
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

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean processSearchAccounts(String username, boolean admin, boolean reset) throws SQLException, IOException {
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

        try (Connection connection = establishConnection()) {
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
        }
        return false;
    }



}
