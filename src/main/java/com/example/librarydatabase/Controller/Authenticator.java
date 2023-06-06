package com.example.librarydatabase.Controller;

import com.example.librarydatabase.Model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class Authenticator extends Controller implements Login {
    private AccountList accList;
    public Authenticator(){
        // Want to initialize the AccountList to the one on the server, not a blank one
        accList = new AccountList();
        populateAccountList();
    }
    @Override
    public boolean processLogin(String username, String password, boolean admin){
        Member returningMember = accList.getMember(username);
        if (returningMember != null){
            if (returningMember.validPassword(password) && (admin == returningMember.getRole())){
                System.out.println("WELCOME BACK " + returningMember.getUsername());
                return true;
            }
            else {
                System.out.println("???? what happened");
                return false;
            }
        }
        else {
            System.out.println("NO ONE'S NAMED THAT DUMMY");
            return false;
        }
    }

    public AccountList getAccList(){
        return accList;
    }
    public boolean processRegistration(String username, String password, boolean isAdmin){
        boolean isValid = !accList.memberExists(username) && !(username.isEmpty() || password.isEmpty());

        if (isAdmin && isValid){
            Admin newAdmin = new Admin(username, password);
            accList.add(newAdmin);
            updateDatabase(newAdmin, password);
            System.out.println("ADDED NEW Admin: " + newAdmin.getUsername());
        }
        else if (isValid) {
            User newUser = new User(username, password);
            accList.add(newUser);
            updateDatabase(newUser, password);
            System.out.println("ADDED NEW User: " + newUser.getUsername());
        }
        else {
            System.out.println("Choose a new username");
            return false;
        }
        return true;
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
