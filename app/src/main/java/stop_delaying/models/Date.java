package stop_delaying.models;

import android.icu.util.Calendar;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date() {}

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return new Date(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
    }

    public long calcTimeUntil() {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.YEAR, year);
        target.set(Calendar.MONTH, month - 1);
        target.set(Calendar.DAY_OF_MONTH, day);

        return (target.getTimeInMillis() - now.getTimeInMillis()) / 1000;
    }
}
