package procrastination.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import procrastination.ui.fragments.tabs.CanceledFragment;
import procrastination.ui.fragments.tabs.CompletedFragment;
import procrastination.ui.fragments.tabs.ToDoFragment;

public class TasksViewPagerAdapter extends FragmentStateAdapter {

    public TasksViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ToDoFragment();
            case 1:
                return new CompletedFragment();
            case 2:
                return new CanceledFragment();
            default:
                return new ToDoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}