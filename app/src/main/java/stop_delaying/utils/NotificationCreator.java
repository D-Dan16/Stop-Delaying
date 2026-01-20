package stop_delaying.utils;

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

public class NotificationCreator {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    static public void createNotification(Intent intent, Context context, String channelId, String title, String message, int icon, int priority) {
        // when clicking on the notif, the user goes to this activity.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
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

        // we randomise the id so each notification is unique and there will be no overrides.
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Checks if the app has permission to post notifications.
     * On Android 13+ (API 33), it explicitly checks for POST_NOTIFICATIONS.
     */
    static public boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Permissions are granted by default on older versions
            return true;
        }
    }

    /**
     * Requests the notification permission.
     * Note: The context passed must be an Activity.
     */
    static public void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    101
            );
        }
    }

    static public void createNotificationChannel(@NonNull Context context, String channelId, String channelName, int notifPriority, String channelDescription) {
        NotificationChannel channel = new NotificationChannel(
            channelId,
            channelName,
            notifPriority
        );
        channel.setDescription(channelDescription);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
