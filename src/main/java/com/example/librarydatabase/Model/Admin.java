package com.example.librarydatabase.Model;



public class Admin extends Member{
    public Admin(String username, String password){
        super(username, password);
        setAdmin(true);
    }


}
