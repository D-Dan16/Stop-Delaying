package stop_delaying.models;

import android.icu.util.Calendar;

public record TimeOfDay(int hour, int minute) {
    public static TimeOfDay getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return new TimeOfDay(
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.HOUR)
        );
    }

    public long calcTimeUntil() {
        long totalTimeRemaining = 0L;
        totalTimeRemaining += (hour - getCurrentTime().hour) * 3600 * 1000L;
        totalTimeRemaining += (minute - getCurrentTime().minute) * 60 * 1000L;

        return totalTimeRemaining;
    }
}
