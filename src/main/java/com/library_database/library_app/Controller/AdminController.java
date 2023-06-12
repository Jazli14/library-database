package com.library_database.library_app.Controller;

import com.library_database.library_app.Model.*;

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

    public boolean processCreateBook(int bookID, String title, String author, double rating,
                                     int num_pages, int year, boolean availability) {
        Book newBook = client.createBook(library, bookID, title, author, rating, num_pages, year, availability);

        if (newBook != null) {
            updateBookDatabase(newBook, true);

            return true;
        } else {
            return false;
        }

    }


    public boolean processRemoveBook(int bookID) {
        Book removedBook = client.removeBook(library, bookID);
        if (removedBook != null) {
            updateBookDatabase(removedBook, false);
            return true;
        } else {
            return false;
        }

    }

    public int processCreateLoan(int bookID, String title, String username, Date borrowDate,
                                 Date returnDate, boolean overdueStatus) {
        if (!library.containsAccount(username)){
            return 1;
        }
        else if (library.retrieveAccount(username).getIsAdminRole()){
            return 2;
        }
        else if (library.containsBook(bookID)) {
            if (!(library.getBook(bookID).getTitle().equals(title))){
                return 3;
            }
        }
        else {
            Loan newLoan = client.createLoan(library, bookID, title, username, borrowDate, returnDate, overdueStatus);

            if (newLoan != null) {
                updateLoans(newLoan, 0);

                return newLoan.getLoanID();
            }

        }
        return 0;

    }

    public boolean processRemoveLoan(int loanID) {
        Loan removedLoan = client.removeLoan(library, loanID);

        if (removedLoan != null) {
            updateLoans(removedLoan, 1);

            return true;
        } else {
            return false;
        }

    }

    public String processCreateAccount(String username, String password, boolean admin) {
        Account newAccount = client.createAccount(library, username, password, admin);

        if (newAccount != null) {
            updateAccounts(newAccount, true);

            return newAccount.getUsername();
        } else {
            return null;
        }
    }

    public boolean processRemoveAccount(String username) {
        Account removedAccount = client.removeAccount(library, username);

        if (removedAccount != null) {
            updateAccounts(removedAccount, false);

            return true;
        } else {
            return false;
        }

    }

    private void updateBookDatabase(Book book, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
            if (createOrDelete) {
                String insertBookQuery = "INSERT INTO books (book_id, title, authors, rating, num_pages, year, " +
                        "ready) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertBookQuery)) {
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
                String deleteBookQuery = "DELETE FROM books WHERE book_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteBookQuery)) {
                    statement.setInt(1, book.getBookID()); // Set the loan_id value
                    statement.executeUpdate();

                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }


    private void updateAccounts(Account account, boolean createOrDelete) {
        try (Connection connection = establishConnection()) {
            if (createOrDelete) {
                String insertAccountQuery = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertAccountQuery)) {
                    statement.setString(1, account.getUsername());
                    statement.setString(2, account.getPassword());
                    statement.setBoolean(3, account.getIsAdminRole());
                    statement.executeUpdate();

                }
            } else {
                String deleteAccountQuery = "DELETE FROM accounts WHERE username = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteAccountQuery)) {
                    statement.setString(1, account.getUsername()); // Set the loan_id value
                    statement.executeUpdate();
                }
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

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean editSQLDatabase(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        try (Connection connection = establishConnection()) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("UPDATE loans SET");
            if (!borrower.isEmpty()) {
                sqlBuilder.append(" borrower = ?,");
            }

            if (borrowDate != null) {
                sqlBuilder.append(" borrow_date = ?,");
            }

            if (returnDate != null) {
                sqlBuilder.append(" borrow_date = ?,");
            }

            sqlBuilder.append(" overdue = ? WHERE loan_id = ?");

            String sql = sqlBuilder.toString();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean processEditBook(int bookID, String title, String author, double rating, int num_pages, int year,
                                   boolean available) {
        if (!title.isEmpty()) {
            client.editBookTitle(library, bookID, title);
        }
        if (!author.isEmpty()) {
            client.editBookAuthor(library, bookID, author);
        }
        if (rating != -1) {
            client.editBookRating(library, bookID, rating);
        }
        if (num_pages != -1) {
            client.editBookLength(library, bookID, num_pages);
        }
        if (year != -1) {
            client.editBookYear(library, bookID, year);
        }
        client.editBookAvailability(library, bookID, available);
        // update db
        return editSQLDatabase(bookID, title, author, rating, num_pages, year, available);

    }

    public boolean processEditLoan(int loanID, String borrower, Date borrowDate, Date returnDate, boolean overdue) {
        if (!borrower.isEmpty()) {
            client.editLoanBorrower(library, loanID, borrower);
        }
        if (borrowDate != null) {
            client.editLoanBorrowDate(library, loanID, borrowDate);
        }
        if (returnDate != null) {
            client.editLoanReturnDate(library, loanID, returnDate);
        }
        client.editLoanOverdue(library, loanID, overdue);

        // update db

        return editSQLDatabase(loanID, borrower, borrowDate, returnDate, overdue);
    }

    public void setAdminAndPopulate(Account client, AccountList accList) {
        this.client = (Admin) client;
        this.library.setAccounts(accList);
        populateLibrary(client);

    }


    public boolean processSearchBooks(String title, String author, boolean minOrMax, double rating,
                                      String pageLength, Integer year, boolean availability) throws SQLException, IOException {

        int maxRange;
        int minRange;
        if (pageLength != null) {
            if (pageLength.equals("More than 500")) {
                maxRange = -1;
                minRange = 501;
            } else if (pageLength.equals("Any")) {
                maxRange = -1;
                minRange = -1;
            } else {
                String[] rangeValues = pageLength.split("-");
                minRange = Integer.parseInt(rangeValues[0]);
                maxRange = Integer.parseInt(rangeValues[1]);

            }
        } else {
            minRange = -1;
            maxRange = -1;
        }

        int intYear;
        intYear = Objects.requireNonNullElse(year, -1);
        return searchBooks(title, author, minOrMax, rating, minRange, maxRange, intYear, availability);
    }


    public boolean processSearchLoans(String bookTitle, String borrower, Date sqlBorrowDate, Date sqlReturnDate,
                                      boolean overdue) throws SQLException, IOException {
        library.clearLoans();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder selectLoans = new StringBuilder("SELECT * FROM loans WHERE 1=1");

        if (!borrower.isEmpty()) {
            selectLoans.append(" AND LOWER(borrower) LIKE LOWER(?)");
        }

        if (!bookTitle.isEmpty()) {
            selectLoans.append(" AND LOWER(title) LIKE LOWER(?)");
        }

        if (sqlBorrowDate != null) {
            selectLoans.append(" AND borrow_date >= ?");
        }
        if (sqlReturnDate != null) {
            selectLoans.append(" AND return_date <= ?");
        }

        boolean searchCriteriaExists = !bookTitle.isEmpty() || !borrower.isEmpty() || sqlBorrowDate != null ||
                sqlReturnDate != null || overdue;
        if (searchCriteriaExists) {
            selectLoans.append(" AND overdue = ?");
        }

        String query = selectLoans.toString();

        try (Connection connection = establishConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

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

                    Loan newLoan = new Loan(loanID, bookID, loanTitle, loanBorrower, borrowDate, returnDate,
                            loanOverdue);

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

        if (!username.isEmpty() && !reset) {
            selectAccounts.append(" AND LOWER(username) LIKE LOWER(?)");
        }
        if (!reset){
            selectAccounts.append(" AND admin = ?");
        }

        String query = selectAccounts.toString();

        try (Connection connection = establishConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                int parameterIndex = 1;

                if (!username.isEmpty() && !reset) {
                    statement.setString(parameterIndex++, "%" + username.toLowerCase() + "%");
                }

                if (!reset) {
                    statement.setBoolean(parameterIndex, admin);
                }

                ResultSet resultSet = statement.executeQuery();

                // Process the search results
                while (resultSet.next()) {
                    // Retrieve data from the result set
                    String newAccountUsername = resultSet.getString("username");
                    String newAccountPassword = resultSet.getString("password");
                    boolean newAccountAdmin = resultSet.getBoolean("admin");

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
