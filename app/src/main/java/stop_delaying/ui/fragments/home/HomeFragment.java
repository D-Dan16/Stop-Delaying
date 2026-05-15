package stop_delaying.ui.fragments.home;

import static stop_delaying.utils.Utils.speak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.procrastination.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import stop_delaying.models.Task;
import stop_delaying.models.User;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.LeaderboardViewModel;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository;
import stop_delaying.ui.fragments.tasks.task_handlers.Tasks;
import stop_delaying.ui.fragments.tasks.task_handlers.TasksViewModel;

/**
 * The landing fragment of the application. Displays the user's most urgent task, 
 * current streaks, and provides navigation to educational resources.
 */
@SuppressLint("SetTextI18n")
public class HomeFragment extends Fragment {

    private OnHomeFragmentInteractionListener onHomeFragmentInteractionListener;
    private TasksViewModel tasksViewModel;
    private LeaderboardViewModel leaderboardViewModel;

    // UI elements for important task display
    private TextView tvImportantTaskTitle;
    private TextView tvImportantTaskDescription;
    private TextView tvImportantTaskDueDate;
    private TextView tvImportantTaskDueTime;
    private View importantTaskCardView;

    // UI elements for streaks display
    private TextView tvTasksStreak;
    private TextView tvDaysStreak;

    /** The task currently identified as the most urgent for display. */
    private Task currentUrgentTask;

    /** Handler for managing periodic UI updates, such as urgent task refreshing. */
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    /** 
     * Runnable that periodically refreshes the urgent task display to ensure 
     * deadlines and visual indicators remain accurate.
     */
    private final Runnable urgentTaskRefreshRunnable = new Runnable() {
        @Override public void run() {
            if (tasksViewModel != null && tasksViewModel.getTasks() != null)
                displayUrgentTask(tasksViewModel.getTasks());

            handler.postDelayed(this, 60000); // refresh every minute for deadline updates
        }
    };

    private static final String TAG = "HomeFragment";

    /**
     * Interface to handle navigation events from the home screen to other fragments.
     */
    public interface OnHomeFragmentInteractionListener {
        void onWhatIsProcrastinationClicked();

        void onLinksAndVideosClicked();

        void onTipsAndTricksClicked();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);

        // Observe tasks and display the most urgent "To Do" task
        observeAndDisplayImportantTask();

        observeAndDisplayTasksStreaks();

        setOnClickListeners(view);

        // Start periodic refresh for urgent task (to update color if deadline passes)
        handler.post(urgentTaskRefreshRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(urgentTaskRefreshRunnable);
    }

    /** Initializes UI components, view models, and default display states. */
    private void initComponents(@NonNull View view) {
        // Initialize UI elements
        tvImportantTaskTitle = view.findViewById(R.id.tv_task_title);
        tvImportantTaskDescription = view.findViewById(R.id.tv_task_description);
        tvImportantTaskDueDate = view.findViewById(R.id.tv_task_due_date);
        tvImportantTaskDueTime = view.findViewById(R.id.tv_task_due_time);
        importantTaskCardView = view.findViewById(R.id.cvImportantTask);

        // Hide the notification icon for the important task card on the home screen
        View ivTaskNotification = view.findViewById(R.id.iv_task_notification);
        if (ivTaskNotification != null)
            ivTaskNotification.setVisibility(View.INVISIBLE);

        // Attach TTS listener for the important task card on Home
        View ivTaskTts = view.findViewById(R.id.iv_task_tts);
        if (ivTaskTts != null)
            ivTaskTts.setOnClickListener(v -> {
                if (currentUrgentTask == null)
                    return;
                String textToSpeak = currentUrgentTask.getTitle() + ". " + currentUrgentTask.getDescription();
                speak(getContext(), textToSpeak);
            });

        tvTasksStreak = view.findViewById(R.id.tvTasksStreak);
        tvDaysStreak = view.findViewById(R.id.tvDaysStreak);

        // Initialize streaks display
        tvTasksStreak.setText("0 tasks");
        tvDaysStreak.setText("0 days");


        // Initialize ViewModel
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        leaderboardViewModel = new ViewModelProvider(requireActivity()).get(LeaderboardViewModel.class);
    }

    /** Sets up click listeners for the informational buttons on the home screen. */
    private void setOnClickListeners(View view) {
        MaterialButton btnWhatIsProcrastination = view.findViewById(R.id.btnWhatIsProcrastination);
        MaterialButton btnLinksAndVideos = view.findViewById(R.id.btnLinksAndVideos);
        MaterialButton btnTipsAndTricks = view.findViewById(R.id.btnTipsAndTricks);

        btnWhatIsProcrastination.setOnClickListener(v -> {
            if (onHomeFragmentInteractionListener != null)
                onHomeFragmentInteractionListener.onWhatIsProcrastinationClicked();
        });

        btnLinksAndVideos.setOnClickListener(v -> {
            if (onHomeFragmentInteractionListener != null)
                onHomeFragmentInteractionListener.onLinksAndVideosClicked();
        });

        btnTipsAndTricks.setOnClickListener(v -> {
            if (onHomeFragmentInteractionListener != null)
                onHomeFragmentInteractionListener.onTipsAndTricksClicked();
        });
    }

    /**
     * Sets up observers for user streak data to ensure the UI stays updated.
     */
    private void observeAndDisplayTasksStreaks() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        displayStreaks(UID);

        leaderboardViewModel.getLiveData().observe(getViewLifecycleOwner(), leaderboardEntries -> {
            displayStreaks(UID);
        });
    }

    /** Fetches and displays the user's current task and day streaks. */
    private void displayStreaks(String UID) {
        UsersRepository.fetchUserById(UID, new UsersRepository.UserFetchCallback() {
            @Override public void onUserFetched(User user) {
                tvDaysStreak.setText(user.getDayStreak() +" days");
                tvTasksStreak.setText(user.getTaskStreak() +" tasks");
            }

            @Override public void onFetchFailed(String errorMessage) {}
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener)
            onHomeFragmentInteractionListener = (OnHomeFragmentInteractionListener) context;
        else
            throw new RuntimeException(context + " must implement OnHomeFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onHomeFragmentInteractionListener = null;
    }

    /**
     * Observes the user's tasks and displays the most urgent "To Do" task
     * based on the deadline.
     */
    private void observeAndDisplayImportantTask() {
        Log.d(TAG, "Setting up LiveData observer for important task");

        tasksViewModel.getLiveData().observe(
                getViewLifecycleOwner(), taskListsMap -> {
                    if (taskListsMap == null) {
                        Log.w(TAG, "Received null task list map");
                        return;
                    }

                    displayUrgentTask(taskListsMap);
                }
        );
    }

    /**
     * Filters and finds the most urgent task to display from the provided task map.
     */
    private void displayUrgentTask(Map<Task.TaskStatus, Tasks> taskListsMap) {
        // Get the "To Do" tasks
        var todoTasks = taskListsMap.get(Task.TaskStatus.TODO);
        Log.d(TAG, "TODO tasks count: " + (todoTasks == null ? 0 : todoTasks.visibleTasks().size()));

        if (todoTasks == null || todoTasks.visibleTasks().isEmpty()) {
            Log.d(TAG, "No TODO tasks available, showing default message");
            displayNoTaskMessage();
            return;
        }

        // Find the task with the soonest deadline
        Task mostUrgentTask = findMostUrgentTask(todoTasks.visibleTasks());
        Log.d(TAG, "Most urgent task: " + (mostUrgentTask != null ? mostUrgentTask.getTitle() : "null"));

        if (mostUrgentTask != null) {
            Log.d(
                    TAG, "Displaying task: " + mostUrgentTask.getTitle() +
                            " | Due: " + mostUrgentTask.getDueDate().getDay() + "-" +
                            mostUrgentTask.getDueDate().getMonth() + "-" +
                            mostUrgentTask.getDueDate().getYear() + " " +
                            mostUrgentTask.getDueTimeOfDay().getHour() + ":" +
                            mostUrgentTask.getDueTimeOfDay().getMinute()
            );
            displayTask(mostUrgentTask);
        } else {
            Log.d(TAG, "No urgent task found, showing default message");
            displayNoTaskMessage();
        }
    }

    /**
     * Finds the task with the soonest deadline from a list of tasks.
     * @param tasks The list of tasks to search.
     * @return The most urgent task, or null if the list is empty.
     */
    private Task findMostUrgentTask(java.util.List<Task> tasks) {
        if (tasks.isEmpty())
            return null;

        Task mostUrgent = null;
        long shortestTimeUntilDeadline = Long.MAX_VALUE;

        for (Task task : tasks) {
            // Calculate time until deadline using the same logic as Task.isDeadlineNear()
            long timeLeftUntilDeadline = task.getDueDate().calcTimeUntil() +
                    task.getDueTimeOfDay().calcTimeUntil();

            // Prioritize tasks that have reached their deadline (negative time)
            // Then prioritize by shortest time remaining
            if (mostUrgent == null || timeLeftUntilDeadline < shortestTimeUntilDeadline) {
                mostUrgent = task;
                shortestTimeUntilDeadline = timeLeftUntilDeadline;
            }
        }

        return mostUrgent;
    }

    /**
     * Populates the UI elements with details from the specified task.
     */
    @SuppressLint("DefaultLocale")
    private void displayTask(Task task) {
        this.currentUrgentTask = task;
        tvImportantTaskTitle.setText(task.getTitle());
        tvImportantTaskDescription.setText(task.getDescription());

        // Format and display due date (DD-MM-YY)
        String formattedDate = String.format(
                "%02d-%02d-%02d",
                task.getDueDate().getDay(),
                task.getDueDate().getMonth(),
                task.getDueDate().getYear() % 100
        );
        tvImportantTaskDueDate.setText(formattedDate);

        // Format and display due time (HH:MM)
        String formattedTime = String.format(
                "%02d:%02d",
                task.getDueTimeOfDay().getHour(),
                task.getDueTimeOfDay().getMinute()
        );
        tvImportantTaskDueTime.setText(formattedTime);

        // Update card background color based on deadline status
        updateTaskCardBackgroundColor(task);
    }


    /** Updates UI to indicate that no tasks are currently pending. */
    private void displayNoTaskMessage() {
        this.currentUrgentTask = null;
        tvImportantTaskTitle.setText("No Important Task");
        tvImportantTaskDescription.setText("You have no pending tasks. Great job!");
        tvImportantTaskDueDate.setText("--");
        tvImportantTaskDueTime.setText("--");

        // Reset card background color to bg_card (specific to Home)
        if (importantTaskCardView != null) {
            androidx.cardview.widget.CardView cardView = (androidx.cardview.widget.CardView) importantTaskCardView;
            int baseColor = getResources().getColor(R.color.bg_card, getContext().getTheme());
            cardView.setCardBackgroundColor(baseColor);
        }
    }

    /**
     * Updates the task card's background color based on proximity to the deadline.
     */
    private void updateTaskCardBackgroundColor(Task task) {
        if (importantTaskCardView == null)
            return;

        int colorRes = task.hasReachedDeadline() ? R.color.bg_task_card_post_deadline :
                task.isDeadlineNear() 
                        ? R.color.bg_task_card_near_deadline 
                        : R.color.bg_card; 

        int baseColor = getResources().getColor(colorRes, getContext().getTheme());
        androidx.cardview.widget.CardView cardView = (androidx.cardview.widget.CardView) importantTaskCardView;
        cardView.setCardBackgroundColor(baseColor);
    }
}
