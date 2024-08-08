import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WindowManager {
    private ToDoItemDAO toDoItemDAO;
    private TableView<ToDoItem> tableView;

    public WindowManager(ToDoItemDAO toDoItemDAO, TableView<ToDoItem> tableView) {
        this.toDoItemDAO = toDoItemDAO;
        this.tableView = tableView;
    }

    public void openAddWindow() {
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
        Button closeButton = new Button("Close");

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

        closeButton.setOnAction(e -> addStage.close());

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(whosForLabel, 0, 1);
        grid.add(whosForField, 1, 1);
        grid.add(prioritiseLabel, 0, 2);
        grid.add(prioritiseField, 1, 2);
        grid.add(saveButton, 1, 3);
        grid.add(closeButton, 2, 3);

        Scene scene = new Scene(grid, 300, 200);
        addStage.setScene(scene);
        addStage.showAndWait();
    }

    public void openDeleteWindow() {
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
        Button closeButton = new Button("Close");

        deleteButton.setOnAction(e -> {
            try {
                toDoItemDAO.markItemAsDeleted(Integer.parseInt(idField.getText()));
                refreshTableView();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            deleteStage.close();
        });

        closeButton.setOnAction(e -> deleteStage.close());

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(deleteButton, 1, 1);
        grid.add(closeButton, 2, 1);

        Scene scene = new Scene(grid, 300, 150);
        deleteStage.setScene(scene);
        deleteStage.showAndWait();
    }

    public void openEditWindow() {
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
        Button closeButton = new Button("Close");

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

        closeButton.setOnAction(e -> editStage.close());

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
        grid.add(closeButton, 2, 5);

        Scene scene = new Scene(grid, 400, 300);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    public void openArchiveWindow() {
        Stage archiveStage = new Stage();
        archiveStage.initModality(Modality.APPLICATION_MODAL);
        archiveStage.setTitle("Archived Tasks");

        TableView<ToDoItem> archiveTableView = new TableView<>();

        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px;");

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));
        prioritiseColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px;");

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 16px;");

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));
        whosForColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px; -fx-background-color: lightblue;");

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));
        doneColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px;");

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        lastModifiedDateColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px;");

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


    private void refreshTableView() {
        try {
            List<ToDoItem> items = toDoItemDAO.getUnfinishedItems();
            tableView.getItems().setAll(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
