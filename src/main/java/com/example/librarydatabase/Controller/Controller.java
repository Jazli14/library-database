package com.example.librarydatabase.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class Controller {

    protected static Connection establishConnection() throws IOException, SQLException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(
                "src/main/java/com/example/librarydatabase/Controller/config.properties")) {
            props.load(fis);
        }

        String url = props.getProperty("dbUrl");
        String username = props.getProperty("dbUsername");
        String password = props.getProperty("dbPassword");

        return DriverManager.getConnection(url, username, password);
    }
}
