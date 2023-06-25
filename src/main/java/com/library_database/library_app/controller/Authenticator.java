package com.library_database.library_app.controller;

import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;


public class Authenticator extends MasterController {
    private final AccountList accList;
    public Authenticator(){
        // Want to initialize the AccountList to the one on the server, not a blank one
        accList = new AccountList();
        populateAccountList();
    }
    public LoginScenario processLogin(String username, String password, boolean admin){
        Account returningAccount = accList.getAccount(username);
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();
        // Check if the user exists

        if (usernameEmpty && passwordEmpty){ // If the username and password are null
            return LoginScenario.USERNAME_AND_PASSWORD_EMPTY;
        }
        else if (usernameEmpty) { // Username is null
            return LoginScenario.USERNAME_EMPTY;
        }
        else if (passwordEmpty){ // Password is null
            return LoginScenario.PASSWORD_EMPTY;
        }

        if (returningAccount != null){ // If the account does exist in the list
            if ((admin == returningAccount.getIsAdminRole())){ // Check if account privileges match type of login
                if (returningAccount.validPassword(password)){ // If password is valid log them in
                    System.out.println("Welcome back " + returningAccount.getUsername());
                    return null;
                }
                else {
                    return LoginScenario.USERNAME_PASSWORD_MISMATCH;
                }
            }
        }
        return LoginScenario.USERNAME_NOT_FOUND;
    }

    public AccountList getAccList(){
        return accList;
    }
    public LoginScenario processRegistration(String username, String password, boolean isAdmin){
        boolean usernameTaken = accList.accountExists(username);
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();
        // Check if the username is taken or null inputs

        if (isAdmin && !usernameTaken && !usernameEmpty && !passwordEmpty){
            // Create an Admin if valid inputs and privileges wanted
            Admin newAdmin = new Admin(username, password);
            accList.add(newAdmin);
            updateDatabase(newAdmin, password);
        }
        else if (!usernameTaken && !usernameEmpty && !passwordEmpty) {
            // Create a User if valid inputs and no privileges requested
            User newUser = new User(username, password);
            accList.add(newUser);
            updateDatabase(newUser, password);
        }
        else if (usernameEmpty && passwordEmpty){ // Check if username and password is empty
            return LoginScenario.USERNAME_AND_PASSWORD_EMPTY;
        }
        else if (usernameEmpty){ // Check if username is empty
            return LoginScenario.USERNAME_EMPTY;
        }
        else if (passwordEmpty){ // Check if password is empty
            return LoginScenario.PASSWORD_EMPTY;
        }
        else { // Username was taken
            return LoginScenario.USERNAME_TAKEN;
        }
        return LoginScenario.REGISTRATION_SUCCESS;
    }

    // Initialize the newly created account list on startup by accessing SQL database
    private void populateAccountList(){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            // Create the select query
            String selectSQL = "SELECT username, password, admin FROM accounts";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Retrieve username, password and if they are an admin
                        String newUsername = resultSet.getString("username");
                        String newPassword = resultSet.getString("password");
                        boolean isAdmin = resultSet.getBoolean("admin");
                        if (isAdmin){ // If account is an admin create an Admin class
                            Admin account = new Admin(newUsername, newPassword);
                            accList.add(account);
                        }
                        else { // If account is not an admin create a User class
                            User account = new User(newUsername, newPassword);
                            accList.add(account);
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Update the SQL database when a new account is registered
    private void updateDatabase(Account account, String password){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            // Create SQL insert query to accounts
            String insertSQL = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                // Insert the given account's username, password and if they are admin
                statement.setString(1, account.getUsername());
                statement.setString(2, password);
                statement.setBoolean(3, account.getIsAdminRole());
                statement.executeUpdate();

            }
        } catch (SQLException | IOException e) {
                e.printStackTrace();
        }
    }

}
