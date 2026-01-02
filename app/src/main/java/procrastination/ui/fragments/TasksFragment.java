package procrastination.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;
import procrastination.adapters.TasksViewPagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TasksFragment extends Fragment {
    boolean areFABsShown = false;

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
        FloatingActionButton fabMainToggle = view.findViewById(R.id.fab_main_toggle);
        FloatingActionButton fabAddTask = view.findViewById(R.id.fab_add_task);
        FloatingActionButton fabSearchTask = view.findViewById(R.id.fab_search_task);
        FloatingActionButton fabAiAnalyze = view.findViewById(R.id.fab_ai_anaylze);
        FloatingActionButton fabOrderBy = view.findViewById(R.id.fab_order_by);

        TasksViewPagerAdapter adapter = new TasksViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("To Do");
                case 1 -> tab.setText("Completed");
                case 2 -> tab.setText("Canceled");
            }
        }).attach();

        fabMainToggle.setOnClickListener((view1) -> {
            if (!areFABsShown) {
                fabAddTask.show();
                fabSearchTask.show();
                fabAiAnalyze.show();
                fabOrderBy.show();
            } else {
                fabAddTask.hide();
                fabSearchTask.hide();
                fabAiAnalyze.hide();
                fabOrderBy.hide();
            }

            areFABsShown = !areFABsShown;
        });

    }
}
