import java.time.LocalDateTime;

public class ToDoItem {
    private int id;
    private int prioritise;
    private String description;
    private String whosFor;
    private boolean done;
    private LocalDateTime lastModifiedDate;

    // Constructors
    public ToDoItem() {}

    public ToDoItem(int id, int prioritise, String description, String whosFor, boolean done, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.prioritise = prioritise;
        this.description = description;
        this.whosFor = whosFor;
        this.done = done;
        this.lastModifiedDate = lastModifiedDate;
    }

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
        if (prioritise < 1 || prioritise > 5) {
            throw new IllegalArgumentException("Prioritise must be between 1 and 5");
        }
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

    // Override toString method
    @Override
    public String toString() {
        return "ToDoItem{" +
                "id=" + id +
                ", prioritise=" + prioritise +
                ", description='" + description + '\'' +
                ", whosFor='" + whosFor + '\'' +
                ", done=" + done +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
