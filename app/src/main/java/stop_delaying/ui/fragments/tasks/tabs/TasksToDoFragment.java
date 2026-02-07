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

import java.util.ArrayList;

import stop_delaying.ui.fragments.tasks.task_handlers.Tasks;
import stop_delaying.ui.fragments.tasks.task_handlers.SelectionActionHandler;
import stop_delaying.ui.fragments.tasks.task_handlers.TaskListAdapter;
import stop_delaying.ui.fragments.tasks.TasksFragment;


/**
 * Tab fragment that shows tasks with status `To Do`.
 * <p>
 * Integrates with the parent `TasksFragment` inline selection toolbar (inline CAB):
 * - Long-press on a card starts selection via `TaskListAdapter.OnStartSelectionListener`.
 * - While selecting, updates the toolbar subtitle with the current selection count.
 * - On action (move/delete), updates local list and delegates cross-tab moves, then clears selection and hides the bar.
 */
public class TasksToDoFragment extends Fragment {
    private static final TaskListAdapter adapter = new TaskListAdapter(new Tasks(new ArrayList<>(), new ArrayList<>()));

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

        recyclerView.setAdapter(adapter);
    }
}
