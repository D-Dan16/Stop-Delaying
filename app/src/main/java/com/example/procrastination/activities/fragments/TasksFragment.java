package com.example.procrastination.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.procrastination.R;
import com.example.procrastination.adapters.TaskListAdapter;
import com.example.procrastination.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private static final String ARG_TASK_STATUS = "task_status";
    private Task.TaskStatus taskStatus;
    private TaskListAdapter adapter;
    private List<Task> taskList;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance(Task.TaskStatus status) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskStatus = (Task.TaskStatus) getArguments().getSerializable(ARG_TASK_STATUS);
        }
        // Initialize taskList here (e.g., fetch from a database or a dummy list)
        taskList = new ArrayList<>();
        // For demonstration, add some dummy tasks based on status
        addDummyTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskListAdapter(taskList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void addDummyTasks() {
        // Example dummy tasks for each status
        if (taskStatus == Task.TaskStatus.TODO) {
            taskList.add(new Task("Finish Project Proposal", "Complete the project proposal document and submit.", new java.util.Date(), Task.TaskStatus.TODO));
            taskList.add(new Task("Buy Groceries", "Milk, Eggs, Bread, and Fruits.", new java.util.Date(), Task.TaskStatus.TODO));
        } else if (taskStatus == Task.TaskStatus.COMPLETED) {
            taskList.add(new Task("Walk the Dog", "Took the dog for a 30-minute walk.", new java.util.Date(), Task.TaskStatus.COMPLETED));
        } else if (taskStatus == Task.TaskStatus.CANCELED) {
            taskList.add(new Task("Meeting with John", "Meeting was canceled due to scheduling conflict.", new java.util.Date(), Task.TaskStatus.CANCELED));
        }
    }

    // Method to update tasks in the fragment
    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        if (adapter != null) {
            adapter.setTasks(newTasks);
        }
    }
}
