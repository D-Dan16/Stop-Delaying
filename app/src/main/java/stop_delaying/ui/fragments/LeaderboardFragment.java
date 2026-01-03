package stop_delaying.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.procrastination.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import stop_delaying.ui.fragments.tabs.LeaderboardDayStreakFragment;
import stop_delaying.ui.fragments.tabs.LeaderboardTaskStreakFragment;

public class LeaderboardFragment extends Fragment {
    ViewPager2 viewPager;
    TabLayout tabLayout;

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
        viewPager = view.findViewById(R.id.leaderboard_view_pager);
        tabLayout = view.findViewById(R.id.leaderboard_tab_layout);


        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return switch (position) {
                    case 0 -> new LeaderboardTaskStreakFragment();
                    case 1 -> new LeaderboardDayStreakFragment();
                    default -> throw new IllegalStateException("Unexpected value: " + position);
                };
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("Task Streak");
                case 1 -> tab.setText("Day Streak");
            }
        }).attach();
    }
}
