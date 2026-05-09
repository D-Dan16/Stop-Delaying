package stop_delaying;

import android.app.Application;

import stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers.DailyStreakWorkerCaller;

/**
 * Main Application class for the Stop Delaying app. Initializes core app components, 
 * such as the daily streak worker, upon startup.
 */
public class StopDelaying extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DailyStreakWorkerCaller.scheduleDailyStreakWorker(this);
    }
}
