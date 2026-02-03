package stop_delaying.models;

import com.google.firebase.database.Exclude;

import java.util.UUID;

public class Task {
    @Exclude private String taskId;
    private String title;
    private String description;
    private Date dueDate;
    private TimeOfDay dueTimeOfDay;
    private TaskStatus status;
    @Exclude private boolean isTaskSelected;
    private boolean isTaskNotifying;

    public enum TaskStatus {
        TODO,
        COMPLETED,
        CANCELED
    }

    public Task() {
    }

    public Task(String title, String description, Date dueDate, TimeOfDay dueTimeOfDay, TaskStatus status) {
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTimeOfDay = dueTimeOfDay;
        this.status = status;
        this.isTaskSelected = false;
        this.isTaskNotifying = false;
    }

    @Exclude public String getTaskId() {
        return taskId;
    }

    @Exclude public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    @Exclude public boolean isTaskSelected() {
        return isTaskSelected;
    }

    @Exclude public void setTaskSelected(boolean taskSelected) {
        isTaskSelected = taskSelected;
    }

    public boolean isTaskNotifying() {
        return isTaskNotifying;
    }
    public void setTaskNotifying(boolean taskNotifying) {
        isTaskNotifying = taskNotifying;
    }


    @Exclude public boolean isDeadlineNear() {
        long timeLeftUntilDeadline = dueDate.calcTimeUntil() + dueTimeOfDay.calcTimeUntil();
        return timeLeftUntilDeadline <= 24 * 3600L;
    }

    @Exclude public boolean hasReachedDeadline() {
        long timeLeftUntilDeadline = dueDate.calcTimeUntil() + dueTimeOfDay.calcTimeUntil();
        return timeLeftUntilDeadline <= 0;
    }
}