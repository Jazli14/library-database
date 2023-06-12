package com.library_database.library_app.View;

import com.library_database.library_app.Controller.MasterController;
import com.library_database.library_app.Model.*;
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

    protected void populateTableView(int typeOfClass, TableView table, MasterController controller, TableColumn category) {
        switch (typeOfClass) {
            case 0 -> {
                ObservableList<Book> bookList = FXCollections.observableArrayList();
                bookList.addAll(controller.library.getBooks().values());
                table.setItems(bookList);
            }
            case 1 -> {
                ObservableList<Loan> loanList = FXCollections.observableArrayList();
                loanList.addAll(controller.library.getLoans().values());
                table.setItems(loanList);
            }
            case 2 -> {
                ObservableList<Account> accountList = FXCollections.observableArrayList();
                accountList.addAll(controller.library.getAccounts().values());
                table.setItems(accountList);
            }
        }
        table.getSortOrder().add(category);
        table.refresh();
    }
}
