package stop_delaying.utils.notifications_and_scheduling;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.jspecify.annotations.NonNull;

/**
 * Utility class for managing system notifications, including permission handling, 
 * channel creation, and triggering individual notifications.
 */
public class NotificationCreator {
    /**
     * Builds and displays a system notification with the specified details.
     * @param intent The intent to trigger when the notification is tapped.
     * @param context Application context.
     * @param notifId Unique identifier for the notification.
     * @param channelId The notification channel ID.
     * @param title Title text for the notification.
     * @param message Main body text for the notification.
     * @param icon Resource ID for the small icon.
     * @param priority Priority level (e.g., NotificationCompat.PRIORITY_HIGH).
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    static public void createNotification(
            Intent intent,
            Context context,
            int notifId,
            String channelId,
            String title,
            String message,
            int icon,
            int priority
    ) {
        // when clicking on the notif, the user goes to this activity.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notifId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(priority)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        var notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notifId, builder.build());
    }

    /**
     * Checks if the app has permission to post notifications. 
     * Handles differences between Android versions (API 33+ requirements).
     */
    static public boolean hasNotificationPermission(Context context) {
        // Permissions are granted by default on older versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    /**
     * Requests notification permission from the user. 
     * @param activity The activity context used to request permissions.
     */
    static public void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
    }

    /**
     * Creates a notification channel for Android O and above.
     */
    static public void createNotificationChannel(@NonNull Context context, String channelId, String channelName, int notifPriority, String channelDescription) {
        NotificationChannel channel = new NotificationChannel(
            channelId,
            channelName,
            notifPriority
        );
        channel.setDescription(channelDescription);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
    }
}
