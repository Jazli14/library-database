package com.example.librarydatabase;

import com.example.librarydatabase.Controller.LoginController;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.IOException;

public class LibraryApp extends Application {

    // start the login scene
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("login_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 350);

        // Create Login Scene Controller
        LoginController loginController = fxmlLoader.getController();
        loginController.setStage(stage);

        stage.setTitle("Login To The Library Database");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}