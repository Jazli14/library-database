package com.library_database.library_app.Model;

import java.sql.Date;

public class Admin extends Account {
    public Admin(String username, String password){
        super(username, password);
        setAdmin(true);
    }

    public Book createBook(Library library, int bookID, String title, String author, double rating,
                      int num_pages, int year, boolean availability){
        Book newBook = new Book(bookID, title, author, rating, num_pages, year, availability);
        if (!library.containsBook(bookID)){
            library.addBook(newBook);
            System.out.println("Successfully added a new book of ID " + newBook.getBookID());
            return newBook;
        }
        else {
            System.out.println("Book ID is already used");
            return null;
        }
    }

    public Book removeBook(Library library, int bookID){
        if (library.containsBook(bookID)){
            Book removedBook = library.removeBook(bookID);
            System.out.println("Removed book successfully.");
            return removedBook;

        }
        else {
            System.out.println("This book does not exist.");
            return null;
        }
    }

    public Loan createLoan(Library library, int bookID, String title, String username, Date borrowDate,
                           Date returnDate, boolean overdueStatus){

        Loan newLoan = new Loan(library, bookID, title, username, borrowDate, returnDate, overdueStatus);
        library.addLoan(newLoan);
        System.out.println("Successfully added a new loan of ID " + newLoan.getLoanID());
        return newLoan;

    }

    public Loan removeLoan(Library library, int loanID){
        if (library.containsLoan(loanID)){
            Loan removedLoan = library.removeLoan(loanID);
            System.out.println("Removed loan successfully.");
            return removedLoan;
        }
        else {
            System.out.println("This loan does not exist.");
            return null;
        }
    }

    public Account removeAccount(Library library, String username){
        Account removedAccount = library.removeAccount(username);
            System.out.println("Removed account successfully.");
            return removedAccount;
    }

    public Account createAccount(Library library, String username, String password, boolean admin){
        return library.addAccount(username, password, admin);
    }


    public void editBookTitle(Library library, int bookID, String newTitle){
        library.getBook(bookID).setTitle(newTitle);
    }

    public void editBookAuthor(Library library, int bookID, String newAuthor){
        library.getBook(bookID).setAuthor(newAuthor);

    }

    public void editBookRating(Library library, int bookID, double newRating){
        library.getBook(bookID).setRating(newRating);
    }

    public void editBookLength(Library library, int bookID, int newLength){
        library.getBook(bookID).setLength(newLength);
    }

    public void editBookYear(Library library, int bookID, int newYear){
        library.getBook(bookID).setYear(newYear);
    }

    public void editBookAvailability(Library library, int bookID, boolean newAvailability){
        library.getBook(bookID).setAvailability(newAvailability);
    }

    public void editLoanBorrower(Library library, int loanID, String newUsername){
        library.getLoan(loanID).setUsername(newUsername);
    }

    public void editLoanBorrowDate(Library library, int loanID, Date newBorrowDate){
        library.getLoan(loanID).setBorrowDate(newBorrowDate);
    }

    public void editLoanReturnDate(Library library, int loanID, Date newReturnDate){
        library.getLoan(loanID).setReturnDate(newReturnDate);

    }

    public void editLoanOverdue(Library library, int loanID, boolean newOverdue){
        library.getLoan(loanID).setOverdue(newOverdue);
    }


}
