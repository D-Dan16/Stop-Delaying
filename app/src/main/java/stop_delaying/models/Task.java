package stop_delaying.models;

import androidx.cardview.widget.CardView;

public class Task {
    private String title;
    private String description;
    private Date dueDate;
    private TimeOfDay dueTimeOfDay;
    private TaskStatus status;
    private boolean isTaskSelected;

    private CardView refToView;

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
        this.isTaskSelected = false;
        this.refToView = null;
    }

    public CardView getRefToView() {
        return refToView;
    }

    public void setRefToView(CardView refToView) {
        this.refToView = refToView;
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

    public boolean isTaskSelected() {
        return isTaskSelected;
    }

    public void setTaskSelected(boolean taskSelected) {
        isTaskSelected = taskSelected;
    }
}