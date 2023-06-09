package com.example.librarydatabase.Model;

import java.io.Serializable;


public abstract class Member implements Serializable {
    private final String username;
    private final String password;
    private boolean isAdminRole;

    public Member(String username, String password){
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
    public boolean getRole(){
        return isAdminRole;
    }


}
