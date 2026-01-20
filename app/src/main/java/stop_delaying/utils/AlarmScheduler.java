package stop_delaying.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmScheduler {
    private static final String TAG = "AlarmScheduler";
    public static boolean canSchedule(Context context) {
        return context.getSystemService(Context.ALARM_SERVICE) != null;
    }

    public static boolean scheduleNotificationAlarm(
            Context context,
            long triggerAtMillis,
            int requestCode, // Unique ID for this alarm
            String channelId,
            String channelName,
            String channelDescription,
            String notificationTitle,
            String notificationDescription,
            int smallIconResource,
            int notificationPriority,
            Intent tapActionIntent
    ) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr == null) {
            Log.e(TAG, "Alarm service not available.");
            Toast.makeText(context, "Alarm service not available.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (triggerAtMillis <= System.currentTimeMillis()) {
            Toast.makeText(context, "Cannot set reminder for a time in the past.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 1. Create the base Intent for the BroadcastReceiver
        Intent receiverIntent = new Intent(context, NotificationTrigger.class);

        // 2. Add all generic notification details as extras
        receiverIntent.putExtra(NotificationTrigger.EXTRA_NOTIFICATION_ID, requestCode);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_CHANNEL_ID, channelId);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_CHANNEL_NAME, channelName);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_CHANNEL_DESCRIPTION, channelDescription);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_NOTIFICATION_TITLE, notificationTitle);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_NOTIFICATION_DESCRIPTION, notificationDescription);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_SMALL_ICON, smallIconResource);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_NOTIFICATION_PRIORITY, notificationPriority);
        receiverIntent.putExtra(NotificationTrigger.EXTRA_TAP_ACTION_INTENT, tapActionIntent);

        // 3. Create the PendingIntent for the AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 4. Schedule the alarm
        alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        Log.d(TAG, "Alarm scheduled (ReqCode: " + requestCode + ", Time: " + triggerAtMillis + ", Title: " + notificationTitle + ")");
        return true;
    }

    /**
     * Cancels a previously scheduled notification alarm.
     *
     * @param context The context.
     * @param requestCode The unique integer ID that was used to schedule the alarm.
     * @return true if an alarm was cancelled, false otherwise.
     * @noinspection UnusedReturnValue
     */
    public static boolean cancelNotificationAlarm(Context context, int requestCode) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr == null) {
            Log.e(TAG, "Alarm service not available for cancellation.");
            return false;
        }

        // It's crucial that the Intent used for cancellation exactly matches the one used for setting the alarm,
        // especially the component (class) and the requestCode. Extras typically don't need to match for cancellation,
        // but for safety and clarity, recreate the target intent.
        Intent receiverIntent = new Intent(context, NotificationTrigger.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode, // Use the unique request code to identify the alarm to cancel
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Flags must match those used during scheduling
        );

        if (pendingIntent != null) {
            alarmMgr.cancel(pendingIntent);
            Log.d(TAG, "Alarm cancelled (ReqCode: " + requestCode + ")");
            return true;
        } else {
            Log.d(TAG, "No alarm found to cancel for request code: " + requestCode);
            return false;
        }
    }
}