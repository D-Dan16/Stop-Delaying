package stop_delaying.ui.fragments.tasks.tabs;

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
import stop_delaying.models.Date;
import stop_delaying.models.Task;
import stop_delaying.models.TimeOfDay;
import stop_delaying.ui.fragments.tasks.TasksFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab fragment that shows tasks with status `To Do`.
 * <p>
 * Integrates with the parent `TasksFragment` inline selection toolbar (inline CAB):
 * - Long-press on a card starts selection via `TaskListAdapter.OnStartSelectionListener`.
 * - While selecting, updates the toolbar subtitle with the current selection count.
 * - On action (move/delete), updates local list and delegates cross-tab moves, then clears selection and hides the bar.
 */
public class TasksToDoFragment extends Fragment {
    private static final TaskListAdapter adapter = new TaskListAdapter(new ArrayList<>());
    public static List<Task> getTaskList() {
        return adapter.getVisibleTasks();
    }

    public static TaskListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_tasks_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_to_do);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnStartSelectionListener(() -> {
            TasksFragment parent = (TasksFragment) getParentFragment();
            if (parent == null) return;

            parent.showSelectionBar(adapter.getSelectedCount(), new SelectionActionHandler() {
                @Override
                public TaskListAdapter adapter() { return adapter; }
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

        loadDummyTasks();

        recyclerView.setAdapter(adapter);
    }

    public static void addTaskFromUser(Task task) {
        getTaskList().add(task);
        adapter.notifyDataSetChanged();
    }

    public static void addTasks(List<Task> tasks) {
        getTaskList().addAll(tasks);
        adapter.notifyDataSetChanged();
    }

    private static void loadDummyTasks() {
        List<Task> taskList = getTaskList();
        if (!taskList.isEmpty()) {
            return;
        }
        // Create some dummy data
        taskList.add(new Task("Complete 1", "Finish the final report for the Q2 project.", new stop_delaying.models.Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));
        taskList.add(new Task("Complete 2", "Finish the final report for the Q2 project.", new stop_delaying.models.Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));
        taskList.add(new Task("Complete 3", "Finish the final report for the Q2 project.", new stop_delaying.models.Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));
        taskList.add(new Task("Complete 4", "Finish the final report for the Q2 project.", new stop_delaying.models.Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));
        taskList.add(new Task("Schedule 5", "Organize a meeting to discuss the new project timeline.", new stop_delaying.models.Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));
        taskList.add(new Task("Buy groceries 6", "Milk, bread, eggs, and cheese.", new Date(11, 4, 2026), new TimeOfDay(12, 45), Task.TaskStatus.TODO));

        adapter.setTasks(taskList);
    }
}