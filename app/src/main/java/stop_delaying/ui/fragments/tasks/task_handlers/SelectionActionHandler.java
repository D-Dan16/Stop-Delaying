package stop_delaying.ui.fragments.tasks.task_handlers;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.models.Task;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository;
import stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers.TaskStreakHandler;
import stop_delaying.ui.fragments.tasks.TasksFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksToDoFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksCompletedFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksCanceledFragment;
import stop_delaying.utils.notifications_and_scheduling.TaskScheduler;

/**
 * Interface defining actions for selected tasks, such as moving them between 
 * categories or deleting them. Provides default logic to maintain consistency.
 */
public interface SelectionActionHandler {
    /**
     * Retrieves the RecyclerView adapter associated with the current selection context.
     */
    TaskListAdapter adapter();

    /**
     * Retrieves the parent TasksFragment that hosts the selection UI.
     */
    TasksFragment parent();



    /**
     * Moves selected tasks to a new status. Updates status, cancels notifications 
     * if necessary, refreshes UI adapters, and saves changes to Firebase.
     * @param status The target status for the selected tasks.
     */
    default void onMoveTo(Task.TaskStatus status) {
        TaskListAdapter adapter = adapter();
        TasksFragment parent = parent();
        if (adapter == null || parent == null) return;

        List<Task> selected = new ArrayList<>(adapter.getSelectedTasks());
        if (selected.isEmpty()) {
            parent.hideSelectionBar();
            return;
        }

        // Remove from the current adapter
        adapter.removeSelectedTasks();
        adapter.clearSelection();
        parent.hideSelectionBar();

        // Update task status
        for (Task task : selected) {
            task.setTaskSelected(false);
            task.setStatus(status);

            // If moving away from 'TO-DO', cancel notifications
            if (status != Task.TaskStatus.TODO) {
                TaskScheduler.cancelNotificationAlarm(parent.requireContext(), task.getTaskId().hashCode());
                task.setTaskNotifying(false);
            }
        }

        // Add to target adapter
        TaskListAdapter targetAdapter = switch (status) {
            case TODO -> TasksToDoFragment.getAdapter();
            case COMPLETED -> TasksCompletedFragment.getAdapter();
            case CANCELED -> TasksCanceledFragment.getAdapter();
        };
        
        for (Task task : selected)
            targetAdapter.addTask(task);

        // Save to Firebase
        var viewModel = new ViewModelProvider(parent.requireActivity()).get(TasksViewModel.class);
        for (Task task : selected)
            viewModel.updateTask(task);

        /// Update streaks
        if (status == Task.TaskStatus.COMPLETED) {
            // If tasks are sent to be "completed", Figure out if streaks should be incremented or reset
            TaskStreakHandler.inspectTasks(selected);

            // Tick the "Has done a task today" tracker for Day streak maintainment.
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser != null)
                UsersRepository.updateUserHasCompletedTodayATask(fbUser.getUid(), true);

        }
    }

    /**
     * Deletes selected tasks from the application. Removes from local list, 
     * clears Firebase entries, and resets UI state.
     */
    default void onDelete() {
        TaskListAdapter adapter = adapter();
        TasksFragment parent = parent();
        if (adapter == null || parent == null) return;

        List<Task> selected = new ArrayList<>(adapter.getSelectedTasks());
        if (selected.isEmpty()) {
            parent.hideSelectionBar();
            return;
        }

        // Remove from UI
        adapter.removeSelectedTasks();
        adapter.clearSelection();
        parent.hideSelectionBar();

        // Delete from Firebase
        var viewModel = new ViewModelProvider(parent.requireActivity()).get(TasksViewModel.class);
        for (Task task : selected)
            viewModel.removeTask(task);
    }

    /**
     * Cancels the current selection session and clears all highlight states.
     */
    default void onEscape() {
        TaskListAdapter adapter = adapter();
        if (adapter != null) adapter.clearSelection();
    }

    /**
     * Listener interface for the event where a selection session begins.
     */
    interface OnStartSelectionListener {
        void onStartSelection();
    }

    /**
     * Listener interface for changes in the total number of selected items.
     */
    interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }
}
