package com.example.librarydatabase.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.*;
import javafx.scene.control.*;
import java.io.IOException;

public class UserScene {
    @FXML
    private Button userLogoutButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void handleLogOut() throws IOException {
        // Load Login Scene
        FXMLLoader newLoader = SceneUtils.loadScene(stage, "/com/example/librarydatabase/login_scene.fxml",
                "Login To The Library Database");
        LoginScene loginScene = newLoader.getController();
        loginScene.setStage(stage);

    }

}

