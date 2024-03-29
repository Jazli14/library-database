package com.library_database.library_app.model;

import java.io.Serializable;


public abstract class Account implements Serializable {
    // Superclass that encompasses both a User and an Admin
    // Both have a username, password and if they are an admin
    private final String username;
    private final String password;
    private boolean isAdminRole;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }

    public boolean validPassword(String password) {
        return this.password.equals(password);
    }

    protected void setAdmin(boolean adminOrUser) {
        this.isAdminRole = adminOrUser;
    }
    public boolean getIsAdminRole(){
        return isAdminRole;
    }

    public String getPassword() { return password; }

}
