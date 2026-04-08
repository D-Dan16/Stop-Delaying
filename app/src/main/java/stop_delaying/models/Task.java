package stop_delaying.models;

import com.google.firebase.database.Exclude;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class Task {
    @Exclude private String taskId;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private Date dueDate;
    @Getter
    @Setter
    private TimeOfDay dueTimeOfDay;
    @Getter
    @Setter
    private TaskStatus status;
    @Exclude private boolean isTaskSelected;
    @Getter
    @Setter
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

    @Exclude public boolean isTaskSelected() {
        return isTaskSelected;
    }

    @Exclude public void setTaskSelected(boolean taskSelected) {
        isTaskSelected = taskSelected;
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