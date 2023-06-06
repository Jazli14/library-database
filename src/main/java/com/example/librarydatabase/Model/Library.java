package com.example.librarydatabase.Model;

import com.example.librarydatabase.Controller.UserController;

import java.util.HashMap;
import java.util.Map;

public class Library {
    private Map<Integer, Book> books;
    private Map<Integer, Loan> loans;
    public UserController userController;   // mm

    public Library(UserController userController){
        books = new HashMap<>();
        loans = new HashMap<>();
        this.userController = userController;
    }
    public void addBook(Book book) {
        books.put(book.getBookID(), book);
    }

    public void removeBook(int bookID) {
        books.remove(bookID);
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

    public void clearBooks() {
        books.clear();
    }

    public void addLoan(Loan loan){
        loans.put(loan.getLoanID(), loan);
    }

    public Loan removeLoan(int loanID) {
        Loan removedLoan = loans.remove(loanID);

        return removedLoan;
    }
    public Loan getLoan(int loanID){
        return loans.get(loanID);
    }

    public Map<Integer, Loan> getLoans(){
        return loans;
    }



}
