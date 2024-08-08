import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.SQLException;

public class ToDoApp extends Application {
    private ToDoItemDAO toDoItemDAO = new ToDoItemDAO();
    private TableView<ToDoItem> tableView = new TableView<>();
    private WindowManager windowManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TooDoo");

        windowManager = new WindowManager(toDoItemDAO, tableView);

        // Set up columns and data binding
        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Prioritise");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));
        prioritiseColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

        TableColumn<ToDoItem, String> whosForColumn = new TableColumn<>("Who's For");
        whosForColumn.setCellValueFactory(new PropertyValueFactory<>("whosFor"));
        whosForColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

        TableColumn<ToDoItem, Boolean> doneColumn = new TableColumn<>("Done");
        doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));
        doneColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        lastModifiedDateColumn.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px;");

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
        addButton.setOnAction(e -> windowManager.openAddWindow());
        deleteButton.setOnAction(e -> windowManager.openDeleteWindow());
        editButton.setOnAction(e -> windowManager.openEditWindow());
        archiveButton.setOnAction(e -> windowManager.openArchiveWindow());

        // Style buttons and arrange them in a single line at the bottom
        HBox buttonBox = new HBox(10, addButton, deleteButton, editButton, archiveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px; -fx-background-color: lightgray;");

        VBox vbox = new VBox(tableView, buttonBox);
        VBox.setVgrow(tableView, Priority.ALWAYS); // Make the table view grow to fill available space

        // Add logo placeholder
        Label logoPlaceholder = new Label("Logo Here");
        logoPlaceholder.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 20px;");
        HBox logoBox = new HBox(logoPlaceholder);
        logoBox.setAlignment(Pos.TOP_LEFT);
        logoBox.setPadding(new Insets(10));

        VBox mainBox = new VBox(logoBox, vbox);
        Scene scene = new Scene(mainBox, 1200, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Add footer text
        Label footer = new Label("CapsCode Inc.");
        footer.setAlignment(Pos.BOTTOM_LEFT);
        footer.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 14px;");
        vbox.getChildren().add(footer);
    }

    private void refreshTableView() {
        try {
            List<ToDoItem> items = toDoItemDAO.getUnfinishedItems();
            tableView.getItems().setAll(items);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
