package com.library_database.library_app.view;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.library_database.library_app.controller.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.*;
import javafx.stage.*;

public class LoginScene implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab userTab;
    @FXML
    private Tab adminTab;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField userPass;
    @FXML
    private TextField adminName;
    @FXML
    private PasswordField adminPass;
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

    final Authenticator auth;

    @FXML
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public LoginScene() throws SQLException, IOException {
        auth = new Authenticator();
    }

    // Initialize the scene
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentTab = tabPane.getSelectionModel().getSelectedItem();
        isInitialized = true;
    }

    // Handle login button
    @FXML
    private void handleLogin() {
        LoginScenario loginSuccess;

        try {
            if (currentTab.equals(userTab)) {
                String username = userName.getText();
                String password = userPass.getText();
                loginSuccess = auth.processLogin(username, password, false);

                if (loginSuccess == null){ // Checks if login was successful
                    // Load User Scene
                    FXMLLoader newLoader = Scene.loadScene(stage, "/com/library_database/library_app/user_scene.fxml",
                            "User Access");
                    UserScene userScene = newLoader.getController();
                    userScene.setStage(stage);
                    userScene.initializeController(auth.getAccList(), username);

                } else {
                    System.out.println("Login failed");
                    showAlert(loginSuccess);
                }

            } else if (currentTab.equals(adminTab)) {
                String username = adminName.getText();
                String password = adminPass.getText();
                loginSuccess = auth.processLogin(username, password, true);


                if (loginSuccess==null){ // Checks if login was successful
                    // Load Admin Scene
                    FXMLLoader newLoader = Scene.loadScene(stage, "/com/library_database/library_app/admin_scene.fxml",
                            "Admin Access");
                    AdminScene adminScene = newLoader.getController();
                    adminScene.setStage(stage);
                    adminScene.initializeAdminController(auth.getAccList(), username);

                } else {
                    System.out.println("Login failed");
                    showAlert(loginSuccess);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Handle register button
    @FXML
    private void handleRegister() {

        LoginScenario isValidRegistration = null;

        try {
            if (currentTab.equals(userTab)) { //
                String username = userName.getText();
                String password = userPass.getText();
                isValidRegistration = auth.processRegistration(username, password, false);
            }
            else if (currentTab.equals(adminTab)){
                String username = adminName.getText();
                String password = adminPass.getText();
                isValidRegistration = auth.processRegistration(username, password, true);
            }

            showAlert(isValidRegistration);

        } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // Show errors or alerts
    public void showAlert(LoginScenario scenario) {
        Alert alert;
        if (scenario != LoginScenario.REGISTRATION_SUCCESS){
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Something went wrong.");
        }
        else {
            alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Registration success.");
        }

        String message = switch (scenario) {
            case PASSWORD_EMPTY -> "Please enter a password.";
            case USERNAME_EMPTY -> "Please enter a username.";
            case USERNAME_AND_PASSWORD_EMPTY -> "Please enter a username and password.";
            case USERNAME_TAKEN -> "This username has been taken, please try a different one.";
            case USERNAME_NOT_FOUND -> "Login has failed, username does not exist.";
            case USERNAME_PASSWORD_MISMATCH -> "Login has failed, the username and password do not match.";
            case REGISTRATION_SUCCESS -> "Your account has been successfully registered.";
        };

        alert.setContentText(message);

        alert.showAndWait();

    }

    // Handle a change in the tab between admin and user
    @FXML
    private void handleTabSelectionChanged() {
        if (isInitialized) {
            currentTab = tabPane.getSelectionModel().getSelectedItem();
        }
    }
}

