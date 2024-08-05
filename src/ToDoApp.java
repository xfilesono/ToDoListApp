import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class ToDoApp extends Application {
    private ToDoItemDAO toDoItemDAO = new ToDoItemDAO();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List");

        TableView<ToDoItem> tableView = new TableView<>();
        // Set up columns and data binding

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        Button doneButton = new Button("Done");

        // Set up button actions

        VBox vbox = new VBox(tableView, addButton, deleteButton, editButton, doneButton);
        Scene scene = new Scene(vbox, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        TableColumn<ToDoItem, String> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));

        tableView.getColumns().addAll(prioritiseColumn, descriptionColumn, whosForColumn, doneColumn, lastModifiedDateColumn);

        addButton.setOnAction(e -> {
            // Code to add a new item
        });

        deleteButton.setOnAction(e -> {
            // Code to delete the selected item
        });

        editButton.setOnAction(e -> {
            // Code to edit the selected item
        });

        doneButton.setOnAction(e -> {
            // Code to mark the selected item as done
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
