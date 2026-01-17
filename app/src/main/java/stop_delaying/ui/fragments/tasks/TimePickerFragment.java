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

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private int hourChosen;
    private int minuteChosen;

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

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourChosen = hourOfDay;
        minuteChosen = minute;

        textViewToDisplayInfo.setText(hourOfDay+":"+minute);
    }

    public int getHourChosen() {
        return hourChosen;
    }

    public int getMinuteChosen() {
        return minuteChosen;
    }
}
