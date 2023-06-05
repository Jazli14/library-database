package com.example.librarydatabase;

import com.example.librarydatabase.Model.*;

import java.sql.*;


public class Authenticator {
    private AccountList accList;
    public Authenticator(){
        // Want to initialize the AccountList to the one on the server, not a blank one
        accList = new AccountList();
        populateAccountList();
    }
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

        String url = "jdbc:postgresql://ruby.db.elephantsql.com/agclswdr";
        String username = "agclswdr";
        String password = "JhvkCaxHwI44WdwbFtNo3GMpyg66xwVR";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
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
        } catch (SQLException e) {
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

        String url = "jdbc:postgresql://ruby.db.elephantsql.com/agclswdr";
        String dbUsername = "agclswdr";
        String dbPassword = "JhvkCaxHwI44WdwbFtNo3GMpyg66xwVR";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {

            String insertSQL = "INSERT INTO accounts (username, password, admin) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, member.getUsername());
                statement.setString(2, password);
                statement.setBoolean(3, member.getRole());
                statement.executeUpdate();

            }
        } catch (SQLException e) {
                e.printStackTrace();
        }

}


}
