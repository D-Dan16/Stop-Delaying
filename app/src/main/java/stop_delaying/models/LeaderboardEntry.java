package stop_delaying.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LeaderboardEntry {
    private int rank;
    private User user;

    public LeaderboardEntry(int rank, User user) {
        this.rank = rank;
        this.user = user;
    }

    public int getDayStreak() {
        return user.getDayStreak();
    }

    public int getTaskStreak() {
        return user.getTaskStreak();
    }

    public String getUserName() {
        return user.getUserName();
    }
}
