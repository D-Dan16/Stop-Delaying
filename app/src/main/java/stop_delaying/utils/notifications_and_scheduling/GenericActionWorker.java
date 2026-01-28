package stop_delaying.utils.notifications_and_scheduling;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GenericActionWorker extends Worker {
    public static final String EXTRA_SCHEDULED_INTENT_URI = "scheduled_intent_uri";

    public GenericActionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String intentUriString = getInputData().getString(EXTRA_SCHEDULED_INTENT_URI);

        if (intentUriString == null) return Result.failure();

        try {
            Intent scheduledIntent = Intent.parseUri(intentUriString, Intent.URI_INTENT_SCHEME);
            PackageManager packageManager = context.getPackageManager();

            if (scheduledIntent.resolveActivity(packageManager) != null) {
                scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(scheduledIntent);
            } else if (context.getPackageManager().resolveService(scheduledIntent, 0) != null)
                context.startService(scheduledIntent);
            else if (!packageManager.queryBroadcastReceivers(scheduledIntent, 0).isEmpty())
                context.sendBroadcast(scheduledIntent);

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}
