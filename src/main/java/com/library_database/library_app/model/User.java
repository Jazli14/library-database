package com.library_database.library_app.model;

import com.library_database.library_app.controller.UserScenario;

import java.sql.Date;

public class User extends Account {
    // Allowed to loan out books and return them
    public User(String username, String password){
        super(username, password);
        setAdmin(false);
    }

    public UserScenario borrowBook(int bookID, Library library, Date borrowDate, Date returnDate) {
        Book book = library.getBook(bookID);
        if (book.getAvailability()) { // Check if book is available
            if (isValidDate(borrowDate, returnDate)){ // Check if the borrow period is valid
                // Change book availability to false
                book.setAvailability(false);
                // Create new loan
                Loan newLoan = new Loan(library, book.getBookID(), book.getTitle(), getUsername(),
                        borrowDate, returnDate, false);
                library.addLoan(newLoan);
                library.userController.updateLoans(newLoan,0);
                System.out.println("Borrowing book: " + book.getTitle());
                return UserScenario.LOAN_SUCCESS;
            }
            else {
                System.out.println("Invalid date: borrow date came after return date.");
                return UserScenario.LOAN_FAILURE_DATE;
            }

        } else {
            System.out.println(book.getTitle()+ " is already loaned out.");
            return UserScenario.LOAN_FAILURE_AVAILABLE;
        }
    }

    public boolean returnBook(int loanID, Library library){
        Loan removedLoan = library.removeLoan(loanID);
        if (!library.getLoans().containsKey(loanID)){ // If library contains book return it
            System.out.println(library.getBook(removedLoan.getBookID()).getTitle() + " successfully returned");
            return true;
        }
        else {
            System.out.println("System could not return book.");
            return false;
        }

    }

    private boolean isValidDate(Date borrowDate, Date returnDate){
        // Ensure that the return date is after borrow date
        int comparisonResult = borrowDate.compareTo(returnDate);
        return (comparisonResult < 0);
    }


}
