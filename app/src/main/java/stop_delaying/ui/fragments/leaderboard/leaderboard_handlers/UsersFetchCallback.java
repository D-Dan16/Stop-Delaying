package stop_delaying.ui.fragments.leaderboard.leaderboard_handlers;

import java.util.List;

import stop_delaying.models.User;

public interface UsersFetchCallback {
    void onUsersFetched(List<User> leaderboardEntries);

    void onFetchFailed(String error);
}
