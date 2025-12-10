package com.example.procrastination.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.procrastination.R;
import com.example.procrastination.ui.fragments.HomeFragment;
import com.example.procrastination.ui.fragments.LeaderboardFragment;
import com.example.procrastination.ui.fragments.LinksFragment;
import com.example.procrastination.ui.fragments.ProcrastinationFragment;
import com.example.procrastination.ui.fragments.SettingsFragment;
import com.example.procrastination.ui.fragments.TasksFragment;
import com.example.procrastination.ui.fragments.TipsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainApp extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    TasksFragment tasksFragment = new TasksFragment();
    LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    ProcrastinationFragment procrastinationFragment = new ProcrastinationFragment();
    LinksFragment linksFragment = new LinksFragment();
    TipsFragment tipsFragment = new TipsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        replaceFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    replaceFragment(homeFragment);
                    return true;
                } else if (item.getItemId() == R.id.nav_tasks) {
                    replaceFragment(tasksFragment);
                    return true;
                } else if (item.getItemId() == R.id.nav_leaderboard) {
                    replaceFragment(leaderboardFragment);
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    replaceFragment(settingsFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onWhatIsProcrastinationClicked() {
        replaceFragment(procrastinationFragment);
    }

    @Override
    public void onLinksAndVideosClicked() {
        replaceFragment(linksFragment);
    }

    @Override
    public void onTipsAndTricksClicked() {
        replaceFragment(tipsFragment);
    }
}