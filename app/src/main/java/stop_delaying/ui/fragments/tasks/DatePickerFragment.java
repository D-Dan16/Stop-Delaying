package stop_delaying.ui.fragments.tasks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private int dayChosen = 0;
    private int monthChosen = 0;
    private int yearChosen = 0;

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



    public void onDateSet(DatePicker view, int year, int month, int day) {
        dayChosen = day;
        monthChosen = month+1;
        yearChosen = year;

        textViewToDisplayInfo.setText(dayChosen+"/"+monthChosen+"/"+yearChosen);
    }

    public int getDayChosen() {
        return dayChosen;
    }

    public int getMonthChosen() {
        return monthChosen;
    }

    public int getYearChosen() {
        return yearChosen;
    }
}
