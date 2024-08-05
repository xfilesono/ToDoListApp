import java.time.LocalDateTime;

public class ToDoItem {
    private int id;
    private int prioritise;
    private String description;
    private String whosFor;
    private boolean done;
    private LocalDateTime lastModifiedDate;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrioritise() {
        return prioritise;
    }

    public void setPrioritise(int prioritise) {
        this.prioritise = prioritise;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWhosFor() {
        return whosFor;
    }

    public void setWhosFor(String whosFor) {
        this.whosFor = whosFor;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
