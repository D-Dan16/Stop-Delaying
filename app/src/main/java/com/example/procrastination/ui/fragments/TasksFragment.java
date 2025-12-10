package com.example.procrastination.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;
import com.example.procrastination.adapters.TasksViewPagerAdapter;
import com.example.procrastination.ui.activities.AddTaskActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TasksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        FloatingActionButton fabAddTask = view.findViewById(R.id.fab_add_task);

        TasksViewPagerAdapter adapter = new TasksViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("To Do");
                    break;
                case 1:
                    tab.setText("Completed");
                    break;
                case 2:
                    tab.setText("Canceled");
                    break;
            }
        }).attach();

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTaskActivity.class);
            startActivity(intent);
        });
    }
}
