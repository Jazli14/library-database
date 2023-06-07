package com.example.librarydatabase.View;

import com.example.librarydatabase.Controller.Controller;
import com.example.librarydatabase.Model.Book;
import com.example.librarydatabase.Model.Loan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.*;

import java.io.IOException;

public class Scene {
    protected static FXMLLoader loadScene(Stage stage, String resource, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(Scene.class.getResource(resource));
        Parent root = loader.load();

        javafx.scene.Scene scene = new javafx.scene.Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        return loader;
    }

    protected void populateTableView(boolean bookOrLoan, TableView table, Controller controller, TableColumn category) {
        if (bookOrLoan){
            ObservableList<Book> bookList = FXCollections.observableArrayList();
            bookList.addAll(controller.library.getBooks().values());
            table.setItems(bookList);
        }
        else {
            ObservableList<Loan> loanList = FXCollections.observableArrayList();
            loanList.addAll(controller.library.getLoans().values());
            table.setItems(loanList);
        }
        table.getSortOrder().add(category);
        table.refresh();
    }
}
