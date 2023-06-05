module com.example.librarydatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.example.librarydatabase to javafx.fxml;
    exports com.example.librarydatabase;
    exports com.example.librarydatabase.View;
    opens com.example.librarydatabase.View to javafx.fxml;
    exports com.example.librarydatabase.Controller;
    opens com.example.librarydatabase.Controller to javafx.fxml;
}