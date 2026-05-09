package stop_delaying.ui.fragments.leaderboard.helpers.streak_handlers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import stop_delaying.models.User;
import stop_delaying.ui.fragments.leaderboard.helpers.leaderboard_handlers.UsersRepository;

/**
 * A background worker that runs daily to update the user's activity streak. 
 * Increments the streak if a task was completed, otherwise resets it to zero.
 */
public class DailyStreakWorker extends Worker {

    public DailyStreakWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /** @noinspection SingleStatementInBlock*/
    @NonNull
    @Override
    public Result doWork() {
        // Check if the currently logged-in user has completed at least one task today
        // This runs locally on each device for its own user

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId;

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            return Result.success();
        }
        
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        
        UsersRepository.fetchUserById(userId, new UsersRepository.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                try {
                    int currentDayStreak = user.getDayStreak();
                    boolean hasCompletedTodayATask = user.isHasCompletedTodayATask();
                    
                    int newDayStreak = hasCompletedTodayATask ? currentDayStreak + 1 : 0;
                    
                    // Update the user's streak and reset the daily flag
                    UsersRepository.updateUserDayStreak(userId, newDayStreak);
                    UsersRepository.updateUserHasCompletedTodayATask(userId, false);
                    
                    success[0] = true;
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onFetchFailed(String errorMessage) {
                latch.countDown();
            }
        });
        
        try {
            // Wait up to 30 seconds for the user data to be fetched and updated
            if (!latch.await(30, TimeUnit.SECONDS))
                return Result.retry();
            
            return success[0] ? Result.success() : Result.retry();
            
        } catch (InterruptedException e) {
            return Result.retry();
        }
    }
}
