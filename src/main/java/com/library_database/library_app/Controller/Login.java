package com.library_database.library_app.Controller;

public interface Login {
    LoginScenario processLogin(String username, String password, boolean admin);
}
