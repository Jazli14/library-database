package com.example.librarydatabase.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/login_scene.fxml"));
        Parent root = loader.load();

        LoginScene loginScene = loader.getController();
        loginScene.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login To The Library Database");
        stage.show();
    }

}

