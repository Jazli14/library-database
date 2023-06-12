package com.library_database.library_app.Model;

import com.library_database.library_app.Controller.UserScenario;

import java.sql.Date;

public class User extends Account {
    public User(String username, String password){
        super(username, password);
        setAdmin(false);
    }

    public UserScenario borrowBook(int bookID, Library library, Date borrowDate, Date returnDate) {
        Book book = library.getBook(bookID);
        if (book.getAvailability()) {
            if (isValidDate(borrowDate, returnDate)){
                book.setAvailability(false);
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
        if (!library.getLoans().containsKey(loanID)){
            System.out.println(library.getBook(removedLoan.getBookID()).getTitle() + " successfully returned");
            return true;
        }
        else {
            System.out.println("System could not return book.");
            return false;
        }

    }

    private boolean isValidDate(Date borrowDate, Date returnDate){
        int comparisonResult = borrowDate.compareTo(returnDate);
        return (comparisonResult < 0);
    }


}
