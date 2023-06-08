package com.example.librarydatabase.Controller;

public interface Login {
    LoginScenario processLogin(String username, String password, boolean admin);
}
