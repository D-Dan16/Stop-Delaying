package stop_delaying.models;

public class Task {
    private String title;
    private String description;
    private Date dueDate;
    private TimeOfDay dueTimeOfDay;
    private TaskStatus status;

    public enum TaskStatus {
        TODO,
        COMPLETED,
        CANCELED
    }

    public Task(String title, String description, Date dueDate, TimeOfDay dueTimeOfDay, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTimeOfDay = dueTimeOfDay;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TimeOfDay getDueTimeOfDay() {
        return dueTimeOfDay;
    }

    public void setDueTimeOfDay(TimeOfDay dueTimeOfDay) {
        this.dueTimeOfDay = dueTimeOfDay;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}