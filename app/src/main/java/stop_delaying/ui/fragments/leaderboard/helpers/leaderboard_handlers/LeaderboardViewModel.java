package stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import stop_delaying.models.LeaderboardEntry;
import stop_delaying.models.User;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository.UserWithId;
import stop_delaying.ui.fragments.leaderboard.ui.tabs.LeaderboardTab;

/**
 * ViewModel responsible for managing leaderboard UI state and business logic.
 * It coordinates data flow between the repository and the UI.
 */
public class LeaderboardViewModel extends ViewModel {
    /** Map holding processed leaderboard entries for each tab type. */
    private final MutableLiveData<Map<Integer, List<LeaderboardEntry>>> _leaderboardLists = new MutableLiveData<>();
    
    /** LiveData indicating whether a leaderboard data fetch is currently in progress. */
    private final MutableLiveData<Boolean> _leaderboardLoading = new MutableLiveData<>(false);
    private static final String TAG = "LeaderboardViewModel";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable showLoadingRunnable;
    private static final long LOADING_DELAY_MS = 250;

    public LeaderboardViewModel() {
        startObservingUsers();
    }

    /**
     * Sets up a real-time listener for user data and automatically updates all leaderboard categories.
     */
    private void startObservingUsers() {
        // Create a new runnable to show loading after a delay
        showLoadingRunnable = () -> _leaderboardLoading.setValue(true);
        handler.postDelayed(showLoadingRunnable, LOADING_DELAY_MS);

        UsersRepository.fetchUsers(new UsersRepository.UsersFetchCallback() {
            @Override public void onUsersFetched(List<UserWithId> fetchedUserWithIdEntries) {
                if (showLoadingRunnable != null)
                    handler.removeCallbacks(showLoadingRunnable);

                List<User> users = fetchedUserWithIdEntries.stream()
                        .map(userWithId -> userWithId.user)
                        .collect(Collectors.toList());

                Map<Integer, List<LeaderboardEntry>> newLists = new HashMap<>();
                
                // Calculate Day Streak Leaderboard
                newLists.put(LeaderboardTab.DAY_STREAK, createRankedEntries(users, LeaderboardTab.DAY_STREAK));
                
                // Calculate Task Streak Leaderboard
                newLists.put(LeaderboardTab.TASK_STREAK, createRankedEntries(users, LeaderboardTab.TASK_STREAK));

                _leaderboardLists.setValue(newLists);
                _leaderboardLoading.setValue(false);
            }

            @Override public void onFetchFailed(String error) {
                if (showLoadingRunnable != null)
                    handler.removeCallbacks(showLoadingRunnable);
                Log.e(TAG, "Failed to fetch leaderboard: " + error);
                _leaderboardLoading.setValue(false);
            }
        });
    }

    /**
     * Helper to sort users and wrap them in ranked LeaderboardEntry objects.
     */
    private List<LeaderboardEntry> createRankedEntries(List<User> users, int type) {
        List<User> sortedUsers = new ArrayList<>(users);
        if (type == LeaderboardTab.DAY_STREAK)
            sortedUsers.sort((u1, u2) -> Integer.compare(u2.getDayStreak(), u1.getDayStreak()));
        else if (type == LeaderboardTab.TASK_STREAK)
            sortedUsers.sort((u1, u2) -> Integer.compare(u2.getTaskStreak(), u1.getTaskStreak()));

        List<LeaderboardEntry> entries = new ArrayList<>();
        for (int i = 0; i < sortedUsers.size(); i++)
            entries.add(new LeaderboardEntry(i + 1, sortedUsers.get(i)));
        return entries;
    }

    /**
     * Force a refresh of the leaderboard data.
     */
    public void refresh() {
        UsersRepository.removeUsersListener();
        startObservingUsers();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (showLoadingRunnable != null)
            handler.removeCallbacks(showLoadingRunnable);
        UsersRepository.removeUsersListener();
    }

    /** Returns the LiveData for the categorized leaderboard collection. */
    public MutableLiveData<Map<Integer, List<LeaderboardEntry>>> getLiveData() {
        return this._leaderboardLists;
    }

    /** Returns the LiveData for the leaderboard loading status. */
    public MutableLiveData<Boolean> getLeaderboardLoading() {
        return this._leaderboardLoading;
    }
}
