package stop_delaying.models;

import android.icu.util.Calendar;

public class TimeOfDay {
    private int hour;
    private int minute;

    public TimeOfDay() {}

    public TimeOfDay(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public static TimeOfDay getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        // The original getCurrentTime had minute and hour swapped. Correcting this.
        return new TimeOfDay(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
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
