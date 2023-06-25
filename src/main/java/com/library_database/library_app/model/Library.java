package com.library_database.library_app.model;

import com.library_database.library_app.controller.AdminController;
import com.library_database.library_app.controller.UserController;

import java.util.HashMap;
import java.util.Map;

public class Library {
    // Entity that contains all the books, loans and accounts in three separate hash maps
    private final Map<Integer, Book> books;
    private final Map<Integer, Loan> loans;
    private AccountList accountList;
    public UserController userController;
    public AdminController adminController;

    public Library(UserController userController){
        books = new HashMap<>();
        loans = new HashMap<>();
        this.userController = userController;
    }
    public Library(AdminController adminController){
        books = new HashMap<>();
        loans = new HashMap<>();
        this.adminController = adminController;
    }
    public void addBook(Book book) {
        books.put(book.getBookID(), book);
    }
    public void clearBooks(){
        books.clear();
    }
    public void clearLoans(){
        loans.clear();
    }

    public Book removeBook(int bookID) {
        return books.remove(bookID);
    }

    public Book getBook(int bookID) {
        return books.get(bookID);
    }
    public Map<Integer, Book> getBooks(){
        return books;
    }

    public boolean containsBook(int bookID) {
        return books.containsKey(bookID);
    }

    public boolean containsLoan(int loanID){ return loans.containsKey(loanID); }
    public void addLoan(Loan loan){
        loans.put(loan.getLoanID(), loan);
    }

    public Loan removeLoan(int loanID) { return loans.remove(loanID); }
    public Loan getLoan(int loanID){
        return loans.get(loanID);
    }

    public Account removeAccount(String username) { return accountList.removeAccount(username);}


    // Add account operation
    public Account addAccount(String username, String password, boolean admin) {
        // Check if the username is taken
        boolean usernameTaken = accountList.accountExists(username);
        if (!usernameTaken && admin){ // If username isn't taken, and it's an admin create an admin account
            Admin newAdmin = new Admin(username, password);
            accountList.add(newAdmin);
            return newAdmin;
        }
        else if (!usernameTaken){ // If username isn't taken, and it's not an admin then create a user account
            User newUser = new User(username, password);
            accountList.add(newUser);
            return newUser;
        }
        else {
            return null;
        }
    }

    public void clearAccountsList(){
        accountList.clearAccounts();
    }

    public boolean containsAccount(String username) {
        return accountList.accountExists(username);
    }

    public Map<Integer, Loan> getLoans(){
        return loans;
    }

    public Map<String, Account> getAccounts() {
        return accountList.getAccounts();
    }

    public Account retrieveAccount(String username) {
        return accountList.getAccount(username);
    }
    public void setAccounts (AccountList accList) {
        this.accountList = accList;
    }


}
