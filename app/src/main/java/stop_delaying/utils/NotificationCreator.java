package stop_delaying.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jspecify.annotations.NonNull;

public class NotificationCreator {
    static public void showNotification(Intent intent, Context context, String channelId, String title, String message, int icon, int priority) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

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

    static public void createNotificationChannel(@NonNull Context context, String taskId, String taskName, int notifPriority, String description) {
        NotificationChannel channel = new NotificationChannel(
            taskId,
            taskName,
            notifPriority
        );
        channel.setDescription(description);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
