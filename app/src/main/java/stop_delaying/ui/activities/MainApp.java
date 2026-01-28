package stop_delaying.ui.activities;

import android.os.Bundle;
import android.content.Intent; // Import Intent

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.procrastination.R;
import stop_delaying.ui.fragments.home.HomeFragment;
import stop_delaying.ui.fragments.leaderboard.LeaderboardFragment;
import stop_delaying.ui.fragments.home.LinksFragment;
import stop_delaying.ui.fragments.home.ProcrastinationFragment;
import stop_delaying.ui.fragments.settings.SettingsFragment;
import stop_delaying.ui.fragments.tasks.TasksFragment;
import stop_delaying.ui.fragments.home.TipsFragment;
import stop_delaying.ui.fragments.tasks.task_handlers.InsertCardResponsiveness;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainApp extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener {
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    TasksFragment tasksFragment;
    LeaderboardFragment leaderboardFragment;
    SettingsFragment settingsFragment;
    ProcrastinationFragment procrastinationFragment;
    LinksFragment linksFragment;
    TipsFragment tipsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_app);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        homeFragment = new HomeFragment();
        tasksFragment = new TasksFragment();
        leaderboardFragment = new LeaderboardFragment();
        settingsFragment = new SettingsFragment();
        procrastinationFragment = new ProcrastinationFragment();
        linksFragment = new LinksFragment();
        tipsFragment = new TipsFragment();

        selectWantedFragment(getIntent());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(homeFragment);
                return true;
            } else if (id == R.id.nav_tasks) {
                replaceFragment(tasksFragment);
                return true;
            } else if (id == R.id.nav_leaderboard) {
                replaceFragment(leaderboardFragment);
                return true;
            } else if (id == R.id.nav_settings) {
                replaceFragment(settingsFragment);
                return true;
            }
            return false;
        });
    }

    @Override protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        selectWantedFragment(intent);
    }

    private void selectWantedFragment(@NonNull Intent intent) {
        // Check for intent extras to determine which fragment to load
        // Cur use: for tapping on notifications, load the tasks fragment that is hold as a reference in the intent
        if (!intent.hasExtra(InsertCardResponsiveness.EXTRA_FRAGMENT_TO_LOAD)) {
            replaceFragment(homeFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            String fragmentToLoad = intent.getStringExtra(InsertCardResponsiveness.EXTRA_FRAGMENT_TO_LOAD);
            //noinspection SwitchStatementWithTooFewBranches
            switch (Objects.requireNonNull(fragmentToLoad)) {
                case TasksFragment.NAME -> {
                    replaceFragment(tasksFragment);
                    bottomNavigationView.setSelectedItemId(R.id.nav_tasks);
                }
                default -> throw new IllegalStateException("Unexpected value: " + fragmentToLoad);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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