module com.library_database.library_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.library_database.library_app to javafx.fxml;
    opens com.library_database.library_app.View to javafx.fxml;
    exports com.library_database.library_app;
    exports com.library_database.library_app.View;
    exports com.library_database.library_app.Controller;
    opens com.library_database.library_app.Controller to javafx.fxml;
    exports com.library_database.library_app.Model;
}