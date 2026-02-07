package stop_delaying.utils;

public final class FBBranches {
    public static final String EMAIL = "email";


    public static final String USERS = "users";
    public static class Users {
        public static final String DAY_STREAK = "dayStreak";
        public static final String TASK_STREAK = "taskStreak";
        public static final String USER_NAME = "userName";
    }

    public static final String TASKS = "tasks";
    public static class Tasks {
        public static final String TASK_ID = "taskId";
        public static final String TASK_NAME = "taskName";
        public static final String TASK_DESCRIPTION = "taskDescription";
        public static final String TASK_DUE_DATE = "taskDueDate";
        public static final String TASK_DUE_TIME_OF_DAY = "taskDueTimeOfDay";
        public static final String TASK_STATUS = "taskStatus";
        public static final String TASK_NOTIFYING = "taskNotifying";
    }
}