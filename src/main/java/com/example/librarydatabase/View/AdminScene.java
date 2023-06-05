package com.example.librarydatabase.View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        FXMLLoader newLoader = SceneUtils.loadScene(stage, "/com/example/librarydatabase/login_scene.fxml",
                "Login To The Library Database");

        LoginScene loginScene = newLoader.getController();
        loginScene.setStage(stage);

    }

    // Other methods and code...
}

