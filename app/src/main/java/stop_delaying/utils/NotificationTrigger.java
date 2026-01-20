package stop_delaying.utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class NotificationTrigger extends BroadcastReceiver {
    // --- Intent Extra Keys (Constants for Generic Alarms) ---
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_CHANNEL_ID = "channel_id";
    public static final String EXTRA_CHANNEL_NAME = "channel_name";
    public static final String EXTRA_CHANNEL_DESCRIPTION = "channel_description";
    public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";
    public static final String EXTRA_NOTIFICATION_DESCRIPTION = "notification_description";
    public static final String EXTRA_SMALL_ICON = "small_icon";
    public static final String EXTRA_NOTIFICATION_PRIORITY = "notification_priority";
    public static final String EXTRA_TAP_ACTION_INTENT = "tap_action_intent";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCreator.createNotification(
            intent,
            context,
            intent.getStringExtra(EXTRA_CHANNEL_ID),
            intent.getStringExtra(EXTRA_NOTIFICATION_TITLE),
            intent.getStringExtra(EXTRA_NOTIFICATION_DESCRIPTION),
            intent.getIntExtra(EXTRA_SMALL_ICON, 0),
            intent.getIntExtra(EXTRA_NOTIFICATION_PRIORITY, NotificationManager.IMPORTANCE_DEFAULT)
        );
    }
}
