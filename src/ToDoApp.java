import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.SQLException;

public class ToDoApp extends Application {
    private ToDoItemDAO toDoItemDAO = new ToDoItemDAO();
    private TableView<ToDoItem> tableView = new TableView<>();
    private WindowManager windowManager;
    private Label johnsonBrothersStatus = new Label();
    private Label johnsonsCoffeeStatus = new Label();
    // Add other status labels as needed

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TooDoo List");

        windowManager = new WindowManager(toDoItemDAO, tableView);

        // Set up columns and data binding
        TableColumn<ToDoItem, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f2d7d5; -fx-font-size: 18px; -fx-alignment: center;");
        idColumn.setPrefWidth(40);

        TableColumn<ToDoItem, Integer> prioritiseColumn = new TableColumn<>("Priority");
        prioritiseColumn.setCellValueFactory(new PropertyValueFactory<>("prioritise"));
        prioritiseColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f8fa9d; -fx-font-size: 18px; -fx-alignment: center;");
        prioritiseColumn.setPrefWidth(85);

        TableColumn<ToDoItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #d1f2eb; -fx-font-size: 18px;");
        descriptionColumn.setPrefWidth(780);
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
        whosForColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #fcf3cf; -fx-font-size: 18px;");
        whosForColumn.setPrefWidth(120);

        TableColumn<ToDoItem, LocalDateTime> lastModifiedDateColumn = new TableColumn<>("Last Modified Date");
        lastModifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));
        lastModifiedDateColumn.setStyle("-fx-font-family: 'Garamond'; -fx-color: #f9bee9; -fx-font-size: 18px; -fx-alignment: center;");
        lastModifiedDateColumn.setPrefWidth(170);
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

        tableView.getColumns().addAll(idColumn, prioritiseColumn, descriptionColumn, whosForColumn, lastModifiedDateColumn);
        //tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load unfinished tasks on startup
        refreshTableView();

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button archiveButton = new Button("Archive");

        // Set up button actions
        addButton.setOnAction(e -> windowManager.openAddWindow());
        editButton.setOnAction(e -> windowManager.openEditWindow());
        archiveButton.setOnAction(e -> windowManager.openArchiveWindow());

        // Style buttons and arrange them in a single line at the bottom
        HBox buttonBox = new HBox(10, addButton, editButton, archiveButton);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-font-family: 'Garamond'; -fx-font-size: 18px; -fx-background-color: #c6f3ef;");

        VBox vbox = new VBox(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS); // Make the table view grow to fill available space

        // Add logo placeholder
        Image logoImage = new Image(getClass().getResourceAsStream("/resources/3.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100);
        logoImageView.setPreserveRatio(true);
        logoImageView.setStyle("-fx-background-color: #ADD8E6;"); // Light blue background

        HBox logoBox = new HBox(logoImageView, johnsonBrothersStatus, johnsonsCoffeeStatus); // Add other status labels
        logoBox.setAlignment(Pos.TOP_CENTER);
        logoBox.setPadding(new Insets(10));

        VBox mainBox = new VBox(logoBox, vbox, buttonBox);
        Scene scene = new Scene(mainBox, 1200, 540);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/3b.png")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Lock window size
        primaryStage.show();
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

    public static void main(String[] args) {
        launch(args);
    }
}
