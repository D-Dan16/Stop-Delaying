package stop_delaying.utils.notifications_and_scheduling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    private static final String TAG = TaskScheduler.class.getName();

    private static boolean cannotSchedule(long delay) {
        return delay < 0;
    }

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
        Log.d(TAG, "Generic action scheduled (ReqCode: " + requestCode + ", Time: " + scheduleDelay + ", Intent: " + intent.toUri(0) + ")");

        return true;
    }


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
        if (scheduleDelays.size() != listOfNotificationTitles.size()) {
            Log.e(TAG, "Number of trigger times and notification titles do not match.");
            return false;
        }

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

    private static boolean scheduleNotificationAlarm(
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
        if (cannotSchedule(scheduleDelay)) return false;

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
        Log.d(TAG, "Work scheduled (ReqCode: " + requestCode + ", Time: " + scheduleDelay + ", Title: " + notificationTitle + ")");

        return true;
    }

    public static boolean cancelNotificationAlarm(Context context, int requestCode) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(requestCode));
        Log.d(TAG, "WorkManager alarm cancelled (ReqCode: " + requestCode + ")");
        return true;
    }
}
