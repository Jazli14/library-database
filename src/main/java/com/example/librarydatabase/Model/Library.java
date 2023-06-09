package com.example.librarydatabase.Model;

import com.example.librarydatabase.Controller.AdminController;
import com.example.librarydatabase.Controller.UserController;

import java.util.HashMap;
import java.util.Map;

public class Library {
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

    public Account addAccount(String username, String password, boolean admin) {
        boolean usernameTaken = accountList.accountExists(username);
        if (!usernameTaken && admin){
            Admin newAdmin = new Admin(username, password);
            accountList.add(newAdmin);
            return newAdmin;
        }
        else if (!usernameTaken){
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
