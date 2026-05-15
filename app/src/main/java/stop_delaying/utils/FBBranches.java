package stop_delaying.utils;

/**
 * Constants representing the Firebase Realtime Database structure.
 * Organizes path strings for users, tasks, and their respective properties.
 */
public final class FBBranches {
    public static final String EMAIL = "email";


    public static final String USERS = "users";

    /**
     * Database keys associated with user profile and streak information.
     */
    public static class Users {
        public static final String DAY_STREAK = "dayStreak";
        public static final String TASK_STREAK = "taskStreak";
        public static final String USER_NAME = "userName";
        public static final String HAS_COMPLETED_TODAY_A_TASK = "hasCompletedTodayATask";
    }

    public static final String TASKS = "tasks";

    /**
     * Database keys for task-related data fields.
     */
    private static class Tasks {
        public static final String TASK_ID = "taskId";
        public static final String TASK_NAME = "taskName";
        public static final String TASK_DESCRIPTION = "taskDescription";
        public static final String TASK_DUE_DATE = "taskDueDate";
        public static final String TASK_DUE_TIME_OF_DAY = "taskDueTimeOfDay";
        public static final String TASK_STATUS = "taskStatus";
        public static final String TASK_NOTIFYING = "taskNotifying";
    }
}