package stop_delaying.models;

import android.icu.util.Calendar;

public record TimeOfDay(int hour, int minute) {
    public static TimeOfDay getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return new TimeOfDay(
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.HOUR_OF_DAY)
        );
    }

    public long calcTimeUntil() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);

        return (target.getTimeInMillis() - now.getTimeInMillis()) / 1000; // convert to seconds
    }
}
