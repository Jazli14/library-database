module com.library_database.library_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.library_database.library_app to javafx.fxml;
    opens com.library_database.library_app.view to javafx.fxml;
    exports com.library_database.library_app;
    exports com.library_database.library_app.view;
    exports com.library_database.library_app.controller;
    opens com.library_database.library_app.controller to javafx.fxml;
    exports com.library_database.library_app.model;
}