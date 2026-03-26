package stop_delaying.models;

import android.icu.util.Calendar;

import androidx.annotation.IntRange;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class TimeOfDay {
    private int hour;
    private int minute;

    public TimeOfDay() {}

    public TimeOfDay(
            @IntRange(from = 0, to = 23) int hour,
            @IntRange(from = 0, to = 59) int minute
    ) {
        this.hour = hour;
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
