package com.example.librarydatabase.View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminScene {
    @FXML
    private Button adminLogoutButton;
    private Stage stage;
    @FXML
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void handleLogOut() throws IOException {
        // Load Login Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/login_scene.fxml"));
        Parent root = loader.load();

        LoginScene loginScene = loader.getController();
        loginScene.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login To The Library Database");
        stage.show();
    }

    // Other methods and code...
}

