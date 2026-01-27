package stop_delaying.utils.notifications_and_scheduling;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.net.URISyntaxException;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        // The intent is passed as a URI string and needs to be parsed back to an Intent
        String intentUriString = getInputData().getString(NotifExtraIntentNames.EXTRA_INTENT);
        Intent intent = null;

        if (intentUriString != null) try {
            intent = Intent.parseUri(intentUriString, 0);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // TODO: Handle the case where notification permissions are not granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            return Result.failure();

        NotificationCreator.createNotification(
                intent,
                context,
                getInputData().getInt(NotifExtraIntentNames.EXTRA_NOTIFICATION_ID, 0),
                getInputData().getString(NotifExtraIntentNames.EXTRA_CHANNEL_ID),
                getInputData().getString(NotifExtraIntentNames.EXTRA_NOTIFICATION_TITLE),
                getInputData().getString(NotifExtraIntentNames.EXTRA_NOTIFICATION_DESCRIPTION),
                getInputData().getInt(NotifExtraIntentNames.EXTRA_SMALL_ICON, 0),
                getInputData().getInt(NotifExtraIntentNames.EXTRA_NOTIFICATION_PRIORITY, NotificationManager.IMPORTANCE_DEFAULT)
        );
        return Result.success();
    }
}
