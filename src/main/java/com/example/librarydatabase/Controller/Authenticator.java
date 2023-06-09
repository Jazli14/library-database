package com.example.librarydatabase.Controller;

import com.example.librarydatabase.Model.*;

import java.io.IOException;
import java.sql.*;


public class Authenticator extends MasterController implements Login {
    private final AccountList accList;
    public Authenticator(){
        // Want to initialize the AccountList to the one on the server, not a blank one
        accList = new AccountList();
        populateAccountList();
    }
    @Override
    public LoginScenario processLogin(String username, String password, boolean admin){
        Member returningMember = accList.getMember(username);
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();
        // the user exists
        if (usernameEmpty && passwordEmpty){
            return LoginScenario.USERNAME_AND_PASSWORD_EMPTY;
        }
        else if (usernameEmpty) {
            return LoginScenario.USERNAME_EMPTY;
        }
        else if (passwordEmpty){
            return LoginScenario.PASSWORD_EMPTY;
        }

        if (returningMember != null){
            if ((admin == returningMember.getRole())){
                if (returningMember.validPassword(password)){
                    System.out.println("Welcome back " + returningMember.getUsername());
                    return null;
                }
                else {
                    return LoginScenario.USERNAME_PASSWORD_MISMATCH;
                }
            }
        }
        return LoginScenario.USERNAME_NOT_FOUND;
    }

    public AccountList getAccList(){
        return accList;
    }
    public LoginScenario processRegistration(String username, String password, boolean isAdmin){
        boolean usernameTaken = accList.memberExists(username);
        boolean usernameEmpty = username.isEmpty();
        boolean passwordEmpty = password.isEmpty();

        if (isAdmin && !usernameTaken && !usernameEmpty && !passwordEmpty){
            Admin newAdmin = new Admin(username, password);
            accList.add(newAdmin);
            updateDatabase(newAdmin, password);
        }
        else if (!usernameTaken && !usernameEmpty && !passwordEmpty) {
            User newUser = new User(username, password);
            accList.add(newUser);
            updateDatabase(newUser, password);
        }
        else if (usernameEmpty && passwordEmpty){
            return LoginScenario.USERNAME_AND_PASSWORD_EMPTY;
        }
        else if (usernameEmpty){
            return LoginScenario.USERNAME_EMPTY;
        }
        else if (passwordEmpty){
            return LoginScenario.PASSWORD_EMPTY;
        }
        else {
            return LoginScenario.USERNAME_TAKEN;
        }
        return LoginScenario.REGISTRATION_SUCCESS;
    }

    private void populateAccountList(){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {
            String selectSQL = "SELECT username, password, admin FROM accounts";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String newUsername = resultSet.getString("username");
                        String newPassword = resultSet.getString("password");
                        boolean isAdmin = resultSet.getBoolean("admin");
                        if (isAdmin){
                            Admin member = new Admin(newUsername, newPassword);
                            accList.add(member);
                        }
                        else {
                            User member = new User(newUsername, newPassword);
                            accList.add(member);
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private void updateDatabase(Member member, String password){
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = establishConnection()) {

            String insertSQL = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, member.getUsername());
                statement.setString(2, password);
                statement.setBoolean(3, member.getRole());
                statement.executeUpdate();

            }
        } catch (SQLException | IOException e) {
                e.printStackTrace();
        }
    }

//    private static Connection establishConnection() throws IOException, SQLException {
//        Properties props = new Properties();
//        try (FileInputStream fis = new FileInputStream(
//                "src/main/java/com/example/librarydatabase/Controller/config.properties")) {
//            props.load(fis);
//        }
//
//        String url = props.getProperty("dbUrl");
//        String username = props.getProperty("dbUsername");
//        String password = props.getProperty("dbPassword");
//
//        return DriverManager.getConnection(url, username, password);
//    }


}
