package stop_delaying.ui.fragments.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;
import stop_delaying.adapters.TaskListAdapter;
import stop_delaying.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab fragment that shows tasks with status `Canceled`.
 * <p>
 * Works with the parent `TasksFragment` inline selection toolbar:
 * - Starts selection on long-press via adapter callback
 * - Updates selection count in the toolbar while selecting
 * - Executes move/delete, then clears selection and hides the bar
 */
public class TasksCanceledFragment extends Fragment {
    private static final TaskListAdapter adapter = new TaskListAdapter(new ArrayList<>());

    public static TaskListAdapter getAdapter() {
        return adapter;
    }
    public static List<Task> getTaskList() {
        return adapter.getVisibleTasks();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_tasks_canceled, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_canceled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnStartSelectionListener(() -> {
            TasksFragment parent = (TasksFragment) getParentFragment();
            if (parent == null) return;

            parent.showSelectionBar(adapter.getSelectedCount(), new SelectionActionHandler() {
                @Override
                public stop_delaying.adapters.TaskListAdapter adapter() { return adapter; }
                @Override
                public TasksFragment parent() { return parent; }
            });
        });

        adapter.setOnSelectionChangeListener(selectedCount -> {
            TasksFragment parent = (TasksFragment) getParentFragment();
            if (parent != null) {
                parent.updateSelectionCount(selectedCount);
                if (selectedCount == 0) {
                    adapter.clearSelection();
                    parent.hideSelectionBar();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public static void addTasks(List<Task> tasks) {
        getTaskList().addAll(tasks);
        adapter.notifyDataSetChanged();
    }

}
