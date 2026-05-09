package stop_delaying.ui.fragments.tasks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import lombok.Getter;

/**
 * A DialogFragment that displays a system date picker. Captures the user's selected 
 * date and updates a target TextView with the formatted result.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    /** The day of the month chosen by the user. */
    @Getter
    private int dayChosen = 0;
    /** The month chosen by the user (1-indexed). */
    @Getter
    private int monthChosen = 0;
    /** The year chosen by the user. */
    @Getter
    private int yearChosen = 0;

    /** Target TextView to display the formatted date selection. */
    private final TextView textViewToDisplayInfo;

    public <TV extends TextView> DatePickerFragment(TV textViewToDisplayInfo) {
        this.textViewToDisplayInfo = textViewToDisplayInfo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }



    /**
     * Callback for when the user selects a date from the picker.
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dayChosen = day;
        monthChosen = month+1;
        yearChosen = year;

        textViewToDisplayInfo.setText(String.format(Locale.getDefault(), "%02d-%02d-%02d", dayChosen, monthChosen, yearChosen % 100));
    }

}
