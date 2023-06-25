package com.library_database.library_app.view;

import com.library_database.library_app.controller.MasterController;
import com.library_database.library_app.model.*;
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

    // Populate the given table and sort by the category
    protected void populateTableView(int typeOfClass, TableView table, MasterController controller, TableColumn category) {
        switch (typeOfClass) {
            case 0 -> { // The book table
                ObservableList<Book> bookList = FXCollections.observableArrayList();
                bookList.addAll(controller.library.getBooks().values());
                table.setItems(bookList);
            }
            case 1 -> { // The loan table
                ObservableList<Loan> loanList = FXCollections.observableArrayList();
                loanList.addAll(controller.library.getLoans().values());
                table.setItems(loanList);
            }
            case 2 -> { // The account table
                ObservableList<Account> accountList = FXCollections.observableArrayList();
                accountList.addAll(controller.library.getAccounts().values());
                table.setItems(accountList);
            }
        }
        table.getSortOrder().add(category);
        table.refresh();
    }
}
