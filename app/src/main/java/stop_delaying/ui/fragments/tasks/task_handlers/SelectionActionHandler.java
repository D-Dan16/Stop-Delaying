package stop_delaying.ui.fragments.tasks.task_handlers;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import stop_delaying.ui.fragments.tasks.tabs.TasksCanceledFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksCompletedFragment;
import stop_delaying.ui.fragments.tasks.tabs.TasksToDoFragment;
import stop_delaying.models.Task;
import stop_delaying.ui.fragments.tasks.TasksFragment;

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
     * Default move implementation: removes currently selected from the local list,
     * updates their status, appends them to the target tab list, clears selection,
     * and hides the selection bar.
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

        adapter.removeSelectedTasks();
        for (Task t : selected) {
            t.setTaskSelected(false);
            t.setStatus(status);
        }

        var viewModel = new ViewModelProvider(parent.requireActivity()).get(TasksViewModel.class);
        
        viewModel.addAllTasks(selected);
        
        adapter.clearSelection();
        parent.hideSelectionBar();
    }

    /**
     * Default delete implementation: removes selected from local list, clears selection,
     * and hides the selection bar.
     */
    default void onDelete() {
        TaskListAdapter adapter = adapter();
        TasksFragment parent = parent();
        if (adapter == null || parent == null) return;
        adapter.removeSelectedTasks();
        adapter.clearSelection();
        parent.hideSelectionBar();
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
