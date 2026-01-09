package stop_delaying.models;

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

    public User() {
    }

    public User(String userName, int dayStreak, int taskStreak) {
        this.userName = userName;
        this.dayStreak = dayStreak;
        this.taskStreak = taskStreak;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getDayStreak() {
        return dayStreak;
    }

    public void setDayStreak(int dayStreak) {
        this.dayStreak = dayStreak;
    }

    public int getTaskStreak() {
        return taskStreak;
    }

    public void setTaskStreak(int taskStreak) {
        this.taskStreak = taskStreak;
    }
}
