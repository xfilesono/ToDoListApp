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
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WindowManager {
    private ToDoItemDAO toDoItemDAO;
    private TableView<ToDoItem> tableView;
    private Label johnsonBrothersStatus = new Label();
    private Label johnsonsCoffeeStatus = new Label();
    // Add other status labels as needed

    public WindowManager(ToDoItemDAO toDoItemDAO, TableView<ToDoItem> tableView) {
        this.toDoItemDAO = toDoItemDAO;
        this.tableView = tableView;
        startPingService();
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
        grid.add(saveButton, 3, 1);
        grid.add(closeButton, 3, 2);

        Scene scene = new Scene(grid, 370, 150);
        addStage.setScene(scene);
        addStage.showAndWait();
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

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label whosForLabel = new Label("Who's For:");
        TextField whosForField = new TextField();
        Label prioritiseLabel = new Label("Prioritise (1-5):");
        TextField prioritiseField = new TextField();
        Label doneLabel = new Label("Done:");
        CheckBox doneCheckBox = new CheckBox();
        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        ToDoItem selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            descriptionField.setText(selectedItem.getDescription());
            whosForField.setText(selectedItem.getWhosFor());
            prioritiseField.setText(String.valueOf(selectedItem.getPrioritise()));
            doneCheckBox.setSelected(selectedItem.isDone());
        }

        saveButton.setOnAction(e -> {
            ToDoItem updatedItem = new ToDoItem();
            updatedItem.setId(selectedItem.getId());
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

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(whosForLabel, 0, 1);
        grid.add(whosForField, 1, 1);
        grid.add(prioritiseLabel, 0, 2);
        grid.add(prioritiseField, 1, 2);
        grid.add(doneLabel, 0, 3);
        grid.add(doneCheckBox, 1, 3);
        grid.add(saveButton, 3, 1);
        grid.add(closeButton, 3, 2);

        Scene scene = new Scene(grid, 400, 230);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    public void openArchiveWindow() {
        Stage archiveStage = new Stage();
        archiveStage.setResizable(false);
        archiveStage.initModality(Modality.APPLICATION_MODAL);
        archiveStage.setTitle("Archived Tasks");

        TableView<ToDoItem> archiveTableView = new TableView<>();

        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f2d7d5; -fx-font-size: 14px; -fx-alignment: center;");
        idColumn.setPrefWidth(50);

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));
        prioritiseColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f8fa9d; -fx-font-size: 14px; -fx-alignment: center;");
        prioritiseColumn.setPrefWidth(100);

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #d1f2eb; -fx-font-size: 16px;");
        descriptionColumn.setPrefWidth(535);
        descriptionColumn.setCellFactory(tc -> {
            TableCell<ToDoItem, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));
        whosForColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #fcf3cf; -fx-font-size: 16px;");
        whosForColumn.setPrefWidth(120);

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));
        doneColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #d4e6f1; -fx-font-size: 14px; -fx-alignment: center;");
        doneColumn.setPrefWidth(60);

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        lastModifiedDateColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f9bee9; -fx-font-size: 14px; -fx-alignment: center;");
        lastModifiedDateColumn.setPrefWidth(150);
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
            archivedItems.sort((a, b) -> b.getLastModifiedDate().compareTo(a.getLastModifiedDate()));
            archiveTableView.getItems().setAll(archivedItems);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        VBox vbox = new VBox(archiveTableView);
        Scene scene = new Scene(vbox, 1000, 400);
        archiveStage.setScene(scene);
        archiveStage.showAndWait();
    }

    private void refreshTableView() {
        try {
            List<ToDoItem> items = toDoItemDAO.getUnfinishedItems();
            items.sort((a, b) -> b.getLastModifiedDate().compareTo(a.getLastModifiedDate()));
            tableView.getItems().setAll(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void startPingService() {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkWebsiteStatus("http://www.johnsonbrothers.co.uk/", johnsonBrothersStatus);
                checkWebsiteStatus("https://www.johnsonscoffee.com/", johnsonsCoffeeStatus);
                // Add other websites similarly
            }
        };
        timer.scheduleAtFixedRate(task, 0, 10000); // Ping every 10 seconds
    }

    private void checkWebsiteStatus(String urlString, Label statusLabel) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                statusLabel.setStyle("-fx-background-color: green;");
            } else {
                statusLabel.setStyle("-fx-background-color: red;");
            }
        } catch (Exception e) {
            statusLabel.setStyle("-fx-background-color: red;");
        }
    }
}