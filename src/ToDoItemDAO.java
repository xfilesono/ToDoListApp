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

    public List<ToDoItem> getUnfinishedItems() throws SQLException {
        List<ToDoItem> items = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE done = false AND isDeleted = false";
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
                item.setDeleted(rs.getBoolean("isDeleted"));
                items.add(item);
            }
        }
        return items;
    }


    public List<ToDoItem> getFinishedItems() throws SQLException {
        List<ToDoItem> items = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE done = true";
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

    public void addItem(ToDoItem item) throws SQLException {
        String query = "INSERT INTO tasks (prioritise, description, whos_for, done, last_modified_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, item.getPrioritise());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getWhosFor());
            stmt.setBoolean(4, item.isDone());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getLastModifiedDate()));
            stmt.executeUpdate();
        }
    }

    public void deleteItem(int id) throws SQLException {
        String query = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void updateItem(ToDoItem item) throws SQLException {
        String query = "UPDATE tasks SET prioritise = ?, description = ?, whos_for = ?, done = ?, last_modified_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, item.getPrioritise());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getWhosFor());
            stmt.setBoolean(4, item.isDone());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getLastModifiedDate()));
            stmt.setInt(6, item.getId());
            stmt.executeUpdate();
        }
    }

    public ToDoItem getItemById(int id) throws SQLException {
        String query = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ToDoItem item = new ToDoItem();
                    item.setId(rs.getInt("id"));
                    item.setPrioritise(rs.getInt("prioritise"));
                    item.setDescription(rs.getString("description"));
                    item.setWhosFor(rs.getString("whos_for"));
                    item.setDone(rs.getBoolean("done"));
                    item.setLastModifiedDate(rs.getTimestamp("last_modified_date").toLocalDateTime());
                    return item;
                }
            }
        }
        return null; // Return null if no item is found
    }

    public List<ToDoItem> getArchivedItems() throws SQLException {
        List<ToDoItem> items = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE done = true OR isDeleted = true";
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
                item.setDeleted(rs.getBoolean("isDeleted"));
                items.add(item);
            }
        }
        return items;
    }

    public void markItemAsDeleted(int id) throws SQLException {
        String query = "UPDATE tasks SET isDeleted = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
