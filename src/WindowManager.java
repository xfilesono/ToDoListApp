import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Label johnsonBrothersStatus;
    private Label johnsonsCoffeeStatus;
    private Label urlString1;
    private Label urlString2;
    private Image upImage;
    private Image downImage;
    private String url1 = "http://www.johnsonbrothers.co.uk/";
    private String url2 = "https://www.johnsonscoffee.com/";
    // Add other status labels as needed

    public WindowManager(ToDoItemDAO toDoItemDAO, TableView<ToDoItem> tableView, Label johnsonBrothersStatus, Label johnsonsCoffeeStatus, Label urlString1, Label urlString2) {
        this.toDoItemDAO = toDoItemDAO;
        this.tableView = tableView;
        this.johnsonBrothersStatus = johnsonBrothersStatus;
        this.johnsonsCoffeeStatus = johnsonsCoffeeStatus;
        this.urlString1 = urlString1;
        this.urlString2 = urlString2;
        this.upImage = new Image(getClass().getResourceAsStream("/resources/upx.png"));
        this.downImage = new Image(getClass().getResourceAsStream("/resources/downx.png"));
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
        Label whosForLabel = new Label("Related to:");
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

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();
        Button fetchButton = new Button("Fetch");

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label whosForLabel = new Label("Related to:");
        TextField whosForField = new TextField();
        Label prioritiseLabel = new Label("Prioritise (1-5):");
        TextField prioritiseField = new TextField();
        Label doneLabel = new Label("Done:");
        CheckBox doneCheckBox = new CheckBox();

        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        fetchButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                ToDoItem fetchedItem = toDoItemDAO.getItemById(id);
                if (fetchedItem != null) {
                    descriptionField.setText(fetchedItem.getDescription());
                    whosForField.setText(fetchedItem.getWhosFor());
                    prioritiseField.setText(String.valueOf(fetchedItem.getPrioritise()));
                    doneCheckBox.setSelected(fetchedItem.isDone());
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
        grid.add(saveButton, 3, 1);
        grid.add(closeButton, 3, 2);

        Scene scene = new Scene(grid, 400, 250);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    public void openEditWindow(ToDoItem selectedItem) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Edit Task");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label idLabel = new Label("ID:");
        TextField idField = new TextField(String.valueOf(selectedItem.getId()));
        idField.setDisable(true); // Disable editing of ID field

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField(selectedItem.getDescription());
        Label whosForLabel = new Label("Related to:");
        TextField whosForField = new TextField(selectedItem.getWhosFor());
        Label prioritiseLabel = new Label("Prioritise (1-5):");
        TextField prioritiseField = new TextField(String.valueOf(selectedItem.getPrioritise()));
        Label doneLabel = new Label("Done:");
        CheckBox doneCheckBox = new CheckBox();
        doneCheckBox.setSelected(selectedItem.isDone());

        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        saveButton.setOnAction(e -> {
            selectedItem.setPrioritise(Integer.parseInt(prioritiseField.getText()));
            selectedItem.setDescription(descriptionField.getText());
            selectedItem.setWhosFor(whosForField.getText());
            selectedItem.setDone(doneCheckBox.isSelected());
            selectedItem.setLastModifiedDate(LocalDateTime.now());
            try {
                toDoItemDAO.updateItem(selectedItem);
                refreshTableView();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            editStage.close();
        });

        closeButton.setOnAction(e -> editStage.close());

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(whosForLabel, 0, 2);
        grid.add(whosForField, 1, 2);
        grid.add(prioritiseLabel, 0, 3);
        grid.add(prioritiseField, 1, 3);
        grid.add(doneLabel, 0, 4);
        grid.add(doneCheckBox, 1, 4);
        grid.add(saveButton, 3, 1);
        grid.add(closeButton, 3, 2);

        Scene scene = new Scene(grid, 400, 250);
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
        prioritiseColumn.setCellFactory(tc -> new TableCell<ToDoItem, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String text;
                    String color;
                    switch (item) {
                        case 5:
                            text = "Future";
                            color = "#ADD8E6"; // Light blue
                            break;
                        case 4:
                            text = "Low";
                            color = "#90EE90"; // Light green
                            break;
                        case 3:
                            text = "Medium";
                            color = "#FFFF00"; // Yellow
                            break;
                        case 2:
                            text = "High";
                            color = "#FFA500"; // Orange
                            break;
                        case 1:
                            text = "Urgent";
                            color = "#FF4500"; // Red
                            break;
                        default:
                            text = "Unknown";
                            color = ""; // White
                            break;
                    }
                    setText(text);
                    setStyle("-fx-background-color: " + color + "; -fx-font-family: 'Garamond'; -fx-font-size: 18px; -fx-alignment: center;");
                }
            }
        });
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

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Related to");
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

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            ToDoItem selectedItem = archiveTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                openEditWindow(selectedItem);
            } else {
                openEditWindow();
            }
        });

        // Add mouse click event handler to clear selection on double-click
        archiveTableView.setRowFactory(tv -> {
            TableRow<ToDoItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    archiveTableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });

        VBox vbox = new VBox(10, archiveTableView, editButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 1000, 450);
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
                checkWebsiteStatus(url1, johnsonBrothersStatus);
                checkWebsiteStatus(url2, johnsonsCoffeeStatus);
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
                updateStatusLabel(statusLabel, upImage);
            } else {
                updateStatusLabel(statusLabel, downImage);
            }
        } catch (Exception e) {
            updateStatusLabel(statusLabel, downImage);
        }
    }

    private void updateStatusLabel(Label statusLabel, Image image) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setGraphic(new ImageView(image));
        });
    }
}
