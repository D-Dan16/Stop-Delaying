package stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import stop_delaying.models.LeaderboardEntry;
import stop_delaying.models.User;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository.UserWithId;
import stop_delaying.ui.fragments.leaderboard.ui.tabs.LeaderboardTab;

/**
 * ViewModel responsible for managing leaderboard UI state and business logic.
 * It holds leaderboard entries, handles filtering, and coordinates data flow between the UI and LeaderboardRepository.
 */
public class LeaderboardViewModel extends ViewModel {
    /// The data structure holding leaderboard entries for UI display
    private final MutableLiveData<List<LeaderboardEntry>> _leaderboardEntries = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _leaderboardLoading = new MutableLiveData<>();
    private static final String TAG = "LeaderboardViewModel";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable showLoadingRunnable;
    private static final long LOADING_DELAY_MS = 250;

    /// Being called by the ViewModelProvider
    public LeaderboardViewModel() {
        _leaderboardLoading.setValue(false); // Initialize to not loading
    }

    public void organizeLeaderboardEntries(int leaderboardType) {
        // Cancel any pending show loading runnable if a new request comes in
        if (showLoadingRunnable != null)
            handler.removeCallbacks(showLoadingRunnable);

        // Create a new runnable to show loading after a delay
        showLoadingRunnable = () -> _leaderboardLoading.setValue(true);
        handler.postDelayed(showLoadingRunnable, LOADING_DELAY_MS);

        UsersRepository.fetchUsers(new UsersRepository.UsersFetchCallback() {
            @Override public void onUsersFetched(List<UserWithId> fetchedUserWithIdEntries) {
                // If data is fetched quickly, cancel the loading runnable to avoid showing the progress bar
                if (showLoadingRunnable != null)
                    handler.removeCallbacks(showLoadingRunnable);

                List<User> fetchedUserEntries = fetchedUserWithIdEntries.stream()
                        .map(userWithId -> userWithId.user)
                        .collect(Collectors.toList());

                ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

                // Sort the fetched entries based on the selected leaderboard type
                switch (leaderboardType) {
                    case LeaderboardTab.DAY_STREAK -> fetchedUserEntries.sort((u1, u2) -> u2.getDayStreak() - u1.getDayStreak());
                    case LeaderboardTab.TASK_STREAK -> fetchedUserEntries.sort((u1, u2) -> u2.getTaskStreak() - u1.getTaskStreak());
                }

                // create the leaderboard entries.
                for (int rank = 1; rank <= fetchedUserEntries.size(); rank++)
                    leaderboardEntries.add(new LeaderboardEntry(rank, fetchedUserEntries.get(rank-1)));

                _leaderboardEntries.setValue(leaderboardEntries);
                _leaderboardLoading.setValue(false); // Set to not loading after data is fetched
            }
            @Override public void onFetchFailed(String error) {
                // If fetch fails, cancel the loading runnable and set loading to false
                if (showLoadingRunnable != null)
                    handler.removeCallbacks(showLoadingRunnable);
                Log.e(TAG, "Failed to fetch leaderboard: " + error);
                _leaderboardLoading.setValue(false); // Set to not loading even on failure
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks when the ViewModel is cleared
        if (showLoadingRunnable != null)
            handler.removeCallbacks(showLoadingRunnable);
        UsersRepository.removeUsersListener();
    }

    /**
     * Returns the LiveData for all leaderboard entries.
     */
    public MutableLiveData<List<LeaderboardEntry>> getLiveData() {
        return this._leaderboardEntries;
    }

    /**
     * Returns the LiveData for the leaderboard loading status.
     */
    public MutableLiveData<Boolean> getLeaderboardLoading() {
        return this._leaderboardLoading;
    }

}