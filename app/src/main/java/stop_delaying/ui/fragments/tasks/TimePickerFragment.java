package stop_delaying.ui.fragments.tasks;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import lombok.Getter;

/**
 * A DialogFragment that displays a system time picker. Captures the user's selected 
 * time and updates a target TextView with the formatted result.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    /** The hour of the day chosen by the user (0-23). */
    @Getter
    private int hourChosen;
    /** The minute chosen by the user (0-59). */
    @Getter
    private int minuteChosen;

    /** Target TextView to display the formatted time selection. */
    private final TextView textViewToDisplayInfo;

    public <TV extends TextView> TimePickerFragment(TV textViewToDisplayInfo) {
        this.textViewToDisplayInfo = textViewToDisplayInfo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * Callback for when the user selects a time from the picker.
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourChosen = hourOfDay;
        minuteChosen = minute;

        textViewToDisplayInfo.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
    }

}
