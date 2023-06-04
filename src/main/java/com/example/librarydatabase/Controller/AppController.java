package com.example.librarydatabase.Controller;
import javafx.stage.*;

public class AppController {
    private Stage stage;
    private LoginController loginController;

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

}
