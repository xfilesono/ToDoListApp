import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoItemDAO {
    public List<ToDoItem> getAllItems() throws SQLException {
        List<ToDoItem> items = new ArrayList<>();
        String query = "SELECT * FROM tasks";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ToDoItem item = new ToDoItem();
                item.setId(rs.getInt("id"));
                item.setPrioritise(rs.getInt("prioritise"));
                item.setDescription(rs.getString("description"));
                item.setWhosFor(rs.getString("whos_for"));
                item.setDone(rs.getBoolean("done"));
                item.setLastModifiedDate(rs.getTimestamp("last_modified_date").toLocalDateTime());
                items.add(item);
            }
        }
        return items;
    }

    // Methods for add, delete, edit, and mark as done
}
