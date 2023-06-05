package com.example.librarydatabase.Model;


public class User extends Member {
    public User(String username, String password){
        super(username, password);
        setAdmin(false);
    }

}
