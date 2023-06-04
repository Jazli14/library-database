package com.example.librarydatabase.Controller;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.librarydatabase.Authenticator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;


import java.io.IOException;

public class LoginController implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab userTab;
    @FXML
    private Tab adminTab;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passWord;
    @FXML
    private Button userConfirm;
    @FXML
    private Button adminConfirm;
    @FXML
    private Button userRegister;
    @FXML
    private Button adminRegister;
    private boolean isInitialized = false;

    private Tab currentTab = userTab;
    private Stage stage;

    Authenticator auth;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }


    @FXML
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public LoginController(){
        auth = new Authenticator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentTab = tabPane.getSelectionModel().getSelectedItem();
        isInitialized = true;
    }
    @FXML
    private void handleLogin() throws IOException {
        String username = userName.getText();
        String password = passWord.getText();
        boolean loginSuccess = auth.processLogin(username, password);

        try {
            if (loginSuccess && currentTab.equals(userTab)) {

                // Load User Scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/user_scene.fxml"));
                Parent root = loader.load();

                UserSceneController userController = loader.getController();
                userController.setStage(stage);

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("User View");
                stage.show();
            } else if (loginSuccess && currentTab.equals(adminTab)) {
                // Load Admin Scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/librarydatabase/admin_scene.fxml"));
                Parent root = loader.load();

                AdminSceneController adminController = loader.getController();
                adminController.setStage(stage);

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Admin View");
                stage.show();
            }
            else if (!loginSuccess){
                System.out.println("Try again");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        String username = userName.getText();
        String password = passWord.getText();
        boolean isValidRegistration = false;

        try {
            if (currentTab.equals(userTab)) {
                isValidRegistration = auth.processRegistration(username, password, false);
            }
            else if (currentTab.equals(adminTab)){
                isValidRegistration = auth.processRegistration(username, password, true);
            }

            if (isValidRegistration){
                System.out.println("Your account is registered");
            }
            else {
                System.out.println("Registration failed");
            }

        } catch (Exception e) {
                e.printStackTrace();
            }
    }
    @FXML
    private void handleTabSelectionChanged() {
        if (isInitialized) {
            currentTab = tabPane.getSelectionModel().getSelectedItem();
        }
    }
}

