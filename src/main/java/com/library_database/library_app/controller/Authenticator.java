package com.library_database.library_app.controller;

import com.library_database.library_app.model.*;

import java.io.IOException;
import java.sql.*;


public class Authenticator extends MasterController {
    private final AccountList accList;
    public Authenticator() throws SQLException, IOException {
        super();
        // Want to initialize the AccountList to the one on the server, not a blank one
        accList = new AccountList();
        populateAccountList(accList);
    }
    // Retrieve username, password and admin information to validate the login attempt
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

    // Retrieve account list
    public AccountList getAccList(){
        return accList;
    }

    // Retrieve username, password and admin information to validate registration attempt
    public LoginScenario processRegistration(String username, String password, boolean isAdmin){
        boolean usernameTaken = accList.accountExists(username);
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();
        // Check if the username is taken or null inputs

        if (isAdmin && !usernameTaken && !usernameEmpty && !passwordEmpty){
            // Create an Admin if valid inputs and privileges wanted
            Admin newAdmin = new Admin(username, password);
            accList.add(newAdmin);
            updateAccountDatabase(newAdmin, password);
        }
        else if (!usernameTaken && !usernameEmpty && !passwordEmpty) {
            // Create a User if valid inputs and no privileges requested
            User newUser = new User(username, password);
            accList.add(newUser);
            updateAccountDatabase(newUser, password);
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

    // Call DatabaseManager to populate the account list through the SQL database
    private void populateAccountList(AccountList accList){
        DatabaseManager.authenticatorPopulateAccountListDB(accList);
    }

    // Call DatabaseManager to update the SQL database when an acoount is registered
    private void updateAccountDatabase(Account account, String password){
        DatabaseManager.authenticatorUpdateAccountDB(account, password);
    }

}
