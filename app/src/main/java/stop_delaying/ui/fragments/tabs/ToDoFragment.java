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

public class ToDoFragment extends Fragment {

    private TaskListAdapter adapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_to_do);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskListAdapter(taskList);
        recyclerView.setAdapter(adapter);

        loadTasks();
    }

    private void loadTasks() {
        // Create some dummy data
        taskList.add(new Task("Complete project report", "Finish the final report for the Q2 project.", new Date(), Task.TaskStatus.TODO));
        taskList.add(new Task("Schedule team meeting", "Organize a meeting to discuss the new project timeline.", new Date(), Task.TaskStatus.TODO));
        taskList.add(new Task("Buy groceries", "Milk, bread, eggs, and cheese.", new Date(), Task.TaskStatus.TODO));

        adapter.setTasks(taskList);
    }
}