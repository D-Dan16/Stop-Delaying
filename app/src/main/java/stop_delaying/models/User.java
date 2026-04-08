package stop_delaying.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    /**
     * The username of the user.
     */
    private String userName;

    /**
     * Represents the number of days in a row that a user has completed a task before the deadline.
     */
    private int dayStreak;

    /**
     * Represents the number of tasks in a row that a user has completed a task before the deadline.
     */
    private int taskStreak;

    /**
     * Represents whether the user has completed at least one task today.
     */
    private boolean hasCompletedTodayATask;

    public User() {
    }

    public User(String userName, int dayStreak, int taskStreak, boolean hasCompletedTodayATask) {
        this.userName = userName;
        this.dayStreak = dayStreak;
        this.taskStreak = taskStreak;
        this.hasCompletedTodayATask = hasCompletedTodayATask;
    }
}
