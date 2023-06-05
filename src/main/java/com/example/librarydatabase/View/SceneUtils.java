package com.example.librarydatabase.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.IOException;

public class SceneUtils {
    public static FXMLLoader loadScene(Stage stage, String resource, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(resource));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        return loader;
    }
}
