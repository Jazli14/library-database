package com.example.librarydatabase.Model;

import java.sql.Date;

import java.util.Map;
import java.util.Random;

public class Loan {
    private int loanID;
    private int bookID;
    private String username;
    private Date borrowDate;
    private Date returnDate;
    private boolean isOverdue;

    public Loan(Library library, int bookID, String username, Date borrowDate, Date returnDate, boolean overdueStatus) {
        this.loanID = generateLoanID(library);
        this.bookID = bookID;
        this.username = username;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isOverdue = overdueStatus;
    }

    public Loan(int loanID, int bookID, String username, Date borrowDate, Date returnDate, boolean overdueStatus) {
        this.loanID = loanID;
        this.bookID = bookID;
        this.username = username;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isOverdue = overdueStatus;

    }

    public int getLoanID() {
        return loanID;
    }

    public int getBookID() {
        return bookID;
    }

    public String getUsername() {
        return username;
    }

    private int generateLoanID(Library library) {
        Map<Integer, Loan> currentLoans = library.getLoans();
        Random random = new Random();
        int newLoanID;
        do {
            newLoanID = random.nextInt(9000000) + 1000000; // Generate a 7-digit loan ID
        } while (currentLoans.containsKey(newLoanID));
        return newLoanID;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }


}

