package stop_delaying.utils;

public final class FBBranches {
    public static final String EMAIL = "email";


    public static final String USERS = "users";
    public static class Users {
        public static final String DAY_STREAK = "day_streak";
        public static final String TASK_STREAK = "task_streak";
        public static final String USER_NAME = "username";
    }

    public static final String TASKS = "tasks";
    public static class Tasks {
        public static final String TASK_ID = "task_id";
        public static final String TASK_NAME = "task_title";
        public static final String TASK_DESCRIPTION = "task_description";
        public static final String TASK_DUE_DATE = "task_due_date";
        public static final String TASK_DUE_TIME_OF_DAY = "task_due_time_of_day";
        public static final String TASK_STATUS = "task_status";
        public static final String TASK_NOTIFYING = "task_notifying";
    }
}
