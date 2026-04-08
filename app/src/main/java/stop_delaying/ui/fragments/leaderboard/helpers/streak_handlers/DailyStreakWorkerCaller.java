package stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import lombok.experimental.UtilityClass;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/** @noinspection deprecation*/
@UtilityClass
public class DailyStreakWorkerCaller {
    private static final String WORK_TAG = "daily_streak_check";
    private static final String APP_SHARED_PREFS = "app_prefs";
    private static final String DAILY_STREAK_WORKER_SCHEDULED = "daily_streak_worker_scheduled";
    
    /**
     * Schedule the daily streak worker to run once per day at midnight.
     * This checks the currently logged-in user on THIS device only.
     * Uses SharedPreferences to ensure scheduling happens only ONCE per device installation.
     * @noinspection deprecation
     */
    public static void scheduleDailyStreakWorker(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        
        // Check if already scheduled on this device
        if (prefs.getBoolean(DAILY_STREAK_WORKER_SCHEDULED, false))
            return; // Already scheduled - no need to call WorkManager again
        
        Calendar now = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        if (midnight.before(now))
            midnight.add(Calendar.DAY_OF_MONTH, 1); // Schedule for next midnight

        long initialDelay = midnight.getTimeInMillis() - now.getTimeInMillis();
        
        PeriodicWorkRequest dailyStreakWorkRequest = new PeriodicWorkRequest.Builder(DailyStreakWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build();

        // Use REPLACE to ensure proper setup on first launch
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE ,
                dailyStreakWorkRequest
        );
        
        // Mark as scheduled in SharedPreferences
        prefs.edit().putBoolean(DAILY_STREAK_WORKER_SCHEDULED, true).apply();
    }
    
    /**
     * Cancel the daily streak worker (for testing or manual control).
     */
    public static void cancelDailyStreakWorker(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
        
        SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(DAILY_STREAK_WORKER_SCHEDULED).apply();
    }
    
    /**
     * Check if the worker is currently scheduled (for debugging).
     */
    public static boolean isWorkerScheduled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(DAILY_STREAK_WORKER_SCHEDULED, false);
    }

}