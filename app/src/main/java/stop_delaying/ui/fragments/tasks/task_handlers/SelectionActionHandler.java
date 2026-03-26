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

/**
 * Callback interface used by child task list fragments to delegate selection actions
 * (move/delete) to the parent `TasksFragment` selection toolbar (inline CAB).
 * <p>
 * To eliminate duplicate implementations across tabs, this interface provides default
 * implementations for the common actions. Implementors only need to supply references
 * to their adapter and parent fragment via {@link #adapter()} and {@link #parent()}.
 */
public interface SelectionActionHandler {
    /**
     * USED FOR INNER IMPLEMENTATION
     * @return the RecyclerView adapter of the current tab.
     */
    TaskListAdapter adapter();

    /**
     * USED FOR INNER IMPLEMENTATION
     * @return the parent TasksFragment controlling the inline selection toolbar.
     */
    TasksFragment parent();



    /**
     * Default move implementation: removes from current adapter, updates Firebase,
     * and manually adds to target adapter (since each has its own TaskLists instance).
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

        // Remove from current adapter
        adapter.removeSelectedTasks();
        adapter.clearSelection();
        parent.hideSelectionBar();

        // Update task status
        for (Task task : selected) {
            task.setTaskSelected(false);
            task.setStatus(status);
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
        var viewModel = new ViewModelProvider(parent).get(TasksViewModel.class);
        for (Task task : selected)
            viewModel.updateTask(task);

        /// Update streaks
        if (status == Task.TaskStatus.COMPLETED) {
            // If tasks are sent to be "completed" , Figure out if streaks should be incremented or reset
            TaskStreakHandler.inspectTasks(selected);

            // Tick the "Has done a task today" tracker for Day streak maintainment.
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser != null)
                UsersRepository.updateUserHasCompletedTodayATask(fbUser.getUid(), true);

        }
    }

    /**
     * Default delete implementation: removes selected from local list, deletes from Firebase,
     * clears selection, and hides the selection bar.
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
        var viewModel = new ViewModelProvider(parent).get(TasksViewModel.class);
        for (Task task : selected)
            viewModel.removeTask(task);
    }

    /**
     * Default escape/cancel implementation: clear all selections in the current list.
     */
    default void onEscape() {
        TaskListAdapter adapter = adapter();
        if (adapter != null) adapter.clearSelection();
    }

    /**
     * Fired on the very first long-press that begins selection mode.
     */
    interface OnStartSelectionListener {
        void onStartSelection();
    }

    /**
     * Notifies listeners whenever the number of selected items changes.
     */
    interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }
}
