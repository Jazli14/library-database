package com.example.librarydatabase;

import com.example.librarydatabase.Model.*;

public class Authenticator {
    private AccountList accounts;
    public Authenticator(){
        // Want to initialize the AccountList to the one on the server, not a blank one
        accounts = new AccountList();
    }
    public boolean processLogin(String username, String password){
        Member returningMember = accounts.getMember(username);
        if (returningMember != null){
            if (returningMember.validPassword(password)){
                System.out.println("WELCOME BACK " + returningMember.getUsername());
                return true;
            }
            else {
                System.out.println("???? what happened");
                return false;
            }
        }
        else {
            System.out.println("NO ONE'S NAMED THAT DUMMY");
            return false;
        }
    }
    public boolean processRegistration(String username, String password, boolean isAdmin){
        boolean isValidUsername = !accounts.memberExists(username);

        if (isAdmin && isValidUsername){
            Admin newAdmin = new Admin(username, password);
            accounts.add(newAdmin);
            System.out.println("ADDED NEW Admin: " + newAdmin.getUsername());
        }
        else if (isValidUsername) {
            User newUser = new User(username, password);
            accounts.add(newUser);
            System.out.println("ADDED NEW User: " + newUser.getUsername());
        }
        else {
            System.out.println("Choose a new username");
            return false;
        }
        return true;
    }


}
