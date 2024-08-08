import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Callback; // Import the Callback class

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ToDoApp extends Application {
    private ToDoItemDAO toDoItemDAO = new ToDoItemDAO();
    private TableView<ToDoItem> tableView = new TableView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List");

        // Set up columns and data binding
        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));

        // Custom cell factory to format LocalDateTime
        lastModifiedDateColumn.setCellFactory(new Callback<TableColumn<ToDoItem, LocalDateTime>, TableCell<ToDoItem, LocalDateTime>>() {
            @Override
            public TableCell<ToDoItem, LocalDateTime> call(TableColumn<ToDoItem, LocalDateTime> param) {
                return new TableCell<ToDoItem, LocalDateTime>() {
                    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.format(formatter));
                        }
                    }
                };
            }
        });

        tableView.getColumns().addAll(idColumn, prioritiseColumn, descriptionColumn, whosForColumn, doneColumn, lastModifiedDateColumn);

        // Load unfinished tasks on startup
        refreshTableView();

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        Button archiveButton = new Button("Archive");

        // Set up button actions
        addButton.setOnAction(e -> openAddWindow());
        deleteButton.setOnAction(e -> openDeleteWindow());
        editButton.setOnAction(e -> openEditWindow());
        archiveButton.setOnAction(e -> openArchiveWindow());

        // Style buttons and arrange them in a single line at the bottom
        HBox buttonBox = new HBox(10, addButton, deleteButton, editButton, archiveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox vbox = new VBox(tableView, buttonBox);
        Scene scene = new Scene(vbox, 1200, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTableView() {
        try {
            List<ToDoItem> items = toDoItemDAO.getUnfinishedItems();
            tableView.getItems().setAll(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openAddWindow() {
        Stage addStage = new Stage();
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.setTitle("Add New Task");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label whosForLabel = new Label("Who's For:");
        TextField whosForField = new TextField();
        Label prioritiseLabel = new Label("Prioritise (1-5):");
        TextField prioritiseField = new TextField();
        Button saveButton = new Button("Save");

        saveButton.setOnAction(e -> {
            ToDoItem newItem = new ToDoItem();
            newItem.setPrioritise(Integer.parseInt(prioritiseField.getText()));
            newItem.setDescription(descriptionField.getText());
            newItem.setWhosFor(whosForField.getText());
            newItem.setDone(false);
            newItem.setLastModifiedDate(LocalDateTime.now());

            try {
                toDoItemDAO.addItem(newItem);
                refreshTableView();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            addStage.close();
        });

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(whosForLabel, 0, 1);
        grid.add(whosForField, 1, 1);
        grid.add(prioritiseLabel, 0, 2);
        grid.add(prioritiseField, 1, 2);
        grid.add(saveButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        addStage.setScene(scene);
        addStage.showAndWait();
    }

    private void openDeleteWindow() {
        Stage deleteStage = new Stage();
        deleteStage.initModality(Modality.APPLICATION_MODAL);
        deleteStage.setTitle("Delete Task");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label idLabel = new Label("Task ID:");
        TextField idField = new TextField();
        Button deleteButton = new Button("Delete");

        deleteButton.setOnAction(e -> {
            try {
                toDoItemDAO.markItemAsDeleted(Integer.parseInt(idField.getText()));
                refreshTableView();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            deleteStage.close();
        });

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(deleteButton, 1, 1);

        Scene scene = new Scene(grid, 300, 150);
        deleteStage.setScene(scene);
        deleteStage.showAndWait();
    }


    private void openEditWindow() {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Edit Task");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label idLabel = new Label("Task ID:");
        TextField idField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label whosForLabel = new Label("Who's For:");
        TextField whosForField = new TextField();
        Label prioritiseLabel = new Label("Prioritise (1-5):");
        TextField prioritiseField = new TextField();
        Label doneLabel = new Label("Done:");
        CheckBox doneCheckBox = new CheckBox();
        Button fetchButton = new Button("Fetch");
        Button saveButton = new Button("Save");

        fetchButton.setOnAction(e -> {
            try {
                ToDoItem item = toDoItemDAO.getItemById(Integer.parseInt(idField.getText()));
                if (item != null) {
                    descriptionField.setText(item.getDescription());
                    whosForField.setText(item.getWhosFor());
                    prioritiseField.setText(String.valueOf(item.getPrioritise()));
                    doneCheckBox.setSelected(item.isDone());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        saveButton.setOnAction(e -> {
            ToDoItem updatedItem = new ToDoItem();
            updatedItem.setId(Integer.parseInt(idField.getText()));
            updatedItem.setPrioritise(Integer.parseInt(prioritiseField.getText()));
            updatedItem.setDescription(descriptionField.getText());
            updatedItem.setWhosFor(whosForField.getText());
            updatedItem.setDone(doneCheckBox.isSelected());
            updatedItem.setLastModifiedDate(LocalDateTime.now());

            try {
                toDoItemDAO.updateItem(updatedItem);
                refreshTableView();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            editStage.close();
        });

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(fetchButton, 2, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(whosForLabel, 0, 2);
        grid.add(whosForField, 1, 2);
        grid.add(prioritiseLabel, 0, 3);
        grid.add(prioritiseField, 1, 3);
        grid.add(doneLabel, 0, 4);
        grid.add(doneCheckBox, 1, 4);
        grid.add(saveButton, 1, 5);

        Scene scene = new Scene(grid, 400, 300);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    private void openArchiveWindow() {
        Stage archiveStage = new Stage();
        archiveStage.initModality(Modality.APPLICATION_MODAL);
        archiveStage.setTitle("Archived Tasks");

        TableView<ToDoItem> archiveTableView = new TableView<>();

        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));

        // Custom cell factory to format LocalDateTime
        lastModifiedDateColumn.setCellFactory(new Callback<TableColumn<ToDoItem, LocalDateTime>, TableCell<ToDoItem, LocalDateTime>>() {
            @Override
            public TableCell<ToDoItem, LocalDateTime> call(TableColumn<ToDoItem, LocalDateTime> param) {
                return new TableCell<ToDoItem, LocalDateTime>() {
                    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.format(formatter));
                        }
                    }
                };
            }
        });

        archiveTableView.getColumns().addAll(idColumn, prioritiseColumn, descriptionColumn, whosForColumn, doneColumn, lastModifiedDateColumn);

        try {
            List<ToDoItem> archivedItems = toDoItemDAO.getArchivedItems();
            archiveTableView.getItems().setAll(archivedItems);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        VBox vbox = new VBox(archiveTableView);
        Scene scene = new Scene(vbox, 800, 600);
        archiveStage.setScene(scene);
        archiveStage.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
