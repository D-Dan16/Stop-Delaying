package stop_delaying.utils.notifications_and_scheduling;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GenericActionWorker extends Worker {

    private static final String TAG = GenericActionWorker.class.getName();
    public static final String EXTRA_SCHEDULED_INTENT_URI = "scheduled_intent_uri";

    public GenericActionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String intentUriString = getInputData().getString(EXTRA_SCHEDULED_INTENT_URI);

        if (intentUriString == null) {
            Log.e(TAG, "No intent URI found in input data.");
            return Result.failure();
        }

        try {
            Intent scheduledIntent = Intent.parseUri(intentUriString, Intent.URI_INTENT_SCHEME);
            PackageManager packageManager = context.getPackageManager();
            String intentUriLog = scheduledIntent.toUri(0);

            if (scheduledIntent.resolveActivity(packageManager) != null) {
                scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(scheduledIntent);
                Log.d(TAG, "Started activity from scheduled intent: " + intentUriLog);
            } else if (context.getPackageManager().resolveService(scheduledIntent, 0) != null) {
                context.startService(scheduledIntent);
                Log.d(TAG, "Started service from scheduled intent: " + intentUriLog);
            } else if (!packageManager.queryBroadcastReceivers(scheduledIntent, 0).isEmpty()) {
                context.sendBroadcast(scheduledIntent);
                Log.d(TAG, "Sent broadcast from scheduled intent: " + intentUriLog);
            } else {
                Log.w(TAG, "Scheduled intent could not be resolved or started: " + intentUriLog);
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing or executing scheduled intent: " + e.getMessage());
            return Result.failure();
        }
    }
}
