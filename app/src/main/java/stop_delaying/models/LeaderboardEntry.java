package stop_delaying.models;

public class LeaderboardEntry {
    private int rank;
    private User user;

    public LeaderboardEntry(int rank, User user) {
        this.rank = rank;
        this.user = user;
    }

    public int getRank() {
        return rank;
    }

    public User getUser() {
        return user;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setUser(User user) {
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
