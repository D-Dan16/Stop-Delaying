package stop_delaying.ui.fragments.leaderboard.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar; // Import ProgressBar
import com.example.procrastination.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.LeaderboardViewModel;
import stop_delaying.ui.fragments.leaderboard.ui.tabs.LeaderboardDayStreakFragment;
import stop_delaying.ui.fragments.leaderboard.ui.tabs.LeaderboardTaskStreakFragment;
import stop_delaying.ui.fragments.leaderboard.ui.tabs.LeaderboardTab;

/**
 * Host fragment for the leaderboard feature. Manages a ViewPager with tabs for 
 * displaying users ranked by day streaks or task streaks.
 */
public class LeaderboardFragment extends Fragment {
    /** ViewPager for horizontal navigation between different leaderboard tabs. */
    private ViewPager2 viewPager;
    /** TabLayout for selecting between day-streak and task-streak leaderboards. */
    private TabLayout tabLayout;
    /** ProgressBar shown while leaderboard data is being fetched and processed. */
    private ProgressBar leaderboardProgressBar; // Declare ProgressBar
    /** ViewModel for managing and observing leaderboard state and data. */
    private LeaderboardViewModel leaderboardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the ViewModel instance from Activity scope to share data and listeners across fragments
        leaderboardViewModel = new ViewModelProvider(requireActivity()).get(LeaderboardViewModel.class);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.leaderboard_view_pager);
        tabLayout = view.findViewById(R.id.leaderboard_tab_layout);
        leaderboardProgressBar = view.findViewById(R.id.leaderboard_progress_bar);


        createTabLayoutLogic();

        // Observe Leaderboard data from ViewModel and update fragments
        setupLeaderboardObservers();

        // init leaderboard data
        leaderboardViewModel.organizeLeaderboardEntries(LeaderboardTab.DAY_STREAK);
    }

    /**
     * Configures LiveData observers for leaderboard entries and loading status.
     */
    private void setupLeaderboardObservers() {
        leaderboardViewModel.getLiveData().observe(getViewLifecycleOwner(), leaderboardEntries -> {
            if (leaderboardEntries == null)
                return;

            LeaderboardDayStreakFragment.getAdapter().setLeaderboardEntries(leaderboardEntries);
            LeaderboardTaskStreakFragment.getAdapter().setLeaderboardEntries(leaderboardEntries);
        });

        // Observe leaderboard loading progress from ViewModel
        leaderboardViewModel.getLeaderboardLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                leaderboardProgressBar.setVisibility(View.VISIBLE);
                leaderboardProgressBar.setProgress(0);
                leaderboardProgressBar.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.progress_bar_animation));
            } else {
                leaderboardProgressBar.clearAnimation();
                leaderboardProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Sets up the ViewPager and TabLayout integration, defining tab titles and 
     * selection behavior.
     */
    private void createTabLayoutLogic() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override public int getItemCount() {
                return 2;
            }

            @NonNull @Override public Fragment createFragment(int position) {
                return switch (position) {
                    case LeaderboardTab.DAY_STREAK -> new LeaderboardDayStreakFragment();
                    case LeaderboardTab.TASK_STREAK -> new LeaderboardTaskStreakFragment();
                    default -> throw new IllegalStateException("Unexpected value: " + position);
                };
            }
        });

        /// Initialize the tab layout
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case LeaderboardTab.DAY_STREAK -> tab.setText("Day Streak");
                case LeaderboardTab.TASK_STREAK -> tab.setText("Task Streak");
            }
        }).attach();

        /// Add a listener for tab selection changes
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                // When a tab is selected, organize the leaderboard entries accordingly
                switch (tab.getPosition()) {
                    case LeaderboardTab.DAY_STREAK -> leaderboardViewModel.organizeLeaderboardEntries(LeaderboardTab.DAY_STREAK);
                    case LeaderboardTab.TASK_STREAK -> leaderboardViewModel.organizeLeaderboardEntries(LeaderboardTab.TASK_STREAK);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
