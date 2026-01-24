package stop_delaying.models;

import android.icu.util.Calendar;

public record Date(int day, int month, int year) {
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return new Date(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        );
    }
    public long calcTimeUntil() {
        long totalTimeRemaining = 0L;
        totalTimeRemaining += (year - getToday().year) * 31536000 *1000L;
        totalTimeRemaining += (month - getToday().month) * 2592000 *1000L;
        totalTimeRemaining += (day - getToday().day) * 86400 *1000L;

        return totalTimeRemaining;
    }
}
