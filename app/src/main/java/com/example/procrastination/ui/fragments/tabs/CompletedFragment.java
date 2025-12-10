package com.example.procrastination.ui.fragments.tabs;

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
import com.example.procrastination.adapters.TaskListAdapter;
import com.example.procrastination.models.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompletedFragment extends Fragment {

    private TaskListAdapter adapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_completed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskListAdapter(taskList);
        recyclerView.setAdapter(adapter);

        loadTasks();
    }

    private void loadTasks() {
        // Create some dummy data
        taskList.add(new Task("Review presentation slides", "Go over the slides for the marketing presentation.", new Date(), Task.TaskStatus.COMPLETED));
        taskList.add(new Task("Email John about the new design", "Send the latest mockups and ask for feedback.", new Date(), Task.TaskStatus.COMPLETED));

        adapter.setTasks(taskList);
    }
}