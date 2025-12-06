package com.example.procrastination.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.procrastination.activities.fragments.TasksFragment;
import com.example.procrastination.models.Task;

public class TasksPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 3;

    public TasksPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TasksFragment.newInstance(Task.TaskStatus.TODO);
            case 1:
                return TasksFragment.newInstance(Task.TaskStatus.COMPLETED);
            case 2:
                return TasksFragment.newInstance(Task.TaskStatus.CANCELED);
            default:
                return TasksFragment.newInstance(Task.TaskStatus.TODO);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
