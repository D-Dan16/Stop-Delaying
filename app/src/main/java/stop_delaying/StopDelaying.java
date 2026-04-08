package stop_delaying;

import android.app.Application;

import stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers.DailyStreakWorkerCaller;

public class StopDelaying extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DailyStreakWorkerCaller.scheduleDailyStreakWorker(this);
    }
}
