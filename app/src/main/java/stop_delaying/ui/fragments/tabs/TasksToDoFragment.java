package stop_delaying.ui.fragments.tabs;

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
import java.util.Date;
import java.util.List;

public class TasksToDoFragment extends Fragment {

    private static TaskListAdapter adapter;
    private static List<Task> taskList = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_to_do);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskListAdapter(taskList);
        recyclerView.setAdapter(adapter);

    }

    public static List<Task> getTaskList() {
        return taskList;
    }

    public static void setTaskList(List<Task> taskList) {
        TasksToDoFragment.taskList = taskList;
    }

    public static void addTask(Task task) {
        taskList.add(task);
        adapter.notifyDataSetChanged();
    }

}