package stop_delaying.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.procrastination.R;
import stop_delaying.adapters.TasksViewPagerAdapter;
import stop_delaying.utils.Utils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TasksFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton fabMainToggle;
    FloatingActionButton fabAddTask;
    FloatingActionButton fabSearchTask;
    FloatingActionButton fabAiAnalyze;
    FloatingActionButton fabOrderBy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        fabMainToggle = view.findViewById(R.id.fab_main_toggle);
        fabAddTask = view.findViewById(R.id.fab_add_task);
        fabSearchTask = view.findViewById(R.id.fab_search_task);
        fabAiAnalyze = view.findViewById(R.id.fab_ai_analyze);
        fabOrderBy = view.findViewById(R.id.fab_order_by);

        TasksViewPagerAdapter adapter = new TasksViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("To Do");
                case 1 -> tab.setText("Completed");
                case 2 -> tab.setText("Canceled");
            }
        }).attach();

        registerActionButtons();
    }

    private void registerActionButtons() {
        fabMainToggle.setOnClickListener(v -> {
            if (!fabAddTask.isShown()) {
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
        });


        fabAddTask.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_add_task_popup));

        fabSearchTask.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_search_task_popup));

        fabAiAnalyze.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_search_ai_analyze));

        fabOrderBy.setOnClickListener(v -> Utils.showPopup(requireView(), requireContext(), R.layout.cv_order_tasks_popup));
    }

}