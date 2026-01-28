package stop_delaying.models;

import android.icu.util.Calendar;

public record Date(int day, int month, int year) {
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return new Date(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, // Calender's Month is 0-based, while Date's is 1-based
                calendar.get(Calendar.YEAR)
        );
    }
    public long calcTimeUntil() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.YEAR, year);
        target.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        target.set(Calendar.DAY_OF_MONTH, day);

        return (target.getTimeInMillis() - now.getTimeInMillis()) / 1000; // convert to seconds
    }
}
