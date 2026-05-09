package stop_delaying.utils.notifications_and_scheduling;

import android.content.Context;
import android.content.Intent;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles the scheduling and cancellation of background tasks and notifications using WorkManager. 
 * Supports both generic intents and specific notification alerts with custom delays.
 */
public class TaskScheduler {
    private static final String TAG = TaskScheduler.class.getName();

    /** Checks if a delay is negative, which would prevent scheduling. */
    private static boolean cannotSchedule(long delay) {
        return delay < 0;
    }

    /**
     * Schedules a generic Intent to be executed after a specified delay.
     * @param context Application context.
     * @param intent The intent to execute.
     * @param scheduleDelay Delay in seconds.
     * @param requestCode Unique ID used as a work tag.
     * @return true if the work was successfully enqueued.
     */
    public static boolean schedule(
            Context context,
            Intent intent,
            long scheduleDelay,
            int requestCode
    ) {
        if (cannotSchedule(scheduleDelay)) return false;

        Data inputData = new Data.Builder()
                .putString(GenericActionWorker.EXTRA_SCHEDULED_INTENT_URI, intent.toUri(0))
                .build();

        OneTimeWorkRequest genericActionWorkRequest = new OneTimeWorkRequest.Builder(GenericActionWorker.class)
                .setInitialDelay(scheduleDelay, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag(String.valueOf(requestCode))
                .build();
        
        WorkManager.getInstance(context).enqueue(genericActionWorkRequest);

        return true;
    }


    /**
     * Schedules multiple notification alarms with varying delays and titles.
     * @return true if all alarms were processed for scheduling.
     */
    public static boolean scheduleNotificationAlarms(
            Context context,
            Intent tapActionIntent,
            List<Long> scheduleDelays,
            int requestCode,
            List<String> listOfNotificationTitles,
            String notificationDescription,
            int smallIconResource,
            int notificationPriority,
            String channelId
    ) {
        if (scheduleDelays.size() != listOfNotificationTitles.size())
            return false;

        for (int i = 0; i < scheduleDelays.size(); i++) {
            long scheduleDelay = scheduleDelays.get(i);
            String notificationTitle = listOfNotificationTitles.get(i);

            scheduleNotificationAlarm(
                    context,
                    tapActionIntent,
                    scheduleDelay,
                    requestCode,
                    notificationTitle,
                    notificationDescription,
                    smallIconResource,
                    notificationPriority,
                    channelId
            );
        }
        return true;
    }

    /**
     * Schedules a single notification alarm using WorkManager.
     */
    private static void scheduleNotificationAlarm(
            Context context,
            Intent tapActionIntent,
            long scheduleDelay, // Time in seconds till the alarm should go off
            int requestCode, // Unique ID for this alarm
            String notificationTitle,
            String notificationDescription,
            int smallIconResource,
            int notificationPriority,
            String channelId
    ) {
        if (cannotSchedule(scheduleDelay)) return;

        Data inputData = new Data.Builder()
                .putString(NotifExtraIntentNames.EXTRA_INTENT, tapActionIntent.toUri(0))
                .putInt(NotifExtraIntentNames.EXTRA_NOTIFICATION_ID, requestCode)
                .putString(NotifExtraIntentNames.EXTRA_CHANNEL_ID, channelId)
                .putString(NotifExtraIntentNames.EXTRA_NOTIFICATION_TITLE, notificationTitle)
                .putString(NotifExtraIntentNames.EXTRA_NOTIFICATION_DESCRIPTION, notificationDescription)
                .putInt(NotifExtraIntentNames.EXTRA_SMALL_ICON, smallIconResource)
                .putInt(NotifExtraIntentNames.EXTRA_NOTIFICATION_PRIORITY, notificationPriority)
                .build();

        OneTimeWorkRequest notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(scheduleDelay, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag(String.valueOf(requestCode)) // Use requestCode as a tag for cancellation
                .build();

        WorkManager.getInstance(context).enqueue(notificationWorkRequest);

    }

    /**
     * Cancels all scheduled notification alarms associated with a specific request code.
     */
    public static void cancelNotificationAlarm(Context context, int requestCode) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(requestCode));
    }
}
