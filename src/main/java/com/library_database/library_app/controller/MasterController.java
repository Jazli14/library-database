package com.library_database.library_app.controller;

import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;

public abstract class MasterController {
    // Super class of UserController and AdminController
    // Ability update SQL table, populate the local library of accounts, books and loans and search via through the
    // DatabaseManager's static method classes

    public Library library;
    protected Connection connection;
    public MasterController() throws SQLException, IOException {
        this.connection = DatabaseManager.establishConnection();

    }
    // Call DatabaseManager to populate the library for either of the Admin and User client
    public void populateLibrary(Account client){
        DatabaseManager.populateLibrary(client, library);
    }

    // Call DatabaseManager to update loans in SQL databases' loan table for User and Admin
    public void updateLoans(Loan loan, int action){
        DatabaseManager.updateLoans(loan, action);
    }

    // Call DatabaseManager to search for books for both User and Admin
    public boolean searchBooks(String title, String author, boolean minOrMax, double rating,
                               int minLength, int maxLength, int year, boolean isUnavailable) {
        Library newLibrary = DatabaseManager.searchBooks(title, author, minOrMax, rating,
                minLength, maxLength, year, isUnavailable, library);
        return newLibrary != null;
    }
}
