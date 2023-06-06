package com.example.librarydatabase.Model;

import java.sql.Date;

public class User extends Member {
    public User(String username, String password){
        super(username, password);
        setAdmin(false);
    }

    public boolean borrowBook(int bookID, Library library, Date borrowDate, Date returnDate) {
        Book book = library.getBook(bookID);
        if (book.getAvailability()) {
            if (isValidDate(borrowDate, returnDate)){
                book.setAvailability(false);
                Loan newLoan = new Loan(library, book.getBookID(), getUsername(), borrowDate, returnDate, false);
                library.addLoan(newLoan);
                System.out.println("Borrowing book: " + book.getTitle());
                return true;
            }
            else {
                System.out.println("Invalid date: borrow date came after return date.");
                return false;
            }

        } else {
            System.out.println(book.getTitle()+ " is already loaned out.");
            return false;
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
        boolean validDate = (comparisonResult < 0);
        return validDate;
    }


}
