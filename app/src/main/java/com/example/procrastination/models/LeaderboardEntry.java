package com.example.procrastination.models;

public class LeaderboardEntry {
    private final int rank;
    private final String userName;
    private final int score;

    public LeaderboardEntry(int rank, String userName, int score) {
        this.rank = rank;
        this.userName = userName;
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public String getUserName() {
        return userName;
    }

    public int getScore() {
        return score;
    }
}
